package xyz.ufactions.prolib.gui.button;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import xyz.ufactions.prolib.api.Module;
import xyz.ufactions.prolib.gui.GUI;

public abstract class IButton<T extends Module> {

    protected GUI<?> opener; // The GUI that this button is in
    protected final T Plugin; // The module linked to this button

    public IButton(T plugin) {
        Plugin = plugin;
    }

    public final void setOpener(GUI<?> opener) {
        this.opener = opener;
    }

    public abstract ItemStack getItem();

    public abstract int getSlot();

    public abstract void onClick(Player player, ClickType type);
}