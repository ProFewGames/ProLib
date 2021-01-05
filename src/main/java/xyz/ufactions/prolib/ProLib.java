package xyz.ufactions.prolib;

import com.google.common.base.Charsets;
import org.bukkit.configuration.file.YamlConfiguration;
import xyz.ufactions.prolib.api.MegaPlugin;
import xyz.ufactions.prolib.api.exception.MegaException;
import xyz.ufactions.prolib.autoupdate.ProUpdater;
import xyz.ufactions.prolib.command.CommandCenter;
import xyz.ufactions.prolib.database.DBPool;
import xyz.ufactions.prolib.file.ProLibConfig;
import xyz.ufactions.prolib.libs.UtilServer;
import xyz.ufactions.prolib.monitor.LagMeter;
import xyz.ufactions.prolib.networking.NetworkModule;
import xyz.ufactions.prolib.recharge.Recharge;
import xyz.ufactions.prolib.redis.Utility;
import xyz.ufactions.prolib.redis.connect.RedisTransferManager;
import xyz.ufactions.prolib.redis.data.MinecraftServer;
import xyz.ufactions.prolib.redis.shutdown.ShutdownManager;
import xyz.ufactions.prolib.redis.status.ServerStatusManager;
import xyz.ufactions.prolib.updater.Updater;
import xyz.ufactions.prolib.updater.exception.UpdaterInitializationException;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

public class ProLib extends MegaPlugin {

    @Override
    public void enable() {
        ProLibConfig.initialize(this);
        if (ProLibConfig.getInstance().autoUpdaterEnabled()) {
            log("Starting plugin updater...");
            try {
                ProUpdater updater = new ProUpdater(this, this.getDataFolder(),
                        ProLibConfig.getInstance().autoUpdaterURL(), ProLibConfig.getInstance().autoUpdaterSecureJar());
                updater.scheduleUpdater();
            } catch (MegaException | IOException e) {
                log("Failed to start plugin updater!");
                if (debugging())
                    e.printStackTrace();
            }
        } else {
            log("Plugin Updater disabled.");
        }
        if (DBPool.allowMySQL()) {
            log("MySQL Supported");
        } else {
            log("MySQL Unsupported");
        }
        generateSettings();
        log("Initializing Mechanics");
        try {
            Updater.initialize(this);
        } catch (UpdaterInitializationException e) {
            log("Failed to start updater, some mechanics will not work.");
            if (debugging())
                e.printStackTrace();
        }
        Recharge.Initialize(this);
        LagMeter.initialize();
        CommandCenter.initialize();
        if (Utility.allowRedis()) {
            log("Networking found, enabling managers.");
            ServerStatusManager.initialize(this);
            ShutdownManager.initialize(this);
            RedisTransferManager.initialize(this);
            log("Listing networked servers:");
            for (MinecraftServer server : Utility.getServerRepository().getServerStatuses()) {
                log(server.getName());
            }
            addModule("Networking", NetworkModule.class);
        }
        log("Initialized Server Logistics/Mechanics");
    }

    @Override
    public void disable() {
        log("Disabling library hooks");
        for (MegaPlugin plugin : UtilServer.getMegaPlugins()) {
            if (plugin == this) continue;
            plugin.onDisable(); // Library Logic
        }
        log("Goodbye");
    }

    private void generateSettings() {
        File file = new File("settings.yml");
        if (!file.exists()) {
            log("Generating default settings.yml");
            try {
                YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("settings.yml")), Charsets.UTF_8));
                YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
                config.options().copyDefaults(true);
                config.setDefaults(defaultConfig);
                config.save(file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean debugging() {
        return ProLibConfig.getInstance().debugging();
    }
}