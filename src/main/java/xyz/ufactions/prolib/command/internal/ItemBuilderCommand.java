package xyz.ufactions.prolib.command.internal;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import xyz.ufactions.prolib.command.CommandBase;
import xyz.ufactions.prolib.gui.GUI;
import xyz.ufactions.prolib.gui.button.SelfSortingButton;
import xyz.ufactions.prolib.libs.*;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class ItemBuilderCommand extends CommandBase<DummyModule> {

    public ItemBuilderCommand(DummyModule plugin) {
        super(plugin, "itembuilder", "ib");

        setPermission("prolib.command.itembuilder");
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        if (!isPlayer(sender)) return;
        Player player = (Player) sender;

        new MaterialPickerGUI(material -> {
            final ItemBuilder builder = new ItemBuilder(material);
            ResponseLib.getInstance().response(glowing -> {
                builder.glow(glowing);
                player.getInventory().setItem(player.getInventory().firstEmpty(), builder.build());
            }, Plugin, false).openInventory(player);
        }, Plugin).openInventory(player);
    }

    private static class MaterialPickerGUI extends GUI<DummyModule> {

        public MaterialPickerGUI(Callback<Material> callback, DummyModule plugin) {
            super(plugin, C.mHead + C.Bold + "Select a material", GUIFiller.NONE);

            List<Material> materials = Arrays.asList(Material.values());
            materials.sort(Comparator.comparing(Enum::name));

            for (Material material : materials) {
                addButton(new SelfSortingButton<DummyModule>(plugin, new ItemBuilder(material)) {

                    @Override
                    public void onClick(Player player, ClickType type) {
                        player.closeInventory();
                        callback.run(material);
                    }
                });
            }
        }
    }
}