package xyz.ufactions.prolib.libs;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import xyz.ufactions.prolib.api.MegaPlugin;

import java.util.ArrayList;
import java.util.List;

public class UtilServer {

    public static List<String> getPlayerNames() {
        return getPlayerNames(getPlayers());
    }

    public static List<String> getPlayerNames(Player[] players) {
        List<String> names = new ArrayList<>();
        for (Player player : players) {
            names.add(player.getName());
        }
        return names;
    }

    public static Player[] getPlayers() {
        return getServer().getOnlinePlayers().toArray(new Player[0]);
    }

    public static Server getServer() {
        return Bukkit.getServer();
    }

    public static MegaPlugin[] getMegaPlugins() {
        List<MegaPlugin> plugins = new ArrayList<>();
        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            if (plugin instanceof MegaPlugin) {
                plugins.add((MegaPlugin) plugin);
            }
        }
        return plugins.toArray(new MegaPlugin[0]);
    }

    public static void broadcast(String message) {
        for (Player cur : getPlayers())
            cur.sendMessage(message);
    }

    public static void broadcastSpecial(String event, String message) {
        for (Player cur : getPlayers()) {
            cur.sendMessage(C.cAqua + C.Bold + event);
            cur.sendMessage(message);
            cur.playSound(cur.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 2f, 0f);
            cur.playSound(cur.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 2f, 0f);
        }
    }

    public static void broadcast(String sender, String message) {
        broadcast("§f§l" + sender + " " + "§b" + message);
    }

    public static void broadcastMagic(String sender, String message) {
        broadcast("§2§k" + message);
    }

    public static double getFilledPercent() {
        return (double) getPlayers().length / (double) UtilServer.getServer().getMaxPlayers();
    }
}
