package dev.canable.cablepunishments.storage;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import dev.canable.cablepunishments.CablePunishmentsPlugin;
import dev.canable.cablepunishments.model.Punishment;
import dev.canable.cablepunishments.storage.config.SettingsYaml;
import org.bson.Document;
import org.bukkit.Bukkit;

import java.io.File;
import java.util.UUID;
import java.util.logging.Level;

public class MongoDB {
    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> punishments;

    public MongoDB(File dataFolder) {
        SettingsYaml settings = new SettingsYaml(dataFolder);
        String connectionString = String.format("mongodb://%s:%s@%s:%d/%s?authSource=%s",
                settings.getMongoUsername(),
                settings.getMongoPassword(),
                settings.getMongoHost(),
                settings.getMongoPort(),
                settings.getMongoDatabase(),
                settings.getMongoAuthDatabase());

        try {
            this.mongoClient = MongoClients.create(connectionString);
            this.database = mongoClient.getDatabase(settings.getMongoDatabase());
            this.punishments = database.getCollection("punishments");
        }catch (Exception e){
            CablePunishmentsPlugin.getInstance().getLogger().log(Level.SEVERE,"Encountered an error connecting to MongoDB using data in the config file.\n Shutting down.");
            Bukkit.getPluginManager().disablePlugin(CablePunishmentsPlugin.getInstance());
        }
    }

    // Close the mongodb client
    public void close() {
        mongoClient.close();
    }

    // save a new punishment
    public void addPunishment(Punishment punishment) {
        Document doc = new Document("_id", UUID.randomUUID().toString())
                .append("issuer", punishment.getIssuer().toString())
                .append("punishment_type", punishment.getPunishmentType().name().toLowerCase())
                .append("reason", punishment.getReason())
                .append("issuedAt", punishment.getIssuedAt())
                .append("duration", punishment.getDuration())
                .append("active", true);

        if (punishment.isIp()) {
            punishments.insertOne(doc.append("ip", true).append("ip_address", punishment.getIpAddress()));
        } else {
            punishments.insertOne(doc.append("ip", false).append("target", punishment.getTarget().toString()));
        }
    }

    // Query the database in search of an active punishment
    public Punishment getActivePunishment(UUID target, Punishment.PunishmentType type) {
        Document doc = punishments.find(
                new Document("target", target.toString())
                        .append("punishment_type", type.name().toLowerCase())
                        .append("active", true)
        ).first();

        if (doc != null) {
            UUID issuer = UUID.fromString(doc.getString("issuer"));
            String reason = doc.getString("reason");
            String ipAddress = doc.getString("ip_address");
            long issuedAt = doc.getLong("issuedAt");
            long duration = doc.getLong("duration");
            boolean ip = doc.getBoolean("ip");

            return new Punishment(issuer, target, type, reason, ipAddress, issuedAt, duration, ip);
        }
        return null;
    }

    // Query the database in search of an active ip punishment
    public Punishment getActiveIPPunishment(String ipAddress, Punishment.PunishmentType type) {
        Document doc = punishments.find(
                new Document("ip_address", ipAddress)
                        .append("punishment_type", type.name().toLowerCase())
                        .append("active", true)
        ).first();

        if (doc != null) {
            UUID issuer = UUID.fromString(doc.getString("issuer"));
            String reason = doc.getString("reason");
            long issuedAt = doc.getLong("issuedAt");
            long duration = doc.getLong("duration");
            boolean ip = doc.getBoolean("ip");

            return new Punishment(issuer, null, type, reason, ipAddress, issuedAt, duration, ip);
        }
        return null;
    }

    // Update punishment activity (if it's ongoing or not you know? 🎃👍)
    public void updatePunishmentStatus(UUID target, boolean active) {
        punishments.updateMany(
                new Document("target", target.toString()),
                new Document("$set", new Document("active", active))
        );
    }

    // Update ip based punishment activity
    public void updateIPPunishmentStatus(String ipAddress, boolean active) {
        punishments.updateMany(
                new Document("ip_address", ipAddress),
                new Document("$set", new Document("active", active))
        );
    }
}