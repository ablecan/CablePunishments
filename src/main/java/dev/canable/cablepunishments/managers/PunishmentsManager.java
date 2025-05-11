package dev.canable.cablepunishments.managers;

import com.google.common.collect.Maps;
import dev.canable.cablepunishments.CablePunishmentsPlugin;
import dev.canable.cablepunishments.model.Punishment;
import lombok.Getter;

import java.util.UUID;
import java.util.concurrent.ConcurrentMap;

@Getter
public final class PunishmentsManager {
    private final ConcurrentMap<UUID, Punishment> bansMap = Maps.newConcurrentMap();
    private final ConcurrentMap<UUID, Punishment> mutesMap = Maps.newConcurrentMap();
    private final ConcurrentMap<String, Punishment> ipBansMap = Maps.newConcurrentMap();
    private final ConcurrentMap<String, Punishment> ipMutesMap = Maps.newConcurrentMap();

    public void addPunishment(Punishment punishment) {
        if (punishment.isIp() && punishment.getIpAddress() != null && !punishment.getIpAddress().isEmpty()) {
            if (punishment.getPunishmentType() == Punishment.PunishmentType.BAN) {
                ipBansMap.put(punishment.getIpAddress(), punishment);
            } else {
                ipMutesMap.put(punishment.getIpAddress(), punishment);
            }
        } else {
            if (punishment.getPunishmentType() == Punishment.PunishmentType.BAN) {
                bansMap.put(punishment.getTarget(), punishment);
            } else {
                mutesMap.put(punishment.getTarget(), punishment);
            }
        }

        CablePunishmentsPlugin.getInstance().getMongoDB().addPunishment(punishment);
    }

    public void removePunishment(UUID target, Punishment punishment) {
        CablePunishmentsPlugin.getInstance().getMongoDB().updatePunishmentStatus(target, false);

        if (punishment.getPunishmentType() == Punishment.PunishmentType.BAN) {
            bansMap.remove(target);
        } else {
            mutesMap.remove(target);
        }
    }

    public void removeIPPunishment(String ipAddress, Punishment punishment) {
        CablePunishmentsPlugin.getInstance().getMongoDB().updateIPPunishmentStatus(ipAddress, false);

        if (punishment.isIp() && !punishment.getIpAddress().isEmpty()) {
            if (punishment.getPunishmentType() == Punishment.PunishmentType.BAN) {
                ipBansMap.remove(ipAddress);
            } else {
                ipMutesMap.remove(ipAddress);
            }
        }
    }

    public Punishment getBan(UUID uuid) {
        return bansMap.get(uuid);
    }

    public Punishment getMute(UUID uuid) {
        return mutesMap.get(uuid);
    }

    public Punishment getIPBan(String ipAddress) {
        return ipBansMap.get(ipAddress);
    }

    public Punishment getIPMute(String ipAddress) {
        return ipMutesMap.get(ipAddress);
    }
}