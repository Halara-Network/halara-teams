package me.coopersully.halara.teams.data_management;

import me.coopersully.halara.teams.HalaraTeams;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class PDCManager {

    private static NamespacedKey team = new NamespacedKey(HalaraTeams.getPlugin(), "team");

    /* Sets the player's currently stored team to none.
    Also used when registering players to have a team key. */
    public static void leaveTeam(@NotNull Player player) {
        player.getPersistentDataContainer().set(team, PersistentDataType.INTEGER, 0);
    }

    public static int getTeamID(Player player) {
        try {
            return player.getPersistentDataContainer().get(team, PersistentDataType.INTEGER);
        } catch (NullPointerException e) {
            leaveTeam(player); // Register team key
            return getTeamID(player);
        }
    }

    public static void setTeam(@NotNull Player player, @NotNull int id) {
        player.getPersistentDataContainer().set(team, PersistentDataType.INTEGER, id);
    }

    public static void setTeam(@NotNull Player player, @NotNull Team id) {
        player.getPersistentDataContainer().set(team, PersistentDataType.INTEGER, id.getId());
    }

}
