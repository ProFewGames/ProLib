package xyz.ufactions.prolib.libs;

import org.apache.commons.lang.Validate;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import xyz.ufactions.prolib.redis.Utility;
import xyz.ufactions.prolib.redis.player.PlayerStatus;
import xyz.ufactions.prolib.redis.player.RedisPlayerManager;
import xyz.ufactions.prolib.reflection.ReflectionUtils;

import java.util.*;

public class UtilPlayer {

    // Classes
    private static final ReflectionUtils.RefClass ClassCraftPlayer = ReflectionUtils.getRefClass("{cb}.entity.CraftPlayer");
    private static final ReflectionUtils.RefClass ClassEntityPlayer = ReflectionUtils.getRefClass("{nms}.EntityPlayer");
    private static final ReflectionUtils.RefClass ClassPlayerConnection = ReflectionUtils.getRefClass("{nms}.PlayerConnection");
    private static final ReflectionUtils.RefClass ClassPacket = ReflectionUtils.getRefClass("{nms}.Packet");

    // Methods
    private static final ReflectionUtils.RefMethod EntityPlayerMethodIsSpectator = ClassEntityPlayer.getMethod("isSpectator");
    private static final ReflectionUtils.RefMethod CraftPlayerMethodGetHandle = ClassCraftPlayer.getMethod("getHandle");
    private static final ReflectionUtils.RefMethod PlayerConnectionMethodSendPacket = ClassPlayerConnection.getMethod("sendPacket", ClassPacket);

    // Fields
    private static final ReflectionUtils.RefField EntityPlayerFieldPlayerConnection = ClassEntityPlayer.getField("playerConnection");

    private static boolean hasIntersection(Vector3D p1, Vector3D p2, Vector3D min, Vector3D max) {
        final double epsilon = 0.0001f;

        Vector3D d = p2.subtract(p1).multiply(0.5);
        Vector3D e = max.subtract(min).multiply(0.5);
        Vector3D c = p1.add(d).subtract(min.add(max).multiply(0.5));
        Vector3D ad = d.abs();

        if (Math.abs(c.x) > e.x + ad.x)
            return false;
        if (Math.abs(c.y) > e.y + ad.y)
            return false;
        if (Math.abs(c.z) > e.z + ad.z)
            return false;

        if (Math.abs(d.y * c.z - d.z * c.y) > e.y * ad.z + e.z * ad.y + epsilon)
            return false;
        if (Math.abs(d.z * c.x - d.x * c.z) > e.z * ad.x + e.x * ad.z + epsilon)
            return false;
        if (Math.abs(d.x * c.y - d.y * c.x) > e.x * ad.y + e.y * ad.x + epsilon)
            return false;

        return true;
    }

    private static class Vector3D {

        // Use protected members, like Bukkit
        private final double x;
        private final double y;
        private final double z;

        private Vector3D(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        private Vector3D(Location location) {
            this(location.toVector());
        }

        private Vector3D(Vector vector) {
            if (vector == null)
                throw new IllegalArgumentException("Vector cannot be NULL.");
            this.x = vector.getX();
            this.y = vector.getY();
            this.z = vector.getZ();
        }

        private Vector3D abs() {
            return new Vector3D(Math.abs(x), Math.abs(y), Math.abs(z));
        }

        private Vector3D add(double x, double y, double z) {
            return new Vector3D(this.x + x, this.y + y, this.z + z);
        }

        private Vector3D add(Vector3D other) {
            if (other == null)
                throw new IllegalArgumentException("other cannot be NULL");

            return new Vector3D(x + other.x, y + other.y, z + other.z);
        }

        private Vector3D multiply(double factor) {
            return new Vector3D(x * factor, y * factor, z * factor);
        }

        private Vector3D multiply(int factor) {
            return new Vector3D(x * factor, y * factor, z * factor);
        }

        private Vector3D subtract(Vector3D other) {
            if (other == null)
                throw new IllegalArgumentException("other cannot be NULL");
            return new Vector3D(x - other.x, y - other.y, z - other.z);
        }
    }

