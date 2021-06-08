package xyz.ufactions.prolib.libs;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

public class UtilLoc {

    public static String readable(Vector vector) {
        return "X - " + vector.getBlockX() + ":Y - " + vector.getBlockY() + ":Z - " + vector.getBlockZ();
    }

    public static Vector getCenter(Vector v1, Vector v2) {
        return getMaximumVector(v1, v2).midpoint(getMinimumVector(v1, v2));
    }

    public static Vector getMaximumVector(Vector v1, Vector v2) {
        return Vector.getMaximum(v1, v2);
    }

    public static Vector getMinimumVector(Vector v1, Vector v2) {
        return Vector.getMinimum(v1, v2);
    }

    public static boolean isInsideOfArea(Vector origin, Vector v1, Vector v2) {
        return origin.isInAABB(getMinimumVector(v1, v2), getMaximumVector(v1, v2));
    }

    public static String toString(Location location) {
        return "World: " + location.getWorld().getName() +
                " X: " + location.getX() +
                " Y: " + location.getY() +
                " Z: " + location.getZ();
    }

    public static String serialize(Location location) {
        return location.getWorld().getName() + ":" +
                location.getX() + ":" +
                location.getY() + ":" +
                location.getZ() + ":" +
                location.getYaw() + ":" +
                location.getPitch() + ":";
    }

    public static Location deserialize(String serialized) {
        String[] array = serialized.split(":");
        World world = Bukkit.getWorld(array[0]);
        double x = Double.parseDouble(array[1]);
        double y = Double.parseDouble(array[2]);
        double z = Double.parseDouble(array[3]);
        float yaw = Float.parseFloat(array[4]);
        float pitch = Float.parseFloat(array[5]);
        return new Location(world, x, y, z, yaw, pitch);
    }
}