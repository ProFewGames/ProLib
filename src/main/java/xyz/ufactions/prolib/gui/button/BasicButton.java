package xyz.ufactions.prolib.gui.button;

import org.bukkit.inventory.ItemStack;
import xyz.ufactions.prolib.api.Module;
import xyz.ufactions.prolib.libs.ItemBuilder;

public abstract class BasicButton<T extends Module> extends IButton<T> {

    private final ItemBuilder builder;
    private final int slot;

    public BasicButton(T plugin, ItemBuilder builder, int slot) {
        super(plugin);

        this.builder = builder;
        this.slot = slot;
    }

    public ItemBuilder getBuilder() {
        return builder;
    }

    @Override
    public ItemStack getItem() {
        return builder.build();
    }

    @Override
    public int getSlot() {
        return slot;
    }
}