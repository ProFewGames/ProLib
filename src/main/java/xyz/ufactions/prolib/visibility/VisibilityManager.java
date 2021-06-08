package xyz.ufactions.prolib.visibility;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import xyz.ufactions.prolib.api.MegaPlugin;
import xyz.ufactions.prolib.libs.UtilServer;
import xyz.ufactions.prolib.updater.UpdateType;
import xyz.ufactions.prolib.updater.event.UpdateEvent;

import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

public class VisibilityManager implements Listener {

    private static VisibilityManager instance;

    public static VisibilityManager getInstance() {
        return instance;
    }

    public static void initialize(MegaPlugin plugin) {
        if (instance == null) instance = new VisibilityManager(plugin);
    }

    private final MegaPlugin plugin;
    private final HashMap<UUID, VisibilityData> data;

    private VisibilityManager(MegaPlugin plugin) {
        this.plugin = plugin;

        this.data = new HashMap<>();
        this.plugin.registerEvents(this);
    }

    public VisibilityData getData(Player player) {
        if (!data.containsKey(player.getUniqueId())) data.put(player.getUniqueId(), new VisibilityData());
        return data.get(player.getUniqueId());
    }

    public void setVisibility(Player target, boolean isVisible, Player... viewers) {
        for (Player player : viewers) {
            if (player.equals(target)) continue;
            getData(player).updateVisibility(player, target, !isVisible);
        }
    }

    public void refreshPlayerToAll(Player player) {
        setVisibility(player, false, UtilServer.getPlayers());
        setVisibility(player, true, UtilServer.getPlayers());
    }

    @EventHandler
    public void onUpdate(UpdateEvent e) {
        if (e.getType() != UpdateType.SLOW) return;

        Iterator<UUID> iterator = data.keySet().iterator();
        while (iterator.hasNext()) {
            UUID uuid = iterator.next();
            Player player = Bukkit.getPlayer(uuid);
            if (player == null || !player.isOnline() || !player.isValid()) {
                iterator.remove();
                continue;
            }
            data.get(uuid).attemptToProcessUpdate(player);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        data.remove(e.getPlayer().getUniqueId());
    }
}