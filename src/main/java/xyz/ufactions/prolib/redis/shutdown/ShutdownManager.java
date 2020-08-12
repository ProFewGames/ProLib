package xyz.ufactions.prolib.redis.shutdown;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.ufactions.prolib.file.ProLibConfig;
import xyz.ufactions.prolib.libs.Callback;
import xyz.ufactions.prolib.redis.JedisManager;

import java.util.HashMap;
import java.util.UUID;

/**
 * Listens on the network for a shutdown requests then handles logistics
 */
public class ShutdownManager {

    private static ShutdownManager instance;

    public static ShutdownManager getInstance() {
        return instance;
    }

    public static void initialize(JavaPlugin plugin) {
        if (instance == null)
            instance = new ShutdownManager(plugin);
    }

    private final JavaPlugin plugin;
    private final String server = ProLibConfig.getInstance().serverName();

    private final HashMap<UUID, ShutdownCallback> shutdowns = new HashMap<>(); // Resource Handling?

    private ShutdownManager(JavaPlugin plugin) {
        System.out.println("<Server> RedisShutdownManager has connected via redis.");

        this.plugin = plugin;

        // Listen on the network
        ShutdownHandler handler = new ShutdownHandler(this);
        JedisManager.getInstance().registerDataType("ShutdownCommand", ShutdownCommand.class, handler);
        JedisManager.getInstance().registerDataType("ShutdownCallback", ShutdownCallback.class, handler);
    }

    public void handleShutdown(ShutdownCallback callback) {
        this.shutdowns.put(callback.getUuid(), callback);
    }

    public void shutdown(final Callback<ShutdownCallback> callback, String server) {
        ShutdownCommand shutdown = new ShutdownCommand(this.server, server);
        final UUID uuid = shutdown.getUUID();
        new BukkitRunnable() {
            @Override
            public void run() {
                callback.run(shutdowns.get(uuid));
            }
        }.runTaskLater(plugin, 40L);
        this.shutdowns.put(uuid, null);
        shutdown.publish();
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }
}