package dev.canable.cablepunishments.listeners;

import dev.canable.cablepunishments.CablePunishmentsPlugin;
import dev.canable.cablepunishments.managers.PunishmentsManager;
import dev.canable.cablepunishments.model.Punishment;
import dev.canable.cablepunishments.utils.TextHelper;
import dev.canable.cablepunishments.utils.TimeFormatter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Map;
import java.util.UUID;

public final class ChatEventListener implements Listener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        PunishmentsManager punishmentsManager = CablePunishmentsPlugin.getInstance().getPunishmentsManager();
        Punishment punishment = punishmentsManager.getMutesMap().get(player.getUniqueId());
        // Check if the player has an active punishment (mute)
        if (punishment != null && punishment.getPunishmentType() == Punishment.PunishmentType.MUTE) {
            event.setCancelled(true);

            TextHelper.sendPrefixedMessage(player, "&cYou are muted for &6" +
                    TimeFormatter.formatDuration(punishment.remainingDuration()));
            TextHelper.broadcastPermissionPrefixedMessage("cablepunishments.mute",
                    "&c" + player.getName() + " &7tried to use chat but is still muted &8(&a"
                            + TimeFormatter.formatDuration(punishment.remainingDuration()) + "&8).");
        }

        if (event.getMessage().startsWith("hi")) {
            StringBuilder message = new StringBuilder();
            punishmentsManager.getIpPunishments().forEach((string, punishments) -> {
                message.append(string).append("\n").append("time left: ")
                        .append(TimeFormatter.formatDuration(punishments.iterator().next().remainingDuration()));
            });

            event.setMessage(message.toString());
        }
    }
}