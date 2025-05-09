package dev.canable.cablepunishments.managers;

import com.google.common.collect.Maps;
import dev.canable.cablepunishments.CablePunishmentsPlugin;
import dev.canable.cablepunishments.model.Punishment;
import lombok.Getter;

import java.util.UUID;
import java.util.concurrent.ConcurrentMap;

@Getter
public final class PunishmentsManager {

    private ConcurrentMap<UUID, Punishment> bansMap = Maps.newConcurrentMap();
    private ConcurrentMap<UUID, Punishment> mutesMap = Maps.newConcurrentMap();

    public void addPunishment(Punishment punishment){
        if(punishment.getPunishmentType() == Punishment.PunishmentType.BAN)
            bansMap.put(punishment.getTarget(), punishment);
        else
            mutesMap.put(punishment.getTarget(), punishment);
        CablePunishmentsPlugin.getInstance().getMongoDB().addPunishment(punishment);
    }

    public void removePunishment(UUID target, Punishment punishment){
        CablePunishmentsPlugin.getInstance().getMongoDB().updatePunishmentStatus(target, false);
        if(punishment.getPunishmentType() == Punishment.PunishmentType.BAN)
            bansMap.remove(target);
        else
            mutesMap.remove(target);
    }
}