    public static Player getPlayerInSight(Player p, int range, boolean lineOfSight) {
        Location observerPos = p.getEyeLocation();
        Vector3D observerDir = new Vector3D(observerPos.getDirection());
        Vector3D observerStart = new Vector3D(observerPos);
        Vector3D observerEnd = observerStart.add(observerDir.multiply(range));

        Player hit = null;

        for (Entity entity : p.getNearbyEntities(range, range, range)) {

            if (entity == p || UtilPlayer.isSpectator(entity))
                continue;

            double theirDist = p.getEyeLocation().distance(entity.getLocation());

            // TODO CONTINUE IF THERE IS A BLOCK IN FRONT OF PLAYER

            Vector3D targetPos = new Vector3D(entity.getLocation());
            Vector3D minimum = targetPos.add(-0.5, 0, -0.5);
            Vector3D maximum = targetPos.add(0.5, 1.67, 0.5);

            if (hasIntersection(observerStart, observerEnd, minimum, maximum)) {
                if (hit == null
                        || hit.getLocation().distanceSquared(observerPos) > entity.getLocation().distanceSquared(observerPos)) {
                    hit = (Player) entity;
                }
            }
        }
        return hit;
    }

    /**
     * AviodAllies doesn't work. Leaving as a param as it sounds like something you may want in the future.
     */
    public static Entity getEntityInSight(Player player, int rangeToScan, boolean avoidAllies, boolean avoidNonLiving,
                                          boolean lineOfSight, float expandBoxesPercentage) {
        // TODO FIX
        return null;
    }

    public static void message(CommandSender sender, String message) {
        message(sender, message, null);
    }

    public static void message(CommandSender sender, String message, String permission) {
        if (sender == null)
            return;

        if (permission != null && !sender.hasPermission(permission)) return;

        sender.sendMessage(message);
    }

    public static Player searchExact(String name) {
        for (Player cur : UtilServer.getPlayers())
            if (cur.getName().equalsIgnoreCase(name))
                return cur;

        return null;
    }

    public static Player searchExact(UUID uuid) {
        return UtilServer.getServer().getPlayer(uuid);
    }

    public static String searchCollection(Player caller, String player, Collection<String> coll, String collName, boolean inform) {
        LinkedList<String> matchList = new LinkedList<String>();

        for (String cur : coll) {
            if (cur.equalsIgnoreCase(player))
                return cur;

            if (cur.toLowerCase().contains(player.toLowerCase()))
                matchList.add(cur);
        }

        // No / Non-Unique
        if (matchList.size() != 1) {
            if (!inform)
                return null;

            // Inform
            message(caller, F.main(collName + " Search", F.elem(String.valueOf(matchList.size())) + " matches for [" + F.elem(player) + "]."));

            if (matchList.size() > 0) {
                String matchString = "";
                for (String cur : matchList)
                    matchString += cur + " ";

                message(caller,
                        F.main(collName + " Search", "" + C.mBody + " Matches [" + C.mElem + matchString + C.mBody + "]."));
            }

            return null;
        }

        return matchList.get(0);
    }

    public static Player searchOnline(CommandSender caller, String player, boolean inform) {
        LinkedList<Player> matchList = new LinkedList<Player>();

        for (Player cur : UtilServer.getPlayers()) {
            if (cur.getName().equalsIgnoreCase(player))
                return cur;

            if (cur.getName().toLowerCase().contains(player.toLowerCase()))
                matchList.add(cur);
        }

        // No / Non-Unique
        if (matchList.size() != 1) {
            if (!inform)
                return null;

            // Inform
            message(caller, F.main("Search", F.elem(String.valueOf(matchList.size())) + " matches for [" + F.elem(player) + "]."));

            if (matchList.size() > 0) {
                String matchString = "";
                for (Player cur : matchList)
                    matchString += F.elem(cur.getName()) + ", ";
                if (matchString.length() > 1)
                    matchString = matchString.substring(0, matchString.length() - 2);

                message(caller,
                        F.main("Online Player Search", "" + C.mBody + "Matches [" + C.mElem + matchString + C.mBody + "]."));
            }

            return null;
        }

        return matchList.get(0);
    }

