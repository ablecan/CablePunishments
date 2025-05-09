package dev.canable.cablepunishments.tasks;

import dev.canable.cablepunishments.CablePunishmentsPlugin;
import dev.canable.cablepunishments.managers.PunishmentsManager;
import dev.canable.cablepunishments.model.Punishment;
import dev.canable.cablepunishments.utils.TextHelper;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.UUID;

public final class PunishmentsTask extends BukkitRunnable {

    private PunishmentsManager punishmentsManager = CablePunishmentsPlugin.getInstance().getPunishmentsManager();

    @Override
    public void run() {
        for (Map.Entry<UUID, Punishment> entry : punishmentsManager.getBansMap().entrySet()) {
            UUID uuid = entry.getKey();
            Punishment ban = entry.getValue();

            if (System.currentTimeMillis() - ban.getIssuedAt() >= ban.getDuration())
                punishmentsManager.removePunishment(uuid, ban);
        }
        for (Map.Entry<UUID, Punishment> entry : punishmentsManager.getMutesMap().entrySet()) {
            UUID uuid = entry.getKey();
            Punishment mute = entry.getValue();

            if (System.currentTimeMillis() - mute.getIssuedAt() >= mute.getDuration()) {
                punishmentsManager.removePunishment(uuid, mute);

                Player player = Bukkit.getPlayer(uuid);
                if (player.isOnline() && mute.getPunishmentType() == Punishment.PunishmentType.MUTE) {
                    TextHelper.sendPrefixedMessage(player, "&aYou have been unmuted!");
                }
            }
        }
    }
}
