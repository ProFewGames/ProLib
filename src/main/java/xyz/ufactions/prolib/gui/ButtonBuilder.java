package xyz.ufactions.prolib.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import xyz.ufactions.prolib.api.Module;
import xyz.ufactions.prolib.gui.button.Button;
import xyz.ufactions.prolib.libs.BiCallback;
import xyz.ufactions.prolib.libs.ItemBuilder;

public final class ButtonBuilder {

    public static ButtonBuilder instance(Module module) {
        return new ButtonBuilder(module);
    }

    private final Module plugin;

    private int slot = -1;
    private long refreshTime = -1;
    private ItemBuilder builder;
    private BiCallback<Player, ClickType> onClick;

    public ButtonBuilder(Module plugin) {
        this.plugin = plugin;
    }

    /**
     * @param slot The position that this button will be placed in the inventory.
     *             Enter -1 for automatic positioning
     */
    public ButtonBuilder slot(int slot) {
        this.slot = slot;
        return this;
    }

    /**
     * @param refreshTime How often will this item update,
     *                    Enter -1 to disable item updating(default)
     */
    public ButtonBuilder refreshTime(long refreshTime) {
        this.refreshTime = refreshTime;
        return this;
    }

    /**
     * @param builder The item that will appear in the inventory
     */
    public ButtonBuilder item(ItemBuilder builder) {
        this.builder = builder;
        return this;
    }

    /**
     * @param predicate This will be executed everytime the button is clicked
     */
    public ButtonBuilder onClick(BiCallback<Player, ClickType> predicate) {
        this.onClick = predicate;
        return this;
    }

    /**
     * @return An instance of {@link Button} with the given parameters.
     */
    public Button<?> build() {
        return new Button<Module>(plugin, builder, refreshTime, slot) {

            @Override
            public void onClick(Player player, ClickType clickType) {
                if (onClick != null) onClick.run(player, clickType);
            }
        };
    }
}