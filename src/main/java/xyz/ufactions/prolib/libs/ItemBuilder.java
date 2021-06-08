package xyz.ufactions.prolib.libs;

import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import xyz.ufactions.prolib.script.ScriptManager;
import xyz.ufactions.prolib.version.VersionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Notice as of 1.13.2 MegaBukkit support for
 * Material ID's has been stopped please use Material
 * Names.
 */
public class ItemBuilder {

    private final ItemStack item;
    private boolean glowing = false;

    public ItemBuilder(Material material) {
        this(material, 0);
    }

    public ItemBuilder(Material material, int data) {
        this(material, 1, data);
    }

    /**
     * Regular Item Creator
     *
     * @param material The material of the item
     * @param amount   How much of the material do you want the item
     * @param data     The item's data
     */
    public ItemBuilder(Material material, int amount, int data) {
        this(new ItemStack(material, amount, (short) data));
    }

    /**
     * Creator from pre-existing item
     *
     * @param item The item we're building for
     */
    public ItemBuilder(ItemStack item) {
        Validate.notNull(item, "Item must not be null.");

        this.item = item;
    }

    /**
     * Skull Creator
     *
     * @param owner The person who's skull we're creating
     */
    public ItemBuilder(OfflinePlayer owner) {
        if (VersionUtils.getVersion().greaterOrEquals(VersionUtils.Version.V1_9))
            this.item = new ItemStack(Material.PLAYER_HEAD);
        else
            this.item = new ItemStack(Material.getMaterial("SKULL_ITEM"));
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        meta.setOwningPlayer(owner);
        item.setItemMeta(meta);
    }

    public ItemBuilder addAttribute(Attribute attribute, AttributeModifier modifier) {
        ItemMeta meta = this.item.getItemMeta();
        meta.addAttributeModifier(attribute, modifier);
        this.item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder enchant(Enchantment enchantment, int amount) {
        Validate.notNull(enchantment);

        this.item.addUnsafeEnchantment(enchantment, amount);
        return this;
    }

    public ItemBuilder glow(boolean glow) {
        if (glow) {
            UtilInv.addDullEnchantment(this.item);
        }
        glowing = glow;
        return this;
    }

    public boolean isGlowing() {
        return glowing;
    }

    public ItemBuilder amount(int amount) {
        this.item.setAmount(amount);
        return this;
    }

    public ItemBuilder name(String name) {
        ItemMeta meta = this.item.getItemMeta();
        meta.setDisplayName(cc((name.startsWith("&") ? "" : "&f") + name));
        this.item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder lore(List<String> lore) {
        ItemMeta meta = this.item.getItemMeta();
        List<String> finLore = new ArrayList<String>();
        for (String line : lore) {
            finLore.add(cc("&7" + ScriptManager.getInstance().replace(line)));
        }
        meta.setLore(finLore);
        this.item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder lore(String... strings) {
        ItemMeta meta = this.item.getItemMeta();
        List<String> lore = new ArrayList<String>();
        for (String string : strings) {
            lore.add(cc("&7" + string));
        }
        meta.setLore(lore);
        this.item.setItemMeta(meta);
        return this;
    }

    public ItemStack build() {
        return this.item;
    }

    public static void saveToConfig(ItemStack item, FileConfiguration config, String path) {
        saveToConfig(new ItemBuilder(item), config, path);
    }

    public static void saveToConfig(ItemBuilder item, FileConfiguration config, String path) {
        config.set(path + ".material", item.build().getType());
        config.set(path + ".data", item.item.getData().getData());
        config.set(path + ".amount", item.item.getAmount());
        config.set(path + ".glow", item.isGlowing());
        config.set(path + ".name", item.item.getItemMeta().getDisplayName());
        config.set(path + ".lore", item.item.getItemMeta().getLore());

        List<String> enchantments = new ArrayList<>();
        for (Enchantment enchantment : item.item.getEnchantments().keySet()) {
            enchantments.add(enchantment.getName() + ":" + item.item.getEnchantments().get(enchantment));
        }

        config.set(path + ".enchantments", enchantments);
    }

    public static ItemBuilder itemFromConfig(FileConfiguration config, String path) {
        Material material = Material.getMaterial(config.getString(path + ".material"));
        if (material == null)
            return null;

        int data = config.getInt(path + ".data", 0);
        int amount = config.getInt(path + ".amount", 0);

        ItemBuilder builder = new ItemBuilder(material, amount, data);

        boolean glow = config.getBoolean(path + ".glow", false);
        builder.glow(glow);

        String name = config.getString(path + ".name");
        if (name != null)
            builder.name(name);

        List<String> lore = config.getStringList(path + ".lore");
        builder.lore(lore);

        List<String> enchantments = config.getStringList(path + ".enchantments");
        for (String enchantment : enchantments) {
            String enchantmentName = enchantment;
            int level = 1;
            if (enchantmentName.contains(":")) {
                if (!UtilMath.isInteger(enchantmentName.split(":")[1]))
                    System.out.println("Failed to fetch level '" + enchantmentName.split(":")[1] + "' for path '" +
                            path + "' in configuration '" + config.getName() + "'.");
                else
                    level = Integer.parseInt(enchantmentName.split(":")[1]);
                if (level <= 0)
                    level = 1;
                enchantmentName = enchantmentName.split(":")[0];
            }
            Enchantment ench = Enchantment.getByName(enchantmentName);
            if (ench == null) {
                System.out.println("Failed to fetch enchantment '" + enchantmentName + "' for path '" +
                        path + "' in configuration '" + config.getName() + "'.");
                continue;
            }
            builder.enchant(ench, level);
        }
        return builder;
    }

    private String cc(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }
}