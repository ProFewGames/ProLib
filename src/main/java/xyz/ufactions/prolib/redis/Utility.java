package xyz.ufactions.prolib.redis;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisConnectionException;
import xyz.ufactions.prolib.redis.data.ServerRepository;
import xyz.ufactions.prolib.redis.repository.RedisServerRepository;

import java.io.File;

public class Utility {

    private static File file;
    private static FileConfiguration config;

    private static final Gson gson = new GsonBuilder().create();

    private static JedisPool jedisPool;
    private static ConnectionData connectionData;

    private static ServerRepository serverRepository;

    public static String serialize(Object o) {
        return gson.toJson(o);
    }

    public static <T> T deserialize(String serializedData, Class<T> type) {
        return gson.fromJson(serializedData, type);
    }

    public static ServerRepository getServerRepository() {
        if (serverRepository == null) serverRepository = new RedisServerRepository();
        return serverRepository;
    }

    public static String concatenate(char delimiter, String... elements) {
        int length = elements.length;
        String result = length > 0 ? elements[0] : "";
        for (int i = 1; i < length; i++) {
            result += delimiter + elements[i];
        }
        return result;
    }

    public static long currentTimeSeconds() {
        long currentTime = 0L;
        Jedis jedis = getJedisPool().getResource();
        try {
            currentTime = Long.parseLong(jedis.time().get(0));
        } catch (JedisConnectionException e) {
            e.printStackTrace();
            getJedisPool().returnBrokenResource(jedis);
            jedis = null;
        } finally {
            if (jedis != null) getJedisPool().returnResource(jedis);
        }
        return currentTime;
    }

    public static JedisPool getJedisPool() {
        if (jedisPool == null) {
            JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
            jedisPoolConfig.setMaxWaitMillis(1000L);
            jedisPoolConfig.setMinIdle(5);
            jedisPoolConfig.setTestOnBorrow(true);
            jedisPoolConfig.setMaxTotal(20);
            jedisPoolConfig.setBlockWhenExhausted(true);
            ConnectionData connData = getConnection();
            jedisPool = new JedisPool(jedisPoolConfig, connData.getHost(), connData.getPort(), 2000, connData.getPassword(), 0, null);
        }
        return jedisPool;
    }

    public static boolean allowRedis() {
        return getConfig().getBoolean("redis.enabled");
    }

    private static ConnectionData getConnection() {
        if (connectionData == null) {
            String host = getConfig().getString("redis.host");
            int port = getConfig().getInt("redis.port");
            String password = getConfig().getString("redis.password");
            connectionData = new ConnectionData(host, port, password);
        }
        return connectionData;
    }

    private static FileConfiguration getConfig() {
        if (config == null) {
            if (file == null)
                file = new File("settings.yml");
            config = YamlConfiguration.loadConfiguration(file);
        }
        return config;
    }
}