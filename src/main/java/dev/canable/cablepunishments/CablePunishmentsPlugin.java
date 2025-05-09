package dev.canable.cablepunishments;

import dev.canable.cablepunishments.commands.BanCommand;
import dev.canable.cablepunishments.commands.MuteCommand;
import dev.canable.cablepunishments.listeners.ChatEventListener;
import dev.canable.cablepunishments.managers.PunishmentsManager;
import dev.canable.cablepunishments.storage.MongoDB;
import dev.canable.cablepunishments.tasks.PunishmentsTask;
import dev.velix.imperat.BukkitImperat;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class CablePunishmentsPlugin extends JavaPlugin {

    public static @Getter CablePunishmentsPlugin instance;
    private BukkitImperat imperat;
    private PunishmentsManager punishmentsManager = new PunishmentsManager();
    private MongoDB mongoDB;

    @Override
    public void onEnable() {
        init();
        // Registering imperat framework commands.
        imperat.registerCommands(new BanCommand(), new MuteCommand());
        // Registering local plugin event listeners.
        registerListeners(new ChatEventListener());
        // Starting the recurring punishments task.
        new PunishmentsTask().runTaskTimerAsynchronously(this, 20L, 20 * 3L);
        // Initiating MongoDB
        mongoDB = new MongoDB("minecraft");
    }

    @Override
    public void onDisable() {
        imperat.unregisterAllCommands();
        mongoDB.close();
    }

    private void init() {
        instance = this;
        imperat = BukkitImperat.builder(this).build();
    }

    private void registerListeners(Listener... listeners) {
        for (Listener listener : listeners) {
            Bukkit.getPluginManager().registerEvents(listener, this);
        }
    }
}
