package xyz.ufactions.prolib.libs;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import xyz.ufactions.prolib.api.Module;
import xyz.ufactions.prolib.gui.GUI;
import xyz.ufactions.prolib.gui.button.Button;
import xyz.ufactions.prolib.gui.button.InverseButton;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class ResponseLib {

    private static ResponseLib instance;

    public static ResponseLib getInstance() {
        if (instance == null) instance = new ResponseLib();
        return instance;
    }

    public void responseString(final Callback<String> callback, final Module plugin, final Player player) {
        player.closeInventory();
        TitleAPI.getInstance().sendTitle(player, C.mHead + C.Bold + "Type response in chat",
                C.cGray + C.Italics + "Left click to cancel", 5, Integer.MAX_VALUE, 3);
        plugin.registerEvents(new Listener() {

            @EventHandler
            public void onInventoryOpen(InventoryOpenEvent e) {
                if (e.getPlayer() == player) {
                    UtilPlayer.message(player,
                            F.error("Response", "You may not do that action until you have given a response."));
                    e.setCancelled(true);
                }
            }

            @EventHandler
            public void onLeftClick(PlayerInteractEvent e) {
                if (UtilEvent.isAction(e, UtilEvent.ActionType.L)) {
                    if (e.getPlayer() == player) {
                        handleResponse("");
                    }
                }
            }

            @EventHandler
            public void onChat(AsyncPlayerChatEvent e) {
                if (e.getPlayer() == player) {
                    e.setCancelled(true);
                    handleResponse(ChatColor.stripColor(e.getMessage()));
                }
            }

            private void handleResponse(String response) {
                HandlerList.unregisterAll(this);
                plugin.runSyncLater(() -> TitleAPI.getInstance().clearTitle(player), 1);
                callback.run(response);
            }
        });
    }

    public void responseInteger(final Callback<Integer> callback, final Module plugin, final Player player) {
        player.closeInventory();
        new GUI<Module>(plugin, "Select a number", 36, GUI.GUIFiller.PANE) {

            private final AtomicInteger integer = new AtomicInteger(0);

            private final Material[] materials = {Material.CHEST, Material.ENDER_CHEST};

            @Override
            public void register() {
                setPaneColor(ChatColor.BLACK);

                addButton(new Button<Module>(plugin, null, 1000, 13) {

                    @Override
                    public void onClick(Player player, ClickType type) {
                    }

                    @Override
                    public ItemStack getItem() {
                        return new ItemBuilder(materials[UtilMath.r(materials.length)])
                                .name("Current number: " + integer.get()).build();
                    }
                });

                for (int i = 1; i < 4; ++i) {
                    final int interval = i == 1 ? 1 : i == 2 ? 5 : 10;
                    addButton(new InverseButton<Module>(plugin, -1, 18 + (i * 2)) {

                        @Override
                        public void onClick(Player player, ClickType type) {
                            if (type.isRightClick()) {
                                integer.addAndGet(interval);
                            } else if (type.isLeftClick()) {
                                integer.addAndGet(-interval);
                            }
                        }


                        @Override
                        public ItemStack getInverse(boolean inversed) {
                            return (inversed ? ColorLib.cb(ChatColor.RED) : ColorLib.cb(ChatColor.GREEN))
                                    .name("Interval=" + interval)
                                    .lore("* Left Click to Add *", "* Right Click to Remove *").build();
                        }
                    });
                }
            }

            @Override
            public void onClose(Player player) {
                callback.run(integer.get());
            }
        }.openInventory(player);
    }

    public <T extends Module> void responseBoolean(final Callback<Boolean> callback, final T plugin,
                                                   final Player player) {
        player.closeInventory();
        new GUI<T>(plugin, C.cGold + C.Bold + "Confirmation", 27, GUI.GUIFiller.PANE) {

            private final AtomicReference<Callback<Boolean>> reference = new AtomicReference<>();

            @Override
            public void register() {
                this.reference.set(callback);

                setPaneColor(ChatColor.AQUA);
                addButton(new Button<T>(plugin, ColorLib.cw(ChatColor.GREEN).name(C.cGreen + C.Bold + "YES"), 11) {

                    @Override
                    public void onClick(Player player, ClickType type) {
                        reference.set(null);
                        player.closeInventory();
                        callback.run(true);
                    }
                });
                addButton(new Button<T>(plugin, ColorLib.cw(ChatColor.RED).name(C.cRed + C.Bold + "NO"), 15) {

                    @Override
                    public void onClick(Player player, ClickType type) {
                        reference.set(null);
                        player.closeInventory();
                        callback.run(false);
                    }
                });
            }

            @Override
            public void onClose(Player player) {
                if (reference.get() != null) reference.get().run(false);
            }
        }.openInventory(player);
    }
}