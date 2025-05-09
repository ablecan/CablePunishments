package dev.canable.cablepunishments.managers;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import dev.canable.cablepunishments.CablePunishmentsPlugin;
import dev.canable.cablepunishments.model.Punishment;
import lombok.Getter;

import java.util.*;
import java.util.concurrent.ConcurrentMap;

@Getter
public final class PunishmentsManager {

    private ConcurrentMap<UUID, Punishment> bansMap = Maps.newConcurrentMap();
    private ConcurrentMap<UUID, Punishment> mutesMap = Maps.newConcurrentMap();
    private ConcurrentMap<String, Set<Punishment>> ipPunishments = Maps.newConcurrentMap();

    public void addPunishment(Punishment punishment) {
        if (punishment.isIp() && !punishment.getIpAdress().isEmpty()) {
            ipPunishments.computeIfAbsent(punishment.getIpAdress(), k -> Sets.newHashSet()).add(punishment);
            CablePunishmentsPlugin.getInstance().getMongoDB().addPunishment(punishment);
            return;
        }

        if (punishment.getPunishmentType() == Punishment.PunishmentType.BAN)
            bansMap.put(punishment.getTarget(), punishment);
        else
            mutesMap.put(punishment.getTarget(), punishment);

        CablePunishmentsPlugin.getInstance().getMongoDB().addPunishment(punishment);
    }

    public void removePunishment(UUID target, Punishment punishment) {
        CablePunishmentsPlugin.getInstance().getMongoDB().updatePunishmentStatus(target, false);
        if (punishment.getPunishmentType() == Punishment.PunishmentType.BAN)
            bansMap.remove(target);
        else
            mutesMap.remove(target);

        if (punishment.isIp() && !punishment.getIpAdress().isEmpty()) {
            Set<Punishment> punishments = ipPunishments.get(punishment.getIpAdress());
            if (punishments != null) {
                punishments.remove(punishment);
                if (punishments.isEmpty()) ipPunishments.remove(punishment.getIpAdress());
            }
        }
    }

    public Set<Punishment> getIPPunishment(String ipAddress) {
        return ipPunishments.getOrDefault(ipAddress, Collections.emptySet());
    }

}