    public static void searchNetwork(final Callback<List<PlayerStatus>> callback, final CommandSender sender, final String target, final boolean inform) {
        if (!Utility.allowRedis()) {
            callback.run(new ArrayList<>());
            return;
        }
        List<PlayerStatus> players = RedisPlayerManager.getInstance().getPlayers(target);
        if (players.size() > 1) {
            if (inform) {
                List<String> names = new ArrayList<>();
                players.forEach(status -> names.add(status.getName()));
                message(sender, F.main("Network Search", "Multiple matches for " + F.elem(target) + ". " + F.elem(F.concatenate(", ", names)) + "."));
            }
        } else if (players.size() == 0) {
            if (inform) {
                message(sender, F.error("Network Search", "There are no searches for " + F.elem(target) + C.mError + "."));
            }
        }
        callback.run(players);
    }

    public static void searchOffline(final Callback<List<OfflinePlayer>> callback, final CommandSender caller, final String player, final boolean inform) {
        List<OfflinePlayer> list = new ArrayList<>();
        for (OfflinePlayer pls : Bukkit.getOfflinePlayers()) {
            if (pls.getName().equalsIgnoreCase(player)) {
                callback.run(Arrays.asList(pls));
                return;
            }
            if (pls.getName().toLowerCase().contains(player.toLowerCase())) {
                list.add(pls);
            }
        }
        callback.run(list); // XXX Possibly move down code tension between code?
        if (list.size() != 1) {
            if (!inform) return;
            message(caller, F.main("Search", F.elem(String.valueOf(list.size())) + " matches for [" + F.elem(player) + "]."));
            if (list.size() > 0) {
                String matchString = "";
                for (OfflinePlayer pls : list) {
                    String cur = pls.getName();
                    matchString += cur + " ";
                    if (matchString.length() > 1)
                        matchString = matchString.substring(0, matchString.length() - 1);
                    message(caller,
                            F.main("Offline Player Search", "" + C.mBody + "Matches [" + C.mElem + matchString + C.mBody + "]."));
                }
            }
        }
    }

    public static LinkedList<Player> matchOnline(Player caller, String players, boolean inform) {
        LinkedList<Player> matchList = new LinkedList<Player>();

        String failList = "";

        for (String cur : players.split(",")) {
            Player match = searchOnline(caller, cur, inform);

            if (match != null)
                matchList.add(match);

            else
                failList += cur + " ";
        }

        if (inform && failList.length() > 0) {
            failList = failList.substring(0, failList.length() - 1);
            message(caller, F.main("Online Player(s) Search", "" + C.mBody + "Invalid [" + C.mElem + failList + C.mBody + "]."));
        }

        return matchList;
    }

    public static LinkedList<Player> getNearby(Location loc, double maxDist) {
        LinkedList<Player> nearbyMap = new LinkedList<Player>();

        for (Player cur : loc.getWorld().getPlayers()) {
            if (UtilPlayer.isSpectator(cur))
                continue;

            if (cur.isDead())
                continue;

            double dist = loc.toVector().subtract(cur.getLocation().toVector()).length();

            if (dist > maxDist)
                continue;

            for (int i = 0; i < nearbyMap.size(); i++) {
                if (dist < loc.toVector().subtract(nearbyMap.get(i).getLocation().toVector()).length()) {
                    nearbyMap.add(i, cur);
                    break;
                }
            }

            if (!nearbyMap.contains(cur))
                nearbyMap.addLast(cur);
        }

        return nearbyMap;
    }

    public static Player getClosest(Location loc, Collection<Player> ignore) {
        Player best = null;
        double bestDist = 0;

        for (Player cur : loc.getWorld().getPlayers()) {
            if (UtilPlayer.isSpectator(cur))
                continue;

            if (cur.isDead())
                continue;

            if (ignore != null && ignore.contains(cur))
                continue;

            double dist = UtilMath.offset(cur.getLocation(), loc);

            if (best == null || dist < bestDist) {
                best = cur;
                bestDist = dist;
            }
        }

        return best;
    }

    public static Player getClosest(Location loc, Entity ignore) {
        Player best = null;
        double bestDist = 0;

        for (Player cur : loc.getWorld().getPlayers()) {
            if (UtilPlayer.isSpectator(cur))
                continue;

            if (cur.isDead())
                continue;

            if (ignore != null && ignore.equals(cur))
                continue;

            double dist = UtilMath.offset(cur.getLocation(), loc);

            if (best == null || dist < bestDist) {
                best = cur;
                bestDist = dist;
            }
        }

        return best;
    }

    public static void kick(Player player, String module, String message) {
        kick(player, module, message, true);
    }

    public static void kick(Player player, String module, String message, boolean log) {
        if (player == null)
            return;

        String out = ChatColor.RED + module + ChatColor.WHITE + " - " + ChatColor.YELLOW + message;
        player.kickPlayer(out);

        // Log
        if (log)
            System.out.println("Kicked Client [" + player.getName() + "] for [" + module + " - " + message + "]");
    }

    public static HashMap<Player, Double> getInRadius(Location loc, double dR) {
        HashMap<Player, Double> players = new HashMap<Player, Double>();

        for (Player cur : loc.getWorld().getPlayers()) {
            if (UtilPlayer.isSpectator(cur))
                continue;

            double offset = UtilMath.offset(loc, cur.getLocation());

            if (offset < dR)
                players.put(cur, 1 - (offset / dR));
        }

        return players;
    }

    public static HashMap<Player, Double> getPlayersInPyramid(Player player, double angleLimit, double distance) {
        HashMap<Player, Double> players = new HashMap<Player, Double>();

        for (Player cur : player.getWorld().getPlayers()) {
            if (UtilPlayer.isSpectator(cur))
                continue;

            //Get lower offset (eye to eye, eye to feet)
            double offset = Math.min(UtilMath.offset(player.getEyeLocation(), cur.getEyeLocation()),
                    UtilMath.offset(player.getEyeLocation(), cur.getLocation()));

            if (offset < distance && UtilAlg.isTargetInPlayerPyramid(player, cur, angleLimit))
                players.put(cur, 1 - (offset / distance));
        }

        return players;
    }

    public static void health(Player player, double mod) {
        if (player.isDead())
            return;

        double health = player.getHealth() + mod;

        if (health < 0)
            health = 0;

        if (health > player.getMaxHealth())
            health = player.getMaxHealth();

        player.setHealth(health);
    }

    public static void hunger(Player player, int mod) {
        if (player.isDead())
            return;

        int hunger = player.getFoodLevel() + mod;

        if (hunger < 0)
            hunger = 0;

        if (hunger > 20)
            hunger = 20;

        player.setFoodLevel(hunger);
    }

    public static boolean isOnline(String name) {
        return (searchExact(name) != null);
    }

    public static String safeNameLength(String name) {
        if (name.length() > 16)
            name = name.substring(0, 16);

        return name;
    }

    @Warning(reason = "Method hasn't been finished")
    public static boolean isChargingBow(Player player) {
        if (!UtilGear.isMat(player.getItemInHand(), Material.BOW))
            return false;

        throw new UnsupportedOperationException("This method still needs to be worked on! Please contact a developer.");
    }

    public static Object getHandle(Player player) {
        return CraftPlayerMethodGetHandle.of(player).call();
    }

    public static Object getConnection(Player player) {
        return EntityPlayerFieldPlayerConnection.of(getHandle(player)).get();
    }

    public static void sendPacket(Player player, Object... packets) {
        Validate.notEmpty(packets, "Packet array empty.");

        Object connection = getConnection(player);

        for (Object packet : packets) {
            Validate.isTrue(!packet.getClass().isInstance(ClassPacket.getRealClass()), "'" + packet + "' isn't packet.");
            PlayerConnectionMethodSendPacket.of(connection).call(packet);
        }
    }

    public static boolean isSpectator(Entity player) {
        if (player instanceof Player) {
            Object handle = CraftPlayerMethodGetHandle.of(player).call();
            return (boolean) EntityPlayerMethodIsSpectator.of(handle).call();
        }
        return false;
    }
}