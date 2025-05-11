package dev.canable.cablepunishments.storage.config;

import dev.canable.cablepunishments.CablePunishmentsPlugin;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

@Getter
public class SettingsYaml {
    private final File configFile;
    private FileConfiguration config;

    private String mongoHost;
    private int mongoPort;
    private String mongoDatabase;
    private String mongoUsername;
    private String mongoPassword;
    private String mongoAuthDatabase;

    public SettingsYaml(File dataFolder) {
        this.configFile = new File(dataFolder, "mongodb.yml");
        loadConfig();
    }

    public void loadConfig() {
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            CablePunishmentsPlugin.getInstance().saveResource(configFile.getName(), false);
        }

        config = YamlConfiguration.loadConfiguration(configFile);
        loadValues();
    }

    private void loadValues() {
        mongoHost = config.getString("mongodb.host", "localhost");
        mongoPort = config.getInt("mongodb.port", 27017);
        mongoDatabase = config.getString("mongodb.database", "minecraft");
        mongoUsername = config.getString("mongodb.username", "root");
        mongoPassword = config.getString("mongodb.password", "random");
        mongoAuthDatabase = config.getString("mongodb.auth-database", "admin");
    }
}