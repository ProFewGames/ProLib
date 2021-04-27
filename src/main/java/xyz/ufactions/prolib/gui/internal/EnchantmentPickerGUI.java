package xyz.ufactions.prolib.gui.internal;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import xyz.ufactions.prolib.api.Module;
import xyz.ufactions.prolib.gui.GUIFuture;
import xyz.ufactions.prolib.gui.button.Button;
import xyz.ufactions.prolib.libs.C;
import xyz.ufactions.prolib.libs.F;
import xyz.ufactions.prolib.libs.ItemBuilder;
import xyz.ufactions.prolib.libs.ResponseLib;

import java.util.ArrayList;
import java.util.List;

public class EnchantmentPickerGUI extends GUIFuture<Module, List<EnchantmentPickerGUI.EnchantmentInformation>> {

    private final List<EnchantmentInformation> enchantments;

    public EnchantmentPickerGUI(Module plugin) {
        super(plugin, C.mHead + C.Bold + "Choose an enchantment", GUIFiller.NONE);

        this.enchantments = new ArrayList<>();

        for (Enchantment enchantment : Enchantment.values()) {
            addButton(new Button<Module>(plugin, new ItemBuilder(Material.BOOK)
                    .name(C.mBody + F.capitalizeFirstLetter(enchantment.getName().replaceAll("_", " ")))
                    .enchant(enchantment, 1)) {

                @Override
                public void onClick(Player player, ClickType clickType) {
                    if (clickType == ClickType.SHIFT_LEFT)
                        complete(enchantments);
                    else
                        ResponseLib.getInstance().responseInteger(level -> {
                            enchantments.add(new EnchantmentInformation(enchantment, level));
                            openInventory(player);
                        }, plugin, player);
                }
            });
        }
    }

    public static class EnchantmentInformation {
        private final Enchantment enchantment;
        private final int level;

        public EnchantmentInformation(Enchantment enchantment, int level) {
            this.enchantment = enchantment;
            this.level = level;
        }

        public Enchantment getEnchantment() {
            return enchantment;
        }

        public int getLevel() {
            return level;
        }
    }
}