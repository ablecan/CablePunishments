package dev.canable.cablepunishments.commands;

import dev.canable.cablepunishments.CablePunishmentsPlugin;
import dev.canable.cablepunishments.model.Punishment;
import dev.canable.cablepunishments.utils.TextHelper;
import dev.canable.cablepunishments.utils.TimeFormatter;
import dev.velix.imperat.BukkitSource;
import dev.velix.imperat.annotations.*;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;


@Command(value = {"mute", "m"})
@Permission("cpunishments.mute")
public final class MuteCommand {


    @Usage
    public void onDefaultExecution(BukkitSource source) {
        Player player = source.asPlayer();
        TextHelper.sendPrefixedMessage(player, "&eUsage: /mute (target) [-silent/-s] [-ip] [duration] [reason]");
    }

    @Usage
    public void mute(BukkitSource source, OfflinePlayer target,
                    @Switch({"silent", "s"}) boolean silent,
                    @Optional @Switch("ip") boolean ip,
                    @Optional @Nullable String duration,
                    @Optional @Greedy String reason) {

        // Check if the duration is not set; a lifetime punishment.
        long muteDuration = -1;
        if (duration != null) muteDuration = TimeFormatter.parseDuration(duration);

        String finalReason = reason == null ? "Breaking the rules" : reason;
        String durationMessage = duration == null ? "lifetime" : duration;

        // Check if the punishment is IP based to capture the address if the player is online
        String ipAddress = null;
        if (ip) {
            Player onlineTarget = Bukkit.getPlayer(target.getUniqueId());
            if (onlineTarget == null) {
                TextHelper.sendPrefixedMessage(source.asPlayer(), "&cTarget must be online to IP mute.");
                return;
            }
            ipAddress = onlineTarget.getAddress().getAddress().getHostAddress();
        }

        Punishment punishment = new Punishment(source.asPlayer().getUniqueId(), target.getUniqueId(),
                Punishment.PunishmentType.MUTE, finalReason, ipAddress, System.currentTimeMillis(), muteDuration, ip);

        // Kicking the player if he's online.
        if(target.isOnline()) target.getPlayer().kickPlayer(TextHelper.format(
                "&cYou are muted from the server\n\n" +
                        "&eIssuer&7: &f" + Bukkit.getOfflinePlayer(punishment.getIssuer()).getName() + "\n" +
                        "&eDuration&7: &f" + TimeFormatter.formatDuration(punishment.getDuration()) + "\n\n" +
                        "&aContact us through &9Discord &asupport tickets &f@ &9discord.pavemc.net"
        ));

        // Inserting the punishment object into memory & mongodb to have a hybrid type of storage.
        CablePunishmentsPlugin.getInstance().getPunishmentsManager().addPunishment(punishment);

        // Broadcasting the punishment to online staff.
        TextHelper.broadcastPermissionPrefixedMessage("cpunishments.mute",
                "&c" + source.asPlayer().getName() + " &7has &aMuted "+target.getName()+"&8(&e" + durationMessage + "&8) &7for &a"+finalReason);

        // Broadcasting the punishment to all players if it's not silent.
        if (!silent)
            TextHelper.broadcastPrefixedMessage(
                    "&6" + target.getName() + " &7has been &cMuted &7for &6" + durationMessage + " &7due to &c"+finalReason
            );
    }

}