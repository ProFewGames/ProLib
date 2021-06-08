package xyz.ufactions.prolib.block;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockVector;
import xyz.ufactions.prolib.libs.UtilLoc;

import java.util.HashSet;
import java.util.Set;

/**
 * A cuboid area saving position x1, y1, z1 -> x2, y2, z2
 */
public class Region {

    private World world;

    private final BlockVector position1;
    private final BlockVector position2;

    public Region(BlockVector position1, BlockVector position2) {
        this(null, position1, position2);
    }

    public Region(World world, BlockVector position1, BlockVector position2) {
        this.position1 = position1;
        this.position2 = position2;

        if (world != null) setWorld(world);
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public World getWorld() {
        return world;
    }

    public Set<Player> getPlayers() {
        Set<Player> players = new HashSet<>();

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (isInside(player.getLocation())) {
                players.add(player);
            }
        }

        return players;
    }

    public BlockVector getPosition1() {
        return position1;
    }

    public BlockVector getPosition2() {
        return position2;
    }

    public boolean isInside(Location location) {
        return UtilLoc.isInsideOfArea(location.toVector(), position1, position2);
    }

    public int getMaximumHeight() {
        return getMaximumPoint().getBlockY();
    }

    public BlockVector getMaximumPoint() {
        return UtilLoc.getMaximumVector(position1, position2).toBlockVector();
    }

    public BlockVector getMinimumPoint() {
        return UtilLoc.getMinimumVector(position1, position2).toBlockVector();
    }

    public BlockVector getCenter() {
        return UtilLoc.getCenter(position1, position2).toBlockVector();
    }

    public String serialize() {
        return world == null ? "" : world.getName() + ";" +
                position1.getBlockX() + ";" + position1.getBlockY() + ";" + position1.getBlockZ() + ";" +
                position2.getBlockX() + ";" + position2.getBlockY() + ";" + position2.getBlockZ();
    }

    public static Region deserialize(String string) {
        String[] array = string.split(";");
        int x1 = Integer.parseInt(array[1]);
        int y1 = Integer.parseInt(array[2]);
        int z1 = Integer.parseInt(array[3]);
        BlockVector position1 = new BlockVector(x1, y1, z1);

        int x2 = Integer.parseInt(array[4]);
        int y2 = Integer.parseInt(array[5]);
        int z2 = Integer.parseInt(array[6]);
        BlockVector position2 = new BlockVector(x2, y2, z2);

        Region region = new Region(position1, position2);
        if (!array[0].isEmpty()) {
            World world = Bukkit.getWorld(array[0]);
            region.setWorld(world);
        }
        return region;
    }
}