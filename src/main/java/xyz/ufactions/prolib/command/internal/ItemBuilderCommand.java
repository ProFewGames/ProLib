package xyz.ufactions.prolib.command.internal;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.ufactions.prolib.command.CommandBase;
import xyz.ufactions.prolib.gui.internal.EnchantmentPickerGUI;
import xyz.ufactions.prolib.gui.internal.MaterialPickerGUI;
import xyz.ufactions.prolib.libs.*;

import java.util.List;

public class ItemBuilderCommand extends CommandBase<DummyModule> {

    private final String prefix = "Item Builder";

    public ItemBuilderCommand(DummyModule plugin) {
        super(plugin, "itembuilder", "ib");

        setPermission("prolib.command.itembuilder");
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        if (!isPlayer(sender)) return;
        Player player = (Player) sender;

        UtilPlayer.message(player, F.line());
        UtilPlayer.message(player, F.main(prefix, "Creating Session..."));
        Plugin.runAsync(() -> {
            UtilPlayer.message(player, F.main(prefix, "Select a material"));
            Material material = new MaterialPickerGUI(Plugin).open(player).getThrown();
            if (material == null) {
                UtilPlayer.message(player, F.error(prefix, "Did not select any material."));
                UtilPlayer.message(player, F.line());
                return;
            }
            final ItemBuilder builder = new ItemBuilder(material);
            UtilPlayer.message(player, F.main(prefix, "Material " + F.elem(F.capitalizeFirstLetter(
                    material.name().replaceAll("_", " "))) + " Chosen."));

            UtilPlayer.message(player, F.main(prefix, "Choose enchantments"));
            List<EnchantmentPickerGUI.EnchantmentInformation> enchantments = new EnchantmentPickerGUI(Plugin)
                    .open(player).getThrown();

            for (EnchantmentPickerGUI.EnchantmentInformation information : enchantments) {
                UtilPlayer.message(player, F.main(prefix, "Enchantment " + F.elem(F.capitalizeFirstLetter(
                        information.getEnchantment().getName())) + " level " + F.elem(
                        String.valueOf(information.getLevel())) + " added."));
                builder.enchant(information.getEnchantment(), information.getLevel());
            }

            UtilPlayer.message(player, F.main(prefix, "Closing Session..."));
            Plugin.runSync(() -> {
                UtilInv.insert(player, builder.build());
                UtilPlayer.message(player, F.main(prefix, "Item Crafted!"));
                UtilPlayer.message(player, F.line());
            });
        });
    }
}