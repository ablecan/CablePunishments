package dev.canable.cablepunishments.model;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class Punishment {
    private UUID issuer, target;
    private PunishmentType punishmentType;
    private String reason, ipAddress;
    private long issuedAt, duration;
    private boolean ip;

    public Punishment(UUID issuer, UUID target, PunishmentType punishmentType, String reason,
                      String ipAdresss, long issuedAt, long duration, boolean ip) {
        this.issuer = issuer;
        this.target = target;
        this.punishmentType = punishmentType;
        this.reason = reason;
        this.ipAddress = ipAdresss;
        this.duration = duration;
        this.issuedAt = issuedAt;
        this.ip = ip;
    }

    public boolean isActive() {
        // -1 means permanent punishment
        return duration == -1 || (System.currentTimeMillis() - this.issuedAt) < this.duration;
    }

    public long remainingDuration() {
        if (duration == -1) return -1;
        return Math.max(0, (this.issuedAt + this.duration) - System.currentTimeMillis());
    }

    public enum PunishmentType {MUTE, BAN}
}

