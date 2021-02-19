package xyz.ufactions.prolib.libs;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import xyz.ufactions.prolib.api.Module;
import xyz.ufactions.prolib.gui.GUI;
import xyz.ufactions.prolib.gui.button.BasicButton;

import java.util.concurrent.atomic.AtomicBoolean;

public class ResponseLib {

    private static ResponseLib instance;

    public static ResponseLib getInstance() {
        if (instance == null) instance = new ResponseLib();
        return instance;
    }

    public <T extends Module> GUI<T> response(final Callback<Boolean> callback, final T plugin, final boolean canClose) {
        return new GUI<T>(plugin, C.cGold + C.Bold + "Confirmation", 27, GUI.GUIFiller.PANE) {

            private final AtomicBoolean chosen = new AtomicBoolean(false);

            @Override
            public boolean canClose(Player player) {
//                return !canClose && !chosen.get();
                return true; // TODO FIX
            }

            @Override
            public void register() {
                setPaneColor(ChatColor.AQUA);
                addButton(new BasicButton<T>(plugin, ColorLib.cw(ChatColor.GREEN).name(C.cGreen + C.Bold + "YES"), 11) {

                    @Override
                    public void onClick(Player player, ClickType type) {
                        player.closeInventory();
                        callback.run(true);
                    }
                });
                addButton(new BasicButton<T>(plugin, ColorLib.cw(ChatColor.RED).name(C.cRed + C.Bold + "NO"), 15) {

                    @Override
                    public void onClick(Player player, ClickType type) {
                        player.closeInventory();
                        callback.run(false);
                    }
                });
            }
        };
    }
}