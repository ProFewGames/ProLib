package xyz.ufactions.prolib.file;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import xyz.ufactions.prolib.api.MegaPlugin;

import java.io.*;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLConnection;

public class ProLibConfig {

    private static ProLibConfig instance;

    public static ProLibConfig getInstance() {
        return instance;
    }

    public static void initialize(MegaPlugin plugin) {
        if (instance == null) instance = new ProLibConfig(plugin);
    }

    private final FileConfiguration configuration;

    private ProLibConfig(MegaPlugin plugin) {
        plugin.getDataFolder().mkdir();
        File file = new File(plugin.getDataFolder(), "config.yml");
        if (!file.exists()) {
            URL url = plugin.getClass().getClassLoader().getResource("config.yml");
            if (url != null) {
                try {
                    URLConnection connection = url.openConnection();
                    connection.setUseCaches(false);
                    InputStream in = connection.getInputStream();

                    OutputStream out = new FileOutputStream(file);
                    byte[] buf = new byte[1024];

                    int len;
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                    out.close();
                    in.close();
                } catch (IOException e) {
                    Bukkit.getLogger().severe("[ProLib] Failed to create file from resources.");
                    e.printStackTrace();
                }
            } else {
                Bukkit.getLogger().warning("[ProLib] Configuration resource does not exist");
            }
        }
        this.configuration = YamlConfiguration.loadConfiguration(file);
    }

    public boolean isDebuggingEnabled() {
        return configuration.getBoolean("debugging", false);
    }

    public String getFallbackServer() {
        return configuration.getString("fallback-server", "lobby");
    }

    public String getServerName() {
        return configuration.getString("server-name", "Unknown-Server");
    }

    public boolean isAutoUpdaterEnabled() {
        return configuration.getBoolean("auto-updater.enabled", false);
    }

    public String getAutoUpdaterURL() {
        return configuration.getString("auto-updater.url", "https://example.com/prolib.jar");
    }

    public Authenticator getAutoUpdaterAuthentication() {
        return new Authenticator() {

            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(configuration.getString("auto-updater.username", ""), configuration.getString("auto-updater.password", "").toCharArray());
            }
        };
    }

    public ChatColor getPrimaryColor() {
        return getColor("formatting.primary");
    }

    public ChatColor getSecondaryColor() {
        return getColor("formatting.secondary");
    }

    public ChatColor getElementalColor() {
        return getColor("formatting.elemental");
    }

    public ChatColor getErrorColor() {
        return getColor("formatting.error");
    }

    public boolean isCustomGUIsEnabled() {
        return configuration.getBoolean("custom-gui", false);
    }

    private ChatColor getColor(String path) {
        ChatColor color;
        try {
            color = ChatColor.valueOf(configuration.getString(path, "WHITE"));
        } catch (EnumConstantNotPresentException e) {
            Bukkit.getLogger().warning("[ProLib] Failed to load color from configuration");
            color = ChatColor.WHITE;
        }
        return color;
    }
}