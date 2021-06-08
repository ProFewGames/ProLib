package xyz.ufactions.prolib.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.dbcp2.BasicDataSource;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import javax.sql.DataSource;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public final class DBPool {

    private static DataSource openDataSource(String url, String username, String password) {
        BasicDataSource source = new BasicDataSource();
        source.addConnectionProperty("autoReconnect", "true");
        source.addConnectionProperty("allowMultiQueries", "true");
        source.setDefaultTransactionIsolation(2);
        source.setDriverClassName("com.mysql.jdbc.Driver");
        source.setUrl(url);
        source.setUsername(username);
        source.setPassword(password);
        source.setMaxTotal(4);
        source.setMaxIdle(4);
        source.setTimeBetweenEvictionRunsMillis(180000L);
        source.setSoftMinEvictableIdleTimeMillis(180000L);
        return source;
    }

    private static File file;
    private static FileConfiguration config;

    private static final Map<SourceType, DataSource> map = new HashMap<>();

    public static DataSource getSource(SourceType type) {
        if (!map.containsKey(type)) {
            if (allowMySQL()) {
                for (String label : getConfig().getConfigurationSection("mysql.connections").getKeys(false)) {
                    String key = "mysql.connections." + label;
                    if (SourceType.valueOf(getConfig().getString(key + ".type")) == type) {
                        System.out.println("<MySQL> Registering database type: " + type.name());
                        map.put(type, openDataSource("jdbc:mysql://" + getConfig().getString(key + ".url"), getConfig().getString(key + ".username"), getConfig().getString(key + ".password")));
                        break;
                    }
                }
            } else {
                throw new UnsupportedOperationException("MySQL is not enabled on the server.");
            }
        }
        return map.get(type);
    }

    public static boolean allowMySQL() {
        return getConfig().getBoolean("mysql.enabled");
    }

    private static FileConfiguration getConfig() {
        if (config == null) {
            if (file == null) file = new File("settings.yml");
            config = YamlConfiguration.loadConfiguration(file);
        }
        return config;
    }
}
