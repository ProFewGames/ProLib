/*
package xyz.ufactions.prolib.command.internal;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import xyz.ufactions.prolib.command.CommandBase;
import xyz.ufactions.prolib.libs.*;
import xyz.ufactions.prolib.updater.UpdateType;
import xyz.ufactions.prolib.updater.event.UpdateEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class ItemBuilderCommand extends CommandBase<DummyModule> implements Listener {

    private static class ItemBuilderSession {

        public UUID uuid;
        public ItemBuilder builder;
        public long lastSessionCheckin;
        public QuestionStage questionStage = QuestionStage.GLOW;

        private enum QuestionStage {
            GLOW, NAME, LORE, ENCHANTMENT;
        }
    }

    private final List<ItemBuilderSession> sessions = new ArrayList<>();
    private final long sessionExpiry = 60000; // 60 Seconds
    private final String prefix = "Item Builder";

    public ItemBuilderCommand(DummyModule plugin) {
        super(plugin, "itembuilder", "ib");

        setPermission("prolib.command.itembuilder");
        plugin.registerEvents(this);
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        if (!isPlayer(sender)) return;
        Player player = (Player) sender;

        for (ItemBuilderSession session : sessions) {
            if (session.uuid == player.getUniqueId()) {
                UtilPlayer.message(player, F.error(prefix, "You already have an active session."));
                return;
            }
        }
        ItemBuilderSession session = new ItemBuilderSession();
        session.uuid = player.getUniqueId();
        session.lastSessionCheckin = System.currentTimeMillis();
        UtilPlayer.message(player, F.main(prefix, "Enter the material type..."));
        UtilPlayer.message(player, F.main(prefix, C.cGray + C.Italics + "All input is applied in chat. If no input" +
                " is received within " + (sessionExpiry / 1000) + " seconds the session will end and your chat will return" +
                " back to normal."));
        UtilPlayer.message(player, F.main(prefix, C.cGray + C.Italics + "Additionally you can type \"CANCEL\" to end" +
                "your current session."));
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        for (ItemBuilderSession session : sessions) {
            if (session.uuid == player.getUniqueId()) {
                session.lastSessionCheckin = System.currentTimeMillis();
                String text = ChatColor.stripColor(e.getMessage());
                if (text.equalsIgnoreCase("cancel")) {
                    sessions.remove(session);
                    UtilPlayer.message(player, F.error(prefix, "Session Cancelled."));
                    return;
                }
                if (session.builder == null) {
                    Material material;
                    try {
                        material = Material.valueOf(text);
                    } catch (EnumConstantNotPresentException ex) {
                        UtilPlayer.message(player, F.error(prefix, "Invalid material!"));
                        UtilPlayer.message(player, F.main(prefix, "Enter the material type..."));
                        return;
                    }
                    session.builder = new ItemBuilder(material);
                    session.builder.amount(0);
                    UtilPlayer.message(player, F.main(prefix, "Enter item amount..."));
                    return;
                }
                if (session.builder.build().getAmount() == 0) {
                    if (!UtilMath.isInteger(text)) {
                        UtilPlayer.message(player, F.error(prefix, "Invalid number!"));
                        UtilPlayer.message(player, F.main(prefix, "Enter item amount..."));
                        return;
                    }
                    int amount = Integer.parseInt(text);
                    if (amount <= 0) {
                        UtilPlayer.message(player, F.error(prefix, "Amount may not be 0."));
                        UtilPlayer.message(player, F.main(prefix, "Enter item amount..."));
                        return;
                    }
                    session.builder.amount(amount);
                    UtilPlayer.message(player, F.main(prefix, "Will this item have the glowing effect?"));
                    UtilPlayer.message(player, F.main(prefix, F.yn(true) + " or " + F.yn(false)));
                    return;
                }
                if (session.questionStage == ItemBuilderSession.QuestionStage.GLOW) {
                    if (!text.equalsIgnoreCase("yes") && !text.equalsIgnoreCase("no")) {
                        UtilPlayer.message(player, F.error(prefix, "Invalid response!"));
                        UtilPlayer.message(player, F.main(prefix, "Will this item have the glowing effect?"));
                        UtilPlayer.message(player, F.main(prefix, F.yn(true) + " or " + F.yn(false)));
                        return;
                    }
                    boolean glow = text.equalsIgnoreCase("yes") || (!text.equalsIgnoreCase("no"));
                    session.builder.glow(glow);
                    session.questionStage = ItemBuilderSession.QuestionStage.NAME;
                    UtilPlayer.message(player, F.main(prefix, "What will be the name of this item? Type \"NONE\" for" +
                            " no name."));
                    return;
                }
                if (session.questionStage == ItemBuilderSession.QuestionStage.NAME) {
                    if (!text.equalsIgnoreCase("none"))
                        session.builder.name(e.getMessage());
                    session.questionStage = ItemBuilderSession.QuestionStage.LORE;
                    UtilPlayer.message(player, F.main(prefix, "Enter any lore the item will have in chat. Once you" +
                            " are done type \"DONE\" and the session will move on."));
                    return;
                }
                if (session.questionStage == ItemBuilderSession.QuestionStage.LORE) {
                    if (text.equalsIgnoreCase("done")) {
                        session.questionStage = ItemBuilderSession.QuestionStage.ENCHANTMENT;
                        UtilPlayer.message(player, F.main(prefix, "Enter any enchantment within the following format:" +
                                "'SHARPNESS:500' with 'SHARPNESS' being the name and '500' being the level. Once you are" +
                                " done type \"DONE\" and the session will move on."));
                        return;
                    }
                    session.builder.lore(e.getMessage());
                    UtilPlayer.message(player, F.main(prefix, "Added."));
                    return;
                }
                if (session.questionStage == ItemBuilderSession.QuestionStage.ENCHANTMENT) {
                    if (text.equalsIgnoreCase("done")) {
                        sessions.remove(session);
                        return;
                    }
                }
            }
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        for (ItemBuilderSession session : sessions) {
            if (session.uuid == e.getPlayer().getUniqueId()) {
                sessions.remove(session);
                break;
            }
        }
    }

    @EventHandler
    public void onUpdate(UpdateEvent e) {
        if (e.getType() != UpdateType.FAST) return;

        Iterator<ItemBuilderSession> iterator = sessions.iterator();
        while (iterator.hasNext()) {
            ItemBuilderSession session = iterator.next();
            Player player = Bukkit.getPlayer(session.uuid);
            if (UtilTime.elapsed(session.lastSessionCheckin, sessionExpiry)) {
                iterator.remove();
                if (player != null)
                    UtilPlayer.message(player, F.main(prefix, "Session has expired."));
            } else {
                TitleAPI.sendActionBar(player, UtilTime.convertString(0, 0, UtilTime.TimeUnit.SECONDS)
                        + "until session expires.", 5);
            }
        }
    }
}*/
