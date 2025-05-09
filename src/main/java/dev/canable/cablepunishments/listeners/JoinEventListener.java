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

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class JoinEventListener implements Listener {

    @EventHandler
    public void onJoin(AsyncPlayerPreLoginEvent event) {
        PunishmentsManager punishmentsManager = CablePunishmentsPlugin.getInstance().getPunishmentsManager();
        UUID target = event.getUniqueId();
        String ipAddress = event.getAddress().getHostAddress().trim();

        // Check if cache contains a normal ban.
        Punishment ban = punishmentsManager.getBansMap().get(target);
        if (ban != null) {
            disallowLogin(event, ban);
            TextHelper.broadcastPrefixedMessage("cache ban is not null");
        } else { // If not check if the database contains a normal ban
            TextHelper.broadcastPrefixedMessage("cache ban is null");
            ban = CablePunishmentsPlugin.getInstance().getMongoDB()
                    .getActivePunishment(target, Punishment.PunishmentType.BAN);
            if (ban != null) {
                TextHelper.broadcastPrefixedMessage("mongo ban is not null");
                punishmentsManager.addPunishment(ban);
                disallowLogin(event, ban);
            } else { // Check if cache contains an IP ban.
                TextHelper.broadcastPrefixedMessage("mongo ban is null");
                Set<Punishment> ipPunishments = punishmentsManager.getIpPunishments().get(ipAddress);
                if (!ipPunishments.isEmpty()) {
                    disallowLogin(event, ipPunishments.iterator().next());
                    TextHelper.broadcastPrefixedMessage("cache ipban is not null");
                } else { // If not check if the database contains an IP ban.
                    StringBuilder message = new StringBuilder();
                    for (Punishment ipPunishment : ipPunishments) {
                        message.append(ipPunishment.getIpAdress()).append("\n");
                    }
                    TextHelper.broadcastPrefixedMessage(message.toString());
                    TextHelper.broadcastPrefixedMessage("cache ipban \""+ipAddress+"\" is  null");
                    ban = CablePunishmentsPlugin.getInstance().getMongoDB()
                            .getActiveIPPunishment(ipAddress, Punishment.PunishmentType.BAN);
                    if (ban != null) {
                        TextHelper.broadcastPrefixedMessage("mongo ipban \""+ipAddress+"\" is not null");
                        punishmentsManager.addPunishment(ban);
                        disallowLogin(event, ban);
                    }else TextHelper.broadcastPrefixedMessage("mongo ipban is null");
                }
            }
        }

        // Check if cache contains a normal mute.
        Punishment mute = punishmentsManager.getMutesMap().get(target);
        if (mute == null) { // If not check if the database contains a normal mute
            mute = CablePunishmentsPlugin.getInstance().getMongoDB()
                    .getActivePunishment(target, Punishment.PunishmentType.MUTE);
            if (mute != null) punishmentsManager.addPunishment(mute);
            else { // Check if cache contains an IP mute.
                Set<Punishment> ipPunishments = punishmentsManager.getIPPunishment(ipAddress);
                if (ipPunishments.isEmpty()) { // If not check if the database contains an IP mute.
                    mute = CablePunishmentsPlugin.getInstance().getMongoDB()
                            .getActiveIPPunishment(ipAddress, Punishment.PunishmentType.MUTE);
                    if (mute != null) punishmentsManager.addPunishment(mute);
                }
            }
        }
    }

    private void disallowLogin(AsyncPlayerPreLoginEvent event, Punishment ban) {
        String message = TextHelper.format(
                "&cYou are banned from the server\n\n" +
                        "&eIssuer&7: &f" + Bukkit.getOfflinePlayer(ban.getIssuer()).getName() + "\n" +
                        "&eDuration&7: &f" + TimeFormatter.formatDuration(ban.remainingDuration()) + "\n\n" +
                        "&aContact us through &9Discord &asupport tickets &f@ &9discord.pavemc.net"
        );
        event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, message);
    }
}
