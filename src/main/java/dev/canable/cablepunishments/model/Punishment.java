package dev.canable.cablepunishments.model;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@Getter
@Setter
public class Punishment {
    private UUID issuer, target;
    private PunishmentType punishmentType;
    private String reason, ipAdress;
    private long issuedAt, duration;
    private boolean ip;

    public Punishment(UUID issuer, UUID target, PunishmentType punishmentType, String reason,
                      String ipAdress, long issuedAt, long duration, boolean ip) {
        this.issuer = issuer;
        this.target = target;
        this.punishmentType = punishmentType;
        this.reason = reason;
        this.ipAdress = ipAdress;
        this.duration = duration;
        this.issuedAt = issuedAt;
        this.ip = ip;
    }

    public long remainingDuration() {
        return (this.issuedAt + this.duration) - System.currentTimeMillis();
    }

    public enum PunishmentType {MUTE, BAN}
}

