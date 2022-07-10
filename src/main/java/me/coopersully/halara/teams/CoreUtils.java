package me.coopersully.halara.teams;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CoreUtils {

    public static @Nullable Player checkPlayer(CommandSender sender) {
        if (sender instanceof Player player) return player;
        sender.sendMessage(ChatColor.RED + "This command can only be run via a player.");
        return null;
    }
    public static @NotNull String colorMessage(@NotNull String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static void sendColoredMessage(@NotNull CommandSender sender, @NotNull String message) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

}
