package xyz.ufactions.prolib.libs;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import xyz.ufactions.prolib.reflection.ReflectionUtils;
import xyz.ufactions.prolib.version.VersionUtils;

import java.lang.reflect.Field;

public class TitleAPI {

    private static TitleAPI instance;

    public static TitleAPI getInstance() {
        if (instance == null) instance = new TitleAPI();
        return instance;
    }

    private final Object[] TitleActions;

    private final ReflectionUtils.RefClass PacketPlayOutPlayerListHeaderFooter;
    private final ReflectionUtils.RefClass PacketPlayOutTitle;
    private final ReflectionUtils.RefClass EnumTitleAction;
    private final ReflectionUtils.RefClass IChatBaseComponent;
    private final ReflectionUtils.RefClass ChatMessage;

    private TitleAPI() {
        PacketPlayOutTitle = ReflectionUtils.getRefClass("{nms}.PacketPlayOutTitle");
        EnumTitleAction = ReflectionUtils.getRefClass("{nms}.PacketPlayOutTitle$EnumTitleAction");
        IChatBaseComponent = ReflectionUtils.getRefClass("{nms}.IChatBaseComponent");
        ChatMessage = ReflectionUtils.getRefClass("{nms}.ChatMessage");
        PacketPlayOutPlayerListHeaderFooter = ReflectionUtils.getRefClass("{nms}.PacketPlayOutPlayerListHeaderFooter");

        this.TitleActions = this.EnumTitleAction.getRealClass().getEnumConstants();
    }

    public void sendTitle(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            sendTitle(player, title, subtitle, fadeIn, stay, fadeOut);
        }
    }

    public void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        Object serialized = this.ChatMessage.getConstructor(String.class, Object[].class).create(title, new Object[0]);
        Object packet = this.PacketPlayOutTitle
                .getConstructor(this.EnumTitleAction, this.IChatBaseComponent, int.class, int.class, int.class)
                .create(TitleActions[0], serialized, fadeIn * 20, stay * 20, fadeOut * 20);
        UtilPlayer.sendPacket(player, packet);

        serialized = this.ChatMessage.getConstructor(String.class, Object[].class).create(subtitle, new Object[0]);
        packet = this.PacketPlayOutTitle
                .getConstructor(this.EnumTitleAction, this.IChatBaseComponent, int.class, int.class, int.class)
                .create(TitleActions[1], serialized, fadeIn * 20, stay * 20, fadeOut * 20);
        UtilPlayer.sendPacket(player, packet);
    }

    public void clearTitle(Player player) {
        Object packet = this.PacketPlayOutTitle
                .getConstructor(this.EnumTitleAction, this.IChatBaseComponent)
                .create(TitleActions[4], null);

        UtilPlayer.sendPacket(player, packet);
    }

    public void sendTabHF(String header, String footer) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            sendTabHF(player, header, footer);
        }
    }

    public void sendTabHF(Player player, String header, String footer) {
        if (VersionUtils.getVersion().greaterOrEquals(VersionUtils.Version.V1_9)) {
            player.setPlayerListHeaderFooter(header, footer);
        } else {
            Object packet = this.PacketPlayOutPlayerListHeaderFooter.getConstructor(this.IChatBaseComponent).create(createChatMessage(header));

            try {
                Field field = this.PacketPlayOutPlayerListHeaderFooter.getRealClass().getDeclaredField("b");
                field.setAccessible(true);
                field.set(packet, createChatMessage(footer));
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }

            UtilPlayer.sendPacket(player, packet);
        }
    }

    public void sendActionBar(String message, int stay) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            sendActionBar(player, message, stay);
        }
    }

    public void sendActionBar(Player player, String message, int stay) {
        setTimings(player, 0, stay, 0);
        player.sendMessage("Unimplemented:TitleAPI$sendActionBar");
    }

    private void setTimings(Player player, int fadeIn, int stay, int fadeOut) {
        player.sendMessage("Unimplemented:TitleAPI$setTimings");
    }

    private Object createChatMessage(String string) {
        return this.ChatMessage.getConstructor(String.class, Object[].class).create(string, new Object[0]);
    }
}
