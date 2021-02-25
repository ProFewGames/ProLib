package xyz.ufactions.prolib.gui.internal;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import xyz.ufactions.prolib.api.Module;
import xyz.ufactions.prolib.gui.GUI;
import xyz.ufactions.prolib.gui.GUIFuture;
import xyz.ufactions.prolib.gui.button.Button;
import xyz.ufactions.prolib.libs.C;
import xyz.ufactions.prolib.libs.ItemBuilder;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class MaterialPickerGUI extends GUIFuture<Module, Material> {

    public MaterialPickerGUI(Module plugin) {
        super(plugin, C.mHead + C.Bold + "Select a material", GUI.GUIFiller.NONE);

        List<Material> materials = Arrays.asList(Material.values());
        materials.sort(Comparator.comparing(Enum::name));

        for (Material material : materials) {
            addButton(new Button<Module>(plugin, new ItemBuilder(material)) {

                @Override
                public void onClick(Player player, ClickType clickType) {
                    complete(material);
                    player.closeInventory();
                }
            });
        }
    }

    @Override
    public void onClose(Player player) {
        if (!hasCompleted()) complete(null);
    }
}