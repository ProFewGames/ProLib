package xyz.ufactions.prolib.gui.button;

import org.apache.commons.lang.Validate;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import xyz.ufactions.prolib.api.IModule;
import xyz.ufactions.prolib.api.Module;
import xyz.ufactions.prolib.gui.GUI;
import xyz.ufactions.prolib.libs.ItemBuilder;

public abstract class Button<T extends IModule> {

    private final int slot;

    private final long refreshTime;

    private long lastUpdated = System.currentTimeMillis();

    protected final T plugin;
    protected final ItemStack item;

    protected GUI<?> gui;

    public Button(T plugin) {
        this(plugin, null);
    }

    public Button(T plugin, ItemBuilder builder) {
        this(plugin, builder, -1);
    }

    public Button(T plugin, ItemBuilder builder, int slot) {
        this(plugin, builder, -1, slot);
    }

    public Button(T plugin, ItemBuilder builder, long refreshTime, int slot) {
        this.plugin = plugin;
        this.item = builder.build();
        this.refreshTime = refreshTime;
        this.slot = slot;
    }

    public abstract void onClick(Player player, ClickType clickType);

    public final void setGUI(GUI<?> gui) {
        this.gui = gui;
    }

    public final boolean isSelfSorting() {
        return slot <= -1;
    }

    public final long getRefreshTime() {
        return refreshTime;
    }

    public final void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public final long getLastUpdated() {
        return lastUpdated;
    }

    public final int getSlot() {
        return slot;
    }

    public ItemStack getItem() {
        Validate.notNull(item, "ItemBuilder is null. Override getItem within button class or set a builder" +
                " when constructing this button class.");

        return item;
    }
}