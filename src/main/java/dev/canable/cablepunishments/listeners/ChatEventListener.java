package dev.canable.cablepunishments.listeners;

import dev.canable.cablepunishments.CablePunishmentsPlugin;
import dev.canable.cablepunishments.managers.PunishmentsManager;
import dev.canable.cablepunishments.model.Punishment;
import dev.canable.cablepunishments.utils.TextHelper;
import dev.canable.cablepunishments.utils.TimeFormatter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public final class ChatEventListener implements Listener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String ipAddress = player.getAddress().getAddress().getHostAddress();

        PunishmentsManager punishmentsManager = CablePunishmentsPlugin.getInstance().getPunishmentsManager();
        Punishment mute = punishmentsManager.getMute(player.getUniqueId());
        // Check in cache if the player has an active mute
        if (mute != null) disallowChat(event, mute);

        // Check in cache if the player has an active ip mute
        Punishment ipMute = punishmentsManager.getIPMute(ipAddress);
        if(ipMute != null) disallowChat(event, ipMute);

    }

    private void disallowChat(AsyncPlayerChatEvent event, Punishment mute){
        Player player = event.getPlayer();

        event.setCancelled(true);

        TextHelper.sendPrefixedMessage(player, "&cYou are muted for &6" +
                TimeFormatter.formatDuration(mute.remainingDuration()));
        TextHelper.broadcastPermissionPrefixedMessage("cablepunishments.mute",
                "&c" + player.getName() + " &7tried to use chat but is still muted &8(&a"
                        + TimeFormatter.formatDuration(mute.remainingDuration()) + "&8).");
    }

}