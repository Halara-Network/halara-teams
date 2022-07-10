package me.coopersully.halara.teams.data_management.config;

import me.coopersully.halara.teams.HalaraTeams;

public class ConfigMain {

    private static boolean debug;
    private static String sqliteNamespace;

    public static void reload() {

        // Load current config.yml into plugin's memory
        HalaraTeams.getPlugin().reloadConfig();
        HalaraTeams.getPlugin().saveDefaultConfig();
        var config = HalaraTeams.getPlugin().getConfig();

        // Universal section
        debug = config.getBoolean("debug");

        // SQLite3 section
        sqliteNamespace = config.getString("sqlite.namespace") + ".db";

    }

    public static boolean isDebug() {
        return debug;
    }

    public static String getSqliteNamespace() {
        return sqliteNamespace;
    }
}
