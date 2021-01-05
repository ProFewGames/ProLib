package xyz.ufactions.prolib.libs;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TitleAPI {

    public static void sendTitle(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            sendTitle(player, title, subtitle, fadeIn, stay, fadeOut);
        }
    }

    public static void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeout) {
        setTimings(player, fadeIn, stay, fadeout);
        player.sendMessage("Unimplemented:TitleAPI$sendTitle");
    }

    public static void sendTabHF(String header, String footer) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            sendTabHF(player, header, footer);
        }
    }

    public static void sendTabHF(Player player, String header, String footer) {
        player.setPlayerListHeaderFooter(header, footer);
    }


    public static void sendActionBar(String message, int stay) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            sendActionBar(player, message, stay);
        }
    }

    public static void sendActionBar(Player player, String message, int stay) {
        setTimings(player, 0, stay, 0);
        player.sendMessage("Unimplemented:TitleAPI$sendActionBar");
    }

    private static void setTimings(Player player, int fadeIn, int stay, int fadeOut) {
        player.sendMessage("Unimplemented:TitleAPI$setTimings");
    }
}
