package dev.canable.cablepunishments.listeners;

import dev.canable.cablepunishments.CablePunishmentsPlugin;
import dev.canable.cablepunishments.model.Punishment;
import dev.canable.cablepunishments.utils.TextHelper;
import dev.canable.cablepunishments.utils.TimeFormatter;
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
        Map<UUID, Punishment> punishmentsMap = CablePunishmentsPlugin.getInstance().getPunishmentsManager().getMutesMap();
        if (punishmentsMap.containsKey(player.getUniqueId())) {

            Punishment punishment = punishmentsMap.get(player.getUniqueId());
            if (punishment.getPunishmentType() == Punishment.PunishmentType.MUTE) {
                event.setCancelled(true);

                TextHelper.sendPrefixedMessage(player, "&cYou are muted for &6" +
                        TimeFormatter.formatDuration(punishment.getDuration()));
                TextHelper.broadcastPermissionPrefixedMessage("cpunishments.mute",
                        "&c"+player.getName()+" &7tried to use chat but is still muted.");
            }
        }
    }
}
