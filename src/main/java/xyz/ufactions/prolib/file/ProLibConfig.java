package xyz.ufactions.prolib.file;

import xyz.ufactions.prolib.api.MegaPlugin;
import xyz.ufactions.prolib.libs.FileHandler;

public class ProLibConfig extends FileHandler {

    private static ProLibConfig instance;

    public static ProLibConfig getInstance() {
        return instance;
    }

    public static void initialize(MegaPlugin plugin) {
        if (instance == null) instance = new ProLibConfig(plugin);
    }

    private ProLibConfig(MegaPlugin plugin) {
        super(plugin, "config.yml", plugin.getDataFolder(), "config.yml");
    }

    public boolean autoUpdaterEnabled() {
        return getBoolean("auto-updater.enabled", true);
    }

    public boolean autoUpdaterSecureJar() {
        return getBoolean("auto-updater.secure-jar", true);
    }

    public String autoUpdaterURL() {
        return getString("auto-updater.url");
    }

    public String serverName() {
        return getString("server-name", "Unknown-Server");
    }

    public String fallbackServer() {
        return getString("fallback-server");
    }

    public boolean debugging() {
        return getBoolean("debugging", false);
    }
}