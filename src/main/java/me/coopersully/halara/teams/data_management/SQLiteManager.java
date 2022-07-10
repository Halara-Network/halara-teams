package me.coopersully.halara.teams.data_management;

import me.coopersully.halara.teams.HalaraTeams;
import me.coopersully.halara.teams.data_management.config.ConfigMain;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SQLiteManager {

    private static Connection conn;
    private static String url;

    public static void createNewDatabase(String fileName) {

        // Ensure plugin folder exists
        var pluginFolder = HalaraTeams.getPlugin().getDataFolder();
        pluginFolder.mkdir();

        // Create database
        url = "jdbc:sqlite:" + pluginFolder + "\\" + fileName;
        if (ConfigMain.isDebug()) System.out.println("Creating database in \"" + url + "\"");
        try {
            Connection conn = DriverManager.getConnection(url);
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                if (ConfigMain.isDebug()) {
                    System.out.println(ChatColor.GREEN + "The driver name is " + meta.getDriverName());
                    System.out.println(ChatColor.GREEN + "A new database has been created.");
                }
            }
        } catch (SQLException e) {
            System.out.println(ChatColor.RED + "Failed to create new database.");
            System.out.println(ChatColor.RED + e.getMessage());
        }
    }

    public static void connect() {
        conn = null;
        try {
            // Create a connection to the database
            conn = DriverManager.getConnection(url);
            if (ConfigMain.isDebug()) System.out.println(ChatColor.GREEN +  "Connection to SQLite has been established.");
        } catch (SQLException e) {
            System.out.println(ChatColor.RED + "Failed to establish SQLite connection.");
            System.out.println(ChatColor.RED + e.getMessage());
        }
    }

    public static void disconnect() {
        if (conn != null) {
            try {
                conn.close();
                if (ConfigMain.isDebug()) System.out.println(ChatColor.GREEN +  "Connection to SQLite has been closed.");
            } catch (SQLException e) {
                System.out.println(ChatColor.RED +  "Failed to close connection to SQLite.");
                throw new RuntimeException(e);
            }
        }
    }

    private static boolean performStatement(String sql) {
        try {
            Statement statement = conn.createStatement();
            return statement.execute(sql);
        } catch (SQLException e) {
            System.out.println(ChatColor.RED + "Failed to perform statement \"" + sql + "\"");
            throw new RuntimeException(e);
        }
    }

    private static boolean performStatementUpdate(String sql) {
        try {
            Statement statement = conn.createStatement();
            statement.executeUpdate(sql);
            return true;
        } catch (SQLException e) {
            System.out.println(ChatColor.RED + "Failed to perform statement \"" + sql + "\"");
            System.out.println(ChatColor.RED + e.getMessage());
            return false;
        }
    }

    private static ResultSet performStatementQuery(String sql) {
        try {
            Statement statement = conn.createStatement();
            return statement.executeQuery(sql);
        } catch (SQLException e) {
            System.out.println(ChatColor.RED + "Failed to perform statement \"" + sql + "\"");
            throw new RuntimeException(e);
        }
    }

    public static void createTeamsTable() {
        performStatementUpdate("CREATE TABLE IF NOT EXISTS teams ( id INTEGER, name TEXT, date_created TEXT, members TEXT )");
        if (ConfigMain.isDebug()) System.out.println(ChatColor.GREEN +  "\"teams\" table successfully created/connected.");
    }

    public static boolean createTeam(String name, Player owner) {
        if (ConfigMain.isDebug()) System.out.println("Attempting to create a new team...");

        Team team = new Team(name);
        team.addMember(owner, 100);
        var sql = "INSERT INTO teams VALUES( " + team.getId() + ", '" + team.getName() + "', '" + team.getBirthdate() + "', '" + team.getMembersJson() + "' )";

        if (ConfigMain.isDebug()) System.out.println(sql);
        return performStatementUpdate(sql);
    }

    public static ResultSet getTeams() {
        return performStatementQuery("SELECT * FROM teams");
    }

    public static ResultSet getTeamsByID(int id) {
        return performStatementQuery("SELECT * FROM teams WHERE id = " + id);
    }

    public static ResultSet getTeamsByApproximateName(String name) {
        return performStatementQuery("SELECT * FROM teams WHERE name LIKE '%" + name + "%'");
    }


    /* Takes in a ResultSet containing values of multiple teams
    and returns a list of corresponding Team objects. */
    public static @NotNull List<Team> convertResultSetToTeams(@NotNull ResultSet resultSet) {
        try {
            List<Team> teams = new ArrayList<>();
            while (resultSet.next()) {
                int id = 0;
                String name = "";
                long date = 0;

                for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                    switch (i) {
                        case 1 -> id = resultSet.getInt(i);
                        case 2 -> name = resultSet.getString(i);
                        case 3 -> date = resultSet.getLong(i);
                    }
                }
                teams.add(new Team(id, name, date));
            }
            return teams;
        } catch (SQLException e) {
            System.out.println("An error occurred while converting a ResultSet into a Team.");
            throw new RuntimeException(e);
        }
    }

    public static int countTeamsByID(int id) {
        try {
            return performStatementQuery("SELECT COUNT(*) AS total FROM teams WHERE id = " + id).getInt("total");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isTeamIDTaken(int id) {
        var fetchSize = countTeamsByID(id);
        if (ConfigMain.isDebug()) System.out.println(fetchSize + " teams found with an ID of " + id);
        return fetchSize != 0;
    }

//    public static TeamMember getMemberFromTeam(int teamID, UUID memberUUID) {
//
//        String members;
//        try {
//            members = getTeamsByID(teamID).getString(4);
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//        JSONObject jsonObject = new JSONObject(members);
//        for (var member : jsonObject.getJSONArray()) {
//            if (member instanceof JSONObject memberObject) {
//                memberObject.getString()
//            }
//        }
//        return new TeamMember();
//    }

}
