package xyz.ufactions.prolib.gui;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import xyz.ufactions.prolib.api.IModule;
import xyz.ufactions.prolib.api.Module;
import xyz.ufactions.prolib.gui.button.Button;
import xyz.ufactions.prolib.libs.Callback;

import java.util.*;
import java.util.function.Predicate;

public final class GUIBuilder<T extends IModule> {

    public static <T extends IModule> GUIBuilder<T> instance(T plugin, String name, GUI.GUIFiller filler) {
        return new GUIBuilder<>(plugin, name, filler);
    }

    private final T plugin;
    private final String name;
    private final GUI.GUIFiller filler;

    private int size = -1;
    private ChatColor color;

    private Map<GUI.GUIAction, Callback<Player>> actions = new HashMap<>();
    private List<Button<?>> buttons = new ArrayList<>();
    private Predicate<Player> canClose;
    private Predicate<Player> canOpen;

    private GUIBuilder(T plugin, String name, GUI.GUIFiller filler) {
        this.plugin = plugin;
        this.name = name;
        this.filler = filler;
    }

    public GUIBuilder<T> size(int size) {
        this.size = size;
        return this;
    }

    public GUIBuilder<T> color(ChatColor color) {
        this.color = color;
        return this;
    }

    public GUIBuilder<T> addButton(Button<?>... button) {
        this.buttons.addAll(Arrays.asList(button));
        return this;
    }

    public GUIBuilder<T> onActionPerformed(GUI.GUIAction action, Callback<Player> player) {
        this.actions.put(action, player);
        return this;
    }

    public GUIBuilder<T> canOpen(Predicate<Player> predicate) {
        this.canOpen = predicate;
        return this;
    }

    public GUIBuilder<T> canClose(Predicate<Player> predicate) {
        this.canClose = predicate;
        return this;
    }

    public GUI<T> build() {
        GUI<T> gui = new GUI<T>(plugin, name, size, filler) {

            @Override
            public boolean canClose(Player player) {
                if (canClose == null) return super.canClose(player);
                return canClose.test(player);
            }

            @Override
            public boolean canOpenInventory(Player player) {
                if (canOpen == null) return super.canOpenInventory(player);
                return canOpen.test(player);
            }

            @Override
            public void onActionPerformed(GUIAction action, Player player) {
                Callback<Player> callback = actions.get(action);
                if (callback != null) callback.run(player);
            }
        };
        if (color != null) gui.setPaneColor(color);
        gui.addButton(buttons.toArray(new Button[0]));
        return gui;
    }
}