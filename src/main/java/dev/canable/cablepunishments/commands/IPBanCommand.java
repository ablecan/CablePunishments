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

@Command(value = {"ipban", "ipb"})
@Permission("cpunishments.ipban")
public final class IPBanCommand {


    @Usage
    public void onDefaultExecution(BukkitSource source) {
        Player player = source.asPlayer();
        TextHelper.sendPrefixedMessage(player, "&eUsage: /ipban (target) [-silent/-s] [duration] [reason]");
    }

    @Usage
    public void ban(BukkitSource source, OfflinePlayer target,
                    @Switch({"silent", "s"}) boolean silent,
                    @Optional @Nullable String duration,
                    @Optional @Greedy String reason) {
        // Check if the duration is not set; a lifetime punishment.
        long banDuration = -1;
        if (duration != null) banDuration = TimeFormatter.parseDuration(duration);

        String finalReason = reason == null ? "Breaking the rules" : reason;
        String durationMessage = duration == null ? "lifetime" : duration;

        Punishment punishment = new Punishment(source.asPlayer().getUniqueId(), target.getUniqueId(),
                Punishment.PunishmentType.BAN, finalReason,
                source.asPlayer().getAddress().getAddress().getHostAddress().trim(),
                System.currentTimeMillis(), banDuration, true);

        // Kicking the player if he's online.
        if(target.isOnline()) target.getPlayer().kickPlayer(TextHelper.format(
                "&cYou are banned from the server\n\n" +
                        "&eIssuer&7: &f" + Bukkit.getOfflinePlayer(punishment.getIssuer()).getName() + "\n" +
                        "&eDuration&7: &f" + TimeFormatter.formatDuration(punishment.getDuration()) + "\n\n" +
                        "&aContact us through &9Discord &asupport tickets &f@ &9discord.pavemc.net"
        ));

        // Inserting the punishment object into memory & mongodb to have a hybrid type of storage.
        CablePunishmentsPlugin.getInstance().getPunishmentsManager().addPunishment(punishment);

        // Broadcasting the punishment to online staff.
        TextHelper.broadcastPermissionPrefixedMessage("cpunishments.ipban",
                "&c" + source.asPlayer().getName() +
                        " &7has &aBanned "+target.getName()+"&8(&e" + durationMessage + "&8) &7for &a"+finalReason
        );
        // Broadcasting the punishment to all players if it's not silent.
        if (!silent)
            TextHelper.broadcastPrefixedMessage("&6" + target.getName() +
                    " &7has been &cBanned &7for &6" + durationMessage + " &7due to &c"+finalReason
            );
    }
}
