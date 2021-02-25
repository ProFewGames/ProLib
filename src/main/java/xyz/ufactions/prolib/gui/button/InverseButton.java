package xyz.ufactions.prolib.gui.button;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.ufactions.prolib.api.Module;

public abstract class InverseButton<T extends Module> extends Button<T> {

    protected boolean inverse = false;

    public InverseButton(T plugin) {
        super(plugin);
    }

    public InverseButton(T plugin, int slot) {
        super(plugin, null, slot);
    }

    public InverseButton(T plugin, long refreshTime, int slot) {
        super(plugin, null, refreshTime, slot);
    }

    public final boolean isInversed() {
        return inverse;
    }

    public final void setInversed(boolean inverse) {
        this.inverse = inverse;
    }

    public final void reverse() {
        setInversed(!isInversed());
    }

    public boolean canInverse(Player player) {
        return true;
    }

    public abstract ItemStack getInverse(boolean inversed);

    @Override
    public final ItemStack getItem() {
        return getInverse(inverse);
    }
}