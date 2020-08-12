package xyz.ufactions.prolib.updater;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.ufactions.prolib.monitor.LagMeter;
import xyz.ufactions.prolib.recharge.Recharge;
import xyz.ufactions.prolib.redis.Utility;
import xyz.ufactions.prolib.redis.status.ServerStatusManager;
import xyz.ufactions.prolib.updater.event.UpdateEvent;
import xyz.ufactions.prolib.updater.exception.UpdaterInitializationException;

public class Updater {

    private static JavaPlugin registree;

    public static void initialize(JavaPlugin plugin) throws UpdaterInitializationException {
        if (registree != null) {
            throw new UpdaterInitializationException("Updater already registered to '" + registree.getClass().getPackage().getName() + "." + registree.getClass().getName() + "' Consider a server restart?");
        } else {
            registree = plugin;
            plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
                for (UpdateType type : UpdateType.values()) {
                    if (type.Elapsed()) {
                        UpdateEvent event = new UpdateEvent(type);
                        Recharge.Instance.update(event);
                        LagMeter.instance.update(event);
                        if (Utility.allowRedis()) ServerStatusManager.getInstance().saveServerStatus(event);
                        Bukkit.getServer().getPluginManager().callEvent(event);
                    }
                }
            }, 1, 1);
        }
    }
}