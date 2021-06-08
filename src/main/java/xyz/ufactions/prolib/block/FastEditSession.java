package xyz.ufactions.prolib.block;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import xyz.ufactions.prolib.libs.Callback;

import java.util.HashMap;
import java.util.Map;

public final class FastEditSession {

    private final Plugin plugin;
    private final Map<Location, Material> updates;

    public FastEditSession(Plugin plugin) {
        this(plugin, new HashMap<>());
    }

    public FastEditSession(Plugin plugin, Map<Location, Material> updates) {
        this.plugin = plugin;
        this.updates = updates;
    }

    public void setBlock(World world, int x, int y, int z, Material material) {
        this.updates.put(new Location(world, x, y, z), material);
    }

    public void update(Callback<Void> whenDone) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            for (Map.Entry<Location, Material> entry : this.updates.entrySet()) {
                entry.getKey().getBlock().setType(entry.getValue());
            }
            whenDone.run(null);
        });
    }
}