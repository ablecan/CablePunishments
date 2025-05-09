package dev.canable.cablepunishments.storage;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import dev.canable.cablepunishments.model.Punishment;
import org.bson.Document;

import java.util.UUID;

public class MongoDB {
    private final MongoClient mongoClient;
    private final MongoDatabase database;
    private final MongoCollection<Document> punishments;

    public MongoDB(String dbName) {
        this.mongoClient = MongoClients.create("mongodb://root:random@localhost:27017/admin");
        this.database = mongoClient.getDatabase(dbName);
        this.punishments = database.getCollection("punishments");
    }

    // Close the mongodb client
    public void close() {
        mongoClient.close();
    }
    // ip, issuer, type, reason, issued add, duration
    // Add a new punishment
    public void addPunishment(Punishment punishment) {
        Document doc = new Document("_id", UUID.randomUUID().toString())
                .append("issuer", punishment.getIssuer().toString())
                .append("punishment_type", punishment.getPunishmentType().name().toLowerCase())
                .append("reason", punishment.getReason())
                .append("issuedAt", punishment.getIssuedAt())
                .append("duration", punishment.getDuration())
                .append("active", true);
        if(punishment.isIp()) punishments.insertOne(doc.append("ip", true).append("ip_address", punishment.getIpAdress()));
        else punishments.insertOne(doc.append("ip", false).append("target", punishment.getTarget().toString()));
    }

    // Query the database for an active punishment
    public Punishment getActivePunishment(UUID target, Punishment.PunishmentType punishmentType) {
        Document doc = punishments.find(new Document("target", target.toString())
                .append("punishment_type", punishmentType.toString())
                .append("active", true)).first();

        if (doc != null) {
            UUID issuer = UUID.fromString(doc.getString("issuer"));
            String reason = doc.getString("reason");
            String ipAdress = doc.getString("ip_address");
            long issuedAt = doc.getLong("issuedAt");
            long duration = doc.getLong("duration");
            boolean ip = doc.getBoolean("ip");

            return new Punishment(issuer, target, punishmentType, reason, ipAdress, issuedAt, duration, ip);
        }
        return null;
    }

    public Punishment getActiveIPPunishment(String ipAddress, Punishment.PunishmentType punishmentType) {
        Document doc = punishments.find(new Document("ip_address", ipAddress)
                .append("punishment_type", punishmentType.toString().toLowerCase())
                .append("active", true)).first();

        if (doc != null) {
            UUID issuer = UUID.fromString(doc.getString("issuer"));
            String reason = doc.getString("reason");
            long issuedAt = doc.getLong("issuedAt");
            long duration = doc.getLong("duration");
            boolean ip = doc.getBoolean("ip");

            return new Punishment(issuer, null, punishmentType, reason, ipAddress, issuedAt, duration, ip);
        }
        return null;
    }

    // Update punishment activity (if it's ongoing or not you know? 🎃👍)
    public void updatePunishmentStatus(UUID target, boolean active) {
        Document filter = new Document("target", target.toString());
        Document update = new Document("$set", new Document("active", active));
        punishments.updateOne(filter, update);
    }

}
