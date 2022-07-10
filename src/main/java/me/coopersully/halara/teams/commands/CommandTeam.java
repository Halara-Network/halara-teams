package me.coopersully.halara.teams.commands;

import me.coopersully.halara.teams.CoreUtils;
import me.coopersully.halara.teams.data_management.PDCManager;
import me.coopersully.halara.teams.data_management.SQLiteManager;
import me.coopersully.halara.teams.data_management.Team;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

public class CommandTeam implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (args.length < 1) {
            info(sender);
            return true;
        }
        String operation = args[0].toLowerCase();

        args = Arrays.copyOfRange(args, 1, args.length);
        switch (operation) {
            case "info" -> info(sender);
            case "create", "add", "new", "make" -> create(sender, args);
            case "list", "find", "search" -> list(sender, args);
        }

        return true;
    }

    public static void info(CommandSender sender) {

        // If the command was not run by a player
        Player player = CoreUtils.checkPlayer(sender);
        if (player == null) return;

        var teamID = PDCManager.getTeamID(player);
        if (teamID == 0) {
            player.sendMessage(ChatColor.RED + "You're not apart of a team.");
            return;
        }

        try {
            var teamInfo = SQLiteManager.getTeamsByID(teamID);
            while (teamInfo.next()) {

                String id = "";
                String name = "";
                long date = 0;

                for (int i = 1; i <= teamInfo.getMetaData().getColumnCount(); i++) {
                    switch (i) {
                        case 1 -> id = teamInfo.getString(i);
                        case 2 -> name = teamInfo.getString(i);
                        case 3 -> date = teamInfo.getLong(i);
                    }
                }

                // Send packaged message & line separator
                sender.sendMessage(
                        CoreUtils.colorMessage(
                                "&7You're apart of &e" + name + " &7(#" + id + ") est. " + Team.formatDateShort(date)
                        )
                );
            }
        } catch (SQLException e) {
            sender.sendMessage(ChatColor.RED + "There was an error fetching your current team; please contact an admin.");
            throw new RuntimeException(e);
        }

    }

    public static void create(CommandSender sender, String[] args) {

        // If the command was not run by a player
        Player player = CoreUtils.checkPlayer(sender);
        if (player == null) return;

        // If there were no arguments given by the sender
        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "You must provide a name for the new team.");
        }

        String teamName = args[0];

        if (teamName.length() > 16) {
            player.sendMessage(ChatColor.RED + "The team name must be 16 characters or less.");
            return;
        }

        // Ensure team name is not already taken
        int teamId = Team.getIDFromName(teamName);
        if (SQLiteManager.isTeamIDTaken(teamId)) {
            player.sendMessage(CoreUtils.colorMessage("&cA team by the name of &e" + teamName + " &calready exists."));
            return;
        }

        // Attempt to register new team in database
        SQLiteManager.createTeam(teamName, player);
        PDCManager.setTeam(player, teamId);
        player.sendMessage(CoreUtils.colorMessage("&7Successfully created team &e" + teamName + "&7!"));

    }

    public static void list(CommandSender sender, String @NotNull [] args) {
        try {

            ResultSet resultSet;
            if (args.length != 0) {
                // If the player provided a search query
                resultSet = SQLiteManager.getTeamsByApproximateName(args[0]);
                sender.sendMessage(CoreUtils.colorMessage("&7All existing teams matching &e" + args[0] + "&7:"));
            } else {
                // If the player did not provide a search query
                resultSet = SQLiteManager.getTeams();
                sender.sendMessage(CoreUtils.colorMessage("&7All existing teams:"));
            }


            while (resultSet.next()) {

                String id = "";
                String name = "";
                long date = 0;

                for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                    switch (i) {
                        case 1 -> id = resultSet.getString(i);
                        case 2 -> name = resultSet.getString(i);
                        case 3 -> date = resultSet.getLong(i);
                    }
                }

                // Send packaged message & line separator
                sender.sendMessage(
                        CoreUtils.colorMessage(
                                "  \u2022 &e" + name + " &7(#" + id + ") est. " + Team.formatDateShort(date)
                        )
                );
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public static void leave(CommandSender sender) {

        // If the command was not run by a player
        Player player = CoreUtils.checkPlayer(sender);
        if (player == null) return;

        PDCManager.getTeamID(player);

    }

}
