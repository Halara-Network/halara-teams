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
import java.util.Arrays;
import java.util.List;

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

    /* The default subcommand.
    Returns information about the player's current team,
    or an error message if they are not currently in a team. */
    public static void info(CommandSender sender) {

        // If the command was not run by a player
        Player player = CoreUtils.checkPlayer(sender);
        if (player == null) return;

        var teamID = PDCManager.getTeamID(player);
        if (teamID == 0) {
            player.sendMessage(ChatColor.RED + "You're not apart of a team.");
            return;
        }

        List<Team> teams = SQLiteManager.convertResultSetToTeams(SQLiteManager.getTeamsByID(teamID));
        if (teams.size() > 1) {
            CoreUtils.sendColoredMessage(
                    sender,
                    "&cYou appear to be on multiple teams; please contact an admin."
            );
            return;
        }

        Team team = teams.get(0);
        CoreUtils.sendColoredMessage(
                sender,
                "&7You're apart of &e" + team.getName() + " &7(#" + team.getId() + ") est. " + Team.formatDateShort(team.getBirthdate())
        );
    }

    /* Creates a team.
    Takes in a team name and if it is not already taken,
    creates a team with the given name and joins the player
    to the created team, making them the owner. */
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
            CoreUtils.sendColoredMessage(player, "&cA team by the name of &e" + teamName + " &calready exists.");
            return;
        }

        // Attempt to register new team in database
        SQLiteManager.createTeam(teamName, player);
        PDCManager.setTeam(player, teamId);

        // Notify player
        CoreUtils.sendColoredMessage(player, "&7Successfully created team &e" + teamName + "&7!");

    }

    /* Lists existing teams.
    Takes in an (optional) search query and returns a list of
    all existing teams matching the search query; if none is given,
    returns a list of all existing teams. */
    public static void list(CommandSender sender, String @NotNull [] args) {

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

        List<Team> teams = SQLiteManager.convertResultSetToTeams(resultSet);
        for (Team team : teams) {
            CoreUtils.sendColoredMessage(
                    sender,
                    "  \u2022 &e" + team.getName() + " &7(#" + team.getId() + ") est. " + Team.formatDateShort(team.getBirthdate())
            );
        }

    }

    /* Leaves the current team.
    If the player is not currently in a team, returns an
    error message. If successful, notifies the player. */
    public static void leave(CommandSender sender) {

        // If the command was not run by a player
        Player player = CoreUtils.checkPlayer(sender);
        if (player == null) return;

        int teamID = PDCManager.getTeamID(player);

    }

}
