package dev.canable.cablepunishments.tasks;

import dev.canable.cablepunishments.CablePunishmentsPlugin;
import dev.canable.cablepunishments.managers.PunishmentsManager;
import dev.canable.cablepunishments.model.Punishment;
import dev.canable.cablepunishments.utils.TextHelper;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class PunishmentsTask extends BukkitRunnable {

    @Override
    public void run() {
        PunishmentsManager punishmentsManager = CablePunishmentsPlugin.getInstance().getPunishmentsManager();

        // Check normal punishments
        for (UUID uuid : punishmentsManager.getBansMap().keySet()) {
            Punishment punishment = punishmentsManager.getBan(uuid);
            if (!punishment.isActive()) {
                punishmentsManager.removePunishment(uuid, punishment);
                CablePunishmentsPlugin.getInstance().getMongoDB().updatePunishmentStatus(uuid, false);
            }
        }

        for (UUID uuid : punishmentsManager.getMutesMap().keySet()) {
            Punishment punishment = punishmentsManager.getMute(uuid);
            if (!punishment.isActive()) {
                punishmentsManager.removePunishment(uuid, punishment);
                CablePunishmentsPlugin.getInstance().getMongoDB().updatePunishmentStatus(uuid, false);
                Player player = Bukkit.getPlayer(uuid);
                if (player != null) {
                    TextHelper.sendPrefixedMessage(player, "&aYour mute has expired.");
                }
            }
        }

        // Check IP punishments
        for (String ip : punishmentsManager.getIpBansMap().keySet()) {
            Punishment punishment = punishmentsManager.getIPBan(ip);
            if (!punishment.isActive()) {
                punishmentsManager.removeIPPunishment(ip, punishment);
                CablePunishmentsPlugin.getInstance().getMongoDB().updateIPPunishmentStatus(ip,false);
            }
        }

        for (String ip : punishmentsManager.getIpMutesMap().keySet()) {
            Punishment punishment = punishmentsManager.getIPMute(ip);
            if (!punishment.isActive()) {
                punishmentsManager.removeIPPunishment(ip, punishment);
                CablePunishmentsPlugin.getInstance().getMongoDB().updateIPPunishmentStatus(ip, false);
                // Notify all online players with this IP
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.getAddress().getAddress().getHostAddress().equals(ip)) {
                        TextHelper.sendPrefixedMessage(player, "&aYour IP mute has expired.");
                    }
                }
            }
        }
    }
}
