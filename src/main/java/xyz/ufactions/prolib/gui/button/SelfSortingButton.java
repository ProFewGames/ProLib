package xyz.ufactions.prolib.gui.button;

import org.bukkit.inventory.ItemStack;
import xyz.ufactions.prolib.api.Module;
import xyz.ufactions.prolib.libs.ItemBuilder;

public abstract class SelfSortingButton<T extends Module> extends IButton<T> {

    private final ItemBuilder builder;

    public SelfSortingButton(T plugin, ItemBuilder builder) {
        super(plugin);

        this.builder = builder;
    }

    @Override
    public ItemStack getItem() {
        return builder.build();
    }

    @Override
    public final int getSlot() { // Not needed but its abstract
        return -1;
    }
}