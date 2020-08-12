package xyz.ufactions.prolib.redis.repository;

import redis.clients.jedis.*;
import redis.clients.jedis.exceptions.JedisConnectionException;
import xyz.ufactions.prolib.redis.Utility;
import xyz.ufactions.prolib.redis.data.MinecraftServer;
import xyz.ufactions.prolib.redis.data.ServerRepository;

import java.util.*;

// Responsible for handling server statuses for real-time networking
public class RedisServerRepository implements ServerRepository {

    public final char KEY_DELIMITER = '.';

    private final JedisPool pool;

    public RedisServerRepository() {
        this.pool = Utility.getJedisPool();
    }

    @Override
    public Collection<MinecraftServer> getServerStatuses() {
        Collection<MinecraftServer> servers = new HashSet<>();
        Jedis jedis = pool.getResource();
        try {
            String setKey = concatenate("serverstatus", "minecraft");
            Pipeline pipeline = jedis.pipelined();
            List<Response<String>> responses = new ArrayList<>();
            for (String serverName : getActiveNames(setKey)) {
                String dataKey = concatenate(setKey, serverName);
                responses.add(pipeline.get(dataKey));
            }
            pipeline.sync();
            for (Response<String> response : responses) {
                String seralizedData = response.get();
                MinecraftServer server = Utility.deserialize(seralizedData, MinecraftServer.class);
                if (server != null) servers.add(server);
            }
        } catch (Exception e) {
            e.printStackTrace();
            pool.returnBrokenResource(jedis);
            jedis = null;
        } finally {
            if (jedis != null) pool.returnResource(jedis);
        }
        return servers;
    }

    @Override
    public MinecraftServer getServerStatus(String name) {
        MinecraftServer server = null;
        Jedis jedis = pool.getResource();
        try {
            String setKey = concatenate("serverstatus", "minecraft");
            String dataKey = concatenate(setKey, name);
            String serializedData = jedis.get(dataKey);
            server = Utility.deserialize(serializedData, MinecraftServer.class);
        } catch (JedisConnectionException e) {
            e.printStackTrace();
            pool.returnBrokenResource(jedis);
            jedis = null;
        } finally {
            if (jedis != null) pool.returnResource(jedis);
        }
        return server;
    }

    @Override
    public void updateServerStatus(MinecraftServer server, int timeout) {
        Jedis jedis = pool.getResource();
        try {
            String serializedData = Utility.serialize(server);
            String serverName = server.getName();
            String setKey = concatenate("serverstatus", "minecraft");
            String dataKey = concatenate(setKey, serverName);
            long expiry = Utility.currentTimeSeconds() + timeout;
            Transaction transaction = jedis.multi();
            transaction.set(dataKey, serializedData);
            transaction.zadd(setKey, expiry, serverName);
            transaction.exec();
        } catch (JedisConnectionException e) {
            e.printStackTrace();
            pool.returnBrokenResource(jedis);
            jedis = null;
        } finally {
            if (jedis != null) pool.returnResource(jedis);
        }
    }

    @Override
    public void removeServerStatus(MinecraftServer server) {
        Jedis jedis = pool.getResource();
        try {
            String serverName = server.getName();
            String setKey = concatenate("serverstatus", "minecraft");
            String dataKey = concatenate(setKey, serverName);
            Transaction transaction = jedis.multi();
            transaction.del(dataKey);
            transaction.zrem(setKey, serverName);
            transaction.exec();
        } catch (JedisConnectionException e) {
            e.printStackTrace();
            pool.returnBrokenResource(jedis);
            jedis = null;
        } finally {
            if (jedis != null) pool.returnResource(jedis);
        }
    }

    @Override
    public boolean serverExists(String server) {
        return getServerStatus(server) != null;
    }

    protected Set<String> getActiveNames(String key) {
        Jedis jedis = pool.getResource();
        Set<String> names = new HashSet<>();
        try {
            String min = "(" + Utility.currentTimeSeconds();
            String max = "+inf";
            names = jedis.zrangeByScore(key, min, max);
        } catch (JedisConnectionException e) {
            e.printStackTrace();
            pool.returnBrokenResource(jedis);
            jedis = null;
        } finally {
            if (jedis != null) pool.returnResource(jedis);
        }
        return names;
    }

    protected String concatenate(String... elements) {
        return Utility.concatenate(KEY_DELIMITER, elements);
    }
}