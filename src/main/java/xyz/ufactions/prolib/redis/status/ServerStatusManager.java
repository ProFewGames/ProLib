package xyz.ufactions.prolib.redis.status;

import org.bukkit.Bukkit;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.ufactions.prolib.file.ProLibConfig;
import xyz.ufactions.prolib.monitor.LagMeter;
import xyz.ufactions.prolib.redis.Utility;
import xyz.ufactions.prolib.redis.data.MinecraftServer;
import xyz.ufactions.prolib.redis.data.ServerRepository;
import xyz.ufactions.prolib.updater.UpdateType;
import xyz.ufactions.prolib.updater.event.UpdateEvent;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Saves server data to Redis for networking
 */
public class ServerStatusManager {

    private static ServerStatusManager instance;

    public static ServerStatusManager getInstance() {
        return instance;
    }

    public static void initialize(JavaPlugin plugin) {
        if (instance == null) instance = new ServerStatusManager(plugin);
    }

    private final ServerRepository repository;

    private final long startupDate;
    private final String name;
    private final String publicIP;

    private final JavaPlugin plugin;

    private ServerStatusManager(JavaPlugin plugin) {
        System.out.println("<Server> ServerStatusManager has connected via redis.");

        this.startupDate = System.currentTimeMillis();
        this.name = ProLibConfig.getInstance().getServerName();
        this.repository = Utility.getServerRepository();
        this.publicIP = getPublicIP();

        this.plugin = plugin;

        saveServerStatus();
    }

    private String getPublicIP() {
        try {
            URL whatismyip = new URL("http://checkip.amazonaws.com");
            BufferedReader in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
            return in.readLine();
        } catch (Exception exception) {
            return "0.0.0.0";
        }
    }

    public String getCurrentServerName() {
        return name;
    }

    public void saveServerStatus(UpdateEvent e) {
        if (e.getType() != UpdateType.SLOW) return;

        saveServerStatus();
    }

    private void saveServerStatus() {
        new BukkitRunnable() {
            @Override
            public void run() {
                repository.updateServerStatus(generateServerSnapshot(), 10);
            }
        }.runTaskAsynchronously(plugin);
    }

    private MinecraftServer generateServerSnapshot() {
        ServerListPingEvent event = new ServerListPingEvent(null, Bukkit.getServer().getMotd(), Bukkit.getServer().getOnlinePlayers().size(), Bukkit.getServer().getMaxPlayers());
        Bukkit.getServer().getPluginManager().callEvent(event);
        String motd = event.getMotd();
        int playerCount = Bukkit.getOnlinePlayers().size();
        int maxPlayerCount = event.getMaxPlayers();
        int tps = (int) LagMeter.instance.getTicksPerSecond();
        int port = Bukkit.getServer().getPort();
        int ram = (int) ((Runtime.getRuntime().maxMemory() - Runtime.getRuntime().freeMemory()) / 1048576L);
        int maxRam = (int) ((Runtime.getRuntime().maxMemory() / 1048576L));
        return new MinecraftServer(this.name, motd, playerCount, maxPlayerCount, tps, ram, maxRam, this.publicIP, port, this.startupDate, System.currentTimeMillis());
    }
}