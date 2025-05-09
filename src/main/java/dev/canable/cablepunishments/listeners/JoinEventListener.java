package dev.canable.cablepunishments.listeners;

import dev.canable.cablepunishments.CablePunishmentsPlugin;
import dev.canable.cablepunishments.managers.PunishmentsManager;
import dev.canable.cablepunishments.model.Punishment;
import dev.canable.cablepunishments.utils.TextHelper;
import dev.canable.cablepunishments.utils.TimeFormatter;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.util.UUID;

public class JoinEventListener implements Listener {

    @EventHandler
    public void onJoin(AsyncPlayerPreLoginEvent event) {
        PunishmentsManager punishmentsManager = CablePunishmentsPlugin.getInstance().getPunishmentsManager();
        UUID target = event.getUniqueId();
        String name = event.getName();

        // Check if cache contains a punishment first.
        if (!punishmentsManager.getMutesMap().containsKey(target)) {
            Punishment mute = CablePunishmentsPlugin.getInstance().getMongoDB().getActivePunishment(target, Punishment.PunishmentType.MUTE);
            if (mute != null) punishmentsManager.addPunishment(mute);
        }
        if (!punishmentsManager.getBansMap().containsKey(target)) {
            Punishment ban = CablePunishmentsPlugin.getInstance().getMongoDB().getActivePunishment(target, Punishment.PunishmentType.BAN);
            if (ban != null) {
                punishmentsManager.addPunishment(ban);
                disallowLogin(event, ban);
            }
        }
    }

    private void disallowLogin(AsyncPlayerPreLoginEvent event, Punishment ban) {
        String message = TextHelper.format(
                "&cYou are banned from the server\n\n" +
                        "&eIssuer&7: &f" + Bukkit.getOfflinePlayer(ban.getIssuer()).getName() + "\n" +
                        "&eDuration&7: &f" + TimeFormatter.formatDuration(ban.getDuration()) + "\n\n" +
                        "&aContact us through &9Discord &asupport tickets &f@ &9discord.pavemc.net"
        );
        event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, message);
    }
}
