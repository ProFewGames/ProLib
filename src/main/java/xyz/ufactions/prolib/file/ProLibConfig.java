package xyz.ufactions.prolib.file;

import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import xyz.ufactions.prolib.ProLib;
import xyz.ufactions.prolib.api.MegaPlugin;
import xyz.ufactions.prolib.libs.F;
import xyz.ufactions.prolib.libs.FileHandler;

import java.util.HashMap;
import java.util.Map;

public class ProLibConfig extends FileHandler {

    private static ProLibConfig instance;

    public static ProLibConfig getInstance() {
        return instance;
    }

    public static void initialize(MegaPlugin plugin) {
        if (instance == null) instance = new ProLibConfig(plugin);
    }

    public enum ProLibConfigColor {
        PRIMARY("formatting.primary"),
        SECONDARY("formatting.secondary"),
        ELEMENTAL("formatting.elemental"),
        ERROR("formatting.error");

        private final String path;

        ProLibConfigColor(String path) {
            this.path = path;
        }
    }

    private Map<ProLibConfigColor, ChatColor> map = new HashMap<>();

    private ProLibConfig(MegaPlugin plugin) {
        super(plugin, plugin.getDataFolder(), "config.yml");
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

    public boolean customGUI() {
        return getBoolean("custom-gui", false);
    }

    public ChatColor getColor(ProLibConfigColor path) {
        Validate.notNull(path);

        if (!map.containsKey(path)) {
            ChatColor color;
            try {
                color = ChatColor.valueOf(getString(path.path).toUpperCase());
            } catch (Exception e) {
                color = ChatColor.WHITE;
                if (ProLib.debugging()) {
                    plugin.warning("Failed to fetch color from config for " + F.capitalizeFirstLetter(path.name()));
                    e.printStackTrace();
                }
            }
            map.put(path, color);
        }
        return map.get(path);
    }

    @Override
    protected void onReload() {
        map = new HashMap<>();
    }
}