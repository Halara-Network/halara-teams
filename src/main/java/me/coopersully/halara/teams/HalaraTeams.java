package me.coopersully.halara.teams;

import me.coopersully.halara.teams.commands.CommandTeam;
import me.coopersully.halara.teams.commands.CommandTeamCreate;
import me.coopersully.halara.teams.commands.CommandTeamLeave;
import me.coopersully.halara.teams.commands.CommandTeamList;
import me.coopersully.halara.teams.data_management.config.ConfigMain;
import me.coopersully.halara.teams.data_management.SQLiteManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class HalaraTeams extends JavaPlugin {

    private static HalaraTeams plugin;

    public static HalaraTeams getPlugin() {
        return plugin;
    }

    @Override
    public void onEnable() {
        // Initialize plugin instance
        plugin = this;

        // Reload config files
        ConfigMain.reload();

        // Create database if it doesn't exist
        SQLiteManager.createNewDatabase(ConfigMain.getSqliteNamespace());
        SQLiteManager.connect();
        SQLiteManager.initializeTable();

        // Register all commands
        getCommand("team").setExecutor(new CommandTeam());
        getCommand("createteam").setExecutor(new CommandTeamCreate());
        getCommand("leaveteam").setExecutor(new CommandTeamLeave());
        getCommand("listteams").setExecutor(new CommandTeamList());
    }

    @Override
    public void onDisable() {
        SQLiteManager.disconnect();
    }
}
