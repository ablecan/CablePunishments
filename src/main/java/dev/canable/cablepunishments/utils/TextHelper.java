package dev.canable.cablepunishments.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class TextHelper {
    private static String PREFIX = "&3CablePunishments &8┃ &r";

    public static String[] format(String... strings){
        return Arrays.stream(strings).map(TextHelper::format).toArray(String[]::new);
    }
    public static String format(String string){
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    public static List<String> format(List<String> strings) {
        return strings.stream().map(TextHelper::format).collect(Collectors.toList());
    }

    public static void sendPrefixedMessage(Player player, String string){
        player.sendMessage(format(PREFIX+string));
    }

    public static void broadcastPrefixedMessage(String message){
        Bukkit.broadcastMessage(format(PREFIX+message));
    }

    public static void broadcastPermissionPrefixedMessage(String permission, String message){
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if(onlinePlayer.hasPermission(permission)) sendPrefixedMessage(onlinePlayer,message);
        }
    }

    public static void sendConsoleMessage(String string){
        Bukkit.getConsoleSender().sendMessage(format(PREFIX+string));
    }
}
