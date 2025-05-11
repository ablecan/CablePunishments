package dev.canable.cablepunishments.commands;

import dev.canable.cablepunishments.CablePunishmentsPlugin;
import dev.canable.cablepunishments.model.Punishment;
import dev.canable.cablepunishments.utils.TextHelper;
import dev.canable.cablepunishments.utils.TimeFormatter;
import dev.velix.imperat.BukkitSource;
import dev.velix.imperat.annotations.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

@Command(value = {"ipmute", "ipm"})
@Permission("cablepunishments.ipmute")
public final class IPMuteCommand {

    @Usage
    public void onDefaultExecution(BukkitSource source) {
        Player player = source.asPlayer();
        TextHelper.sendPrefixedMessage(player, "&eUsage: /ipmute (target) [-silent/-s] [duration] [reason]");
    }

    @Usage
    public void mute(BukkitSource source, Player target,
                     @Switch({"silent", "s"}) boolean silent,
                     @Optional @Nullable String duration,
                     @Optional @Greedy String reason) {
        if(!target.isOnline()){
            TextHelper.sendPrefixedMessage(source.asPlayer(), "&cTarget player must be online!");
            return;
        }

        long muteDuration = TimeFormatter.parseDuration(duration);
        if (muteDuration == -2) {
            TextHelper.sendPrefixedMessage(source.asPlayer(), "&cInvalid duration format. Use -1 for permanent or a valid time format (e.g., 1d, 2h, 30m).");
            return;
        }

        String finalReason = reason == null ? "Breaking the rules" : reason;
        String durationMessage = muteDuration == -1 ? "permanent" : duration;

        Punishment punishment = new Punishment(source.asPlayer().getUniqueId(), target.getUniqueId(),
                Punishment.PunishmentType.MUTE, finalReason, target.getAddress().getAddress().getHostAddress(),
                System.currentTimeMillis(), muteDuration, true);

        // Inserting the punishment object into memory & mongodb to have a hybrid type of storage.
        CablePunishmentsPlugin.getInstance().getPunishmentsManager().addPunishment(punishment);

        // Broadcasting the punishment to online staff.
        TextHelper.broadcastPermissionPrefixedMessage("cablepunishments.ipmute",
                "&c" + source.asPlayer().getName() + " &7has &aMuted "+target.getName()+"&8(&e" + durationMessage + "&8) &7for &a"+finalReason);

        // Broadcasting the punishment to all players if it's not silent.
        if (!silent)
            TextHelper.broadcastPrefixedMessage(
                    "&6" + target.getName() + " &7has been &cMuted &7for &6" + durationMessage + " &7due to &c"+finalReason
            );
    }
}