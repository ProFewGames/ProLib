package xyz.ufactions.prolib.redis.shutdown;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.ufactions.prolib.file.ProLibConfig;
import xyz.ufactions.prolib.redis.CommandCallback;
import xyz.ufactions.prolib.redis.ServerCommand;
import xyz.ufactions.prolib.redis.connect.RedisTransferManager;

public class ShutdownHandler implements CommandCallback {

    private final String server = ProLibConfig.getInstance().getServerName();

    private final ShutdownManager manager;

    public ShutdownHandler(ShutdownManager manager) {
        this.manager = manager;
    }

    public void run(ServerCommand command) {
        if (command instanceof ShutdownCommand) {
            final ShutdownCommand shutdownCommand = (ShutdownCommand) command;
            if (shutdownCommand.getTargetServer().equalsIgnoreCase(this.server)) {
                final int players = Bukkit.getOnlinePlayers().size();
                for (Player player : Bukkit.getOnlinePlayers())
                    RedisTransferManager.getInstance().transfer(player.getName(), ProLibConfig.getInstance().getFallbackServer()); // send * to fallback
                new BukkitRunnable() {

                    @Override
                    public void run() {
                        ShutdownCallback callback = new ShutdownCallback(shutdownCommand, ShutdownHandler.this.server, players);
                        callback.publish(); // Send callback before shutdown
                        Bukkit.shutdown();
                    }
                }.runTaskLater(manager.getPlugin(), 5L); // Shutdown the server after * is transferred out
            }
        } else if (command instanceof ShutdownCallback) {
            this.manager.handleShutdown((ShutdownCallback) command);
        }
    }
}
