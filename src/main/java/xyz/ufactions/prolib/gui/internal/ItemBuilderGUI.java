package xyz.ufactions.prolib.gui.internal;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import xyz.ufactions.prolib.gui.GUI;
import xyz.ufactions.prolib.gui.button.SelfSortingButton;
import xyz.ufactions.prolib.libs.DummyModule;
import xyz.ufactions.prolib.libs.F;
import xyz.ufactions.prolib.libs.ItemBuilder;

public class ItemBuilderGUI extends GUI<DummyModule> {

    public ItemBuilderGUI(DummyModule plugin) {
        super(plugin, "Choose a material", GUIFiller.PANE);

        for (Material material : Material.values()) {
            addButton(new SelfSortingButton<DummyModule>(plugin, new ItemBuilder(material).name(F.capitalizeFirstLetter(material.name()))) {

                @Override
                public void onClick(Player player, ClickType type) {
                }
            });
        }
    }
}
