package xyz.ufactions.prolib.libs;

import net.minecraft.server.v1_14_R1.ChatMessage;
import net.minecraft.server.v1_14_R1.Packet;
import net.minecraft.server.v1_14_R1.PacketPlayOutTitle;
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
        Packet<?> a = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, new ChatMessage(title));
        Packet<?> b = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, new ChatMessage(subtitle));
        UtilPlayer.sendPacket(player, a, b);
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

    public static void sendActionBar(Player p, String message, int stay) {
        setTimings(p, 0, stay, 0);
        UtilPlayer.sendPacket(p, new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.ACTIONBAR, new ChatMessage(message)));
    }

    private static void setTimings(Player player, int fadeIn, int stay, int fadeOut) {
        UtilPlayer.sendPacket(player, new PacketPlayOutTitle(fadeIn, stay, fadeOut));
    }
}
