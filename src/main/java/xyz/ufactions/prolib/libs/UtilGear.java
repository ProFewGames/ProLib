package xyz.ufactions.prolib.libs;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

public class UtilGear {
    private static final HashSet<Material> _axeSet = new HashSet<Material>();
    private static final HashSet<Material> _swordSet = new HashSet<Material>();
    private static final HashSet<Material> _maulSet = new HashSet<Material>();
    private static final HashSet<Material> pickSet = new HashSet<Material>();
    private static final HashSet<Material> diamondSet = new HashSet<Material>();
    private static final HashSet<Material> goldSet = new HashSet<Material>();

    private static final Set<Material> leatherSet = new HashSet<>();

    public static boolean isLeather(ItemStack item) {
        if (item == null) return false;

        if (leatherSet.isEmpty()) {
            leatherSet.add(Material.LEATHER_HELMET);
            leatherSet.add(Material.LEATHER_CHESTPLATE);
            leatherSet.add(Material.LEATHER_LEGGINGS);
            leatherSet.add(Material.LEATHER_BOOTS);
        }
        return leatherSet.contains(item);
    }

    public static boolean isAxe(ItemStack item) {
        if (item == null)
            return false;

        if (_axeSet.isEmpty()) {
            _axeSet.add(Material.WOODEN_AXE);
            _axeSet.add(Material.STONE_AXE);
            _axeSet.add(Material.IRON_AXE);
            _axeSet.add(Material.GOLDEN_AXE);
            _axeSet.add(Material.DIAMOND_AXE);
        }

        return _axeSet.contains(item.getType());
    }

    public static boolean isSword(ItemStack item) {

        if (item == null)
            return false;

        if (_swordSet.isEmpty()) {
            _swordSet.add(Material.WOODEN_SWORD);
            _swordSet.add(Material.STONE_SWORD);
            _swordSet.add(Material.IRON_SWORD);
            _swordSet.add(Material.GOLDEN_SWORD);
            _swordSet.add(Material.DIAMOND_SWORD);
        }

        return _swordSet.contains(item.getType());
    }

    public static boolean isShovel(ItemStack item) {
        if (item == null)
            return false;

        if (_maulSet.isEmpty()) {
            _maulSet.add(Material.WOODEN_SHOVEL);
            _maulSet.add(Material.STONE_SHOVEL);
            _maulSet.add(Material.IRON_SHOVEL);
            _maulSet.add(Material.GOLDEN_SHOVEL);
            _maulSet.add(Material.DIAMOND_SHOVEL);
        }

        return _maulSet.contains(item.getType());
    }

    public static HashSet<Material> scytheSet = new HashSet<Material>();

    public static boolean isHoe(ItemStack item) {
        if (item == null)
            return false;

        if (scytheSet.isEmpty()) {
            scytheSet.add(Material.WOODEN_HOE);
            scytheSet.add(Material.STONE_HOE);
            scytheSet.add(Material.IRON_HOE);
            scytheSet.add(Material.GOLDEN_HOE);
            scytheSet.add(Material.DIAMOND_HOE);
        }

        return scytheSet.contains(item.getType());
    }

    public static boolean isPickaxe(ItemStack item) {
        if (item == null)
            return false;

        if (pickSet.isEmpty()) {
            pickSet.add(Material.WOODEN_PICKAXE);
            pickSet.add(Material.STONE_PICKAXE);
            pickSet.add(Material.IRON_PICKAXE);
            pickSet.add(Material.GOLDEN_PICKAXE);
            pickSet.add(Material.DIAMOND_PICKAXE);
        }

        return pickSet.contains(item.getType());
    }

    public static boolean isDiamond(ItemStack item) {
        if (item == null)
            return false;

        if (diamondSet.isEmpty()) {
            diamondSet.add(Material.DIAMOND_SWORD);
            diamondSet.add(Material.DIAMOND_AXE);
            diamondSet.add(Material.DIAMOND_SHOVEL);
            diamondSet.add(Material.DIAMOND_HOE);
        }

        return diamondSet.contains(item.getType());
    }

    public static boolean isGold(ItemStack item) {
        if (item == null)
            return false;

        if (goldSet.isEmpty()) {
            goldSet.add(Material.GOLDEN_SWORD);
            goldSet.add(Material.GOLDEN_AXE);
        }

        return goldSet.contains(item.getType());
    }

    public static boolean isBow(ItemStack item) {
        if (item == null)
            return false;

        return item.getType() == Material.BOW;
    }

    public static boolean isWeapon(ItemStack item) {
        return isAxe(item) || isSword(item);
    }

    public static boolean isMat(ItemStack item, Material mat) {
        if (item == null)
            return false;

        return item.getType() == mat;
    }

    public static boolean isRepairable(ItemStack item) {
        return (item.getType().getMaxDurability() > 0);
    }

    private static final HashSet<Material> helmetSet = new HashSet<>();

    public static boolean isHelmet(ItemStack item) {
        if (item == null) return false;

        if (helmetSet.isEmpty()) {
            helmetSet.add(Material.DIAMOND_HELMET);
            helmetSet.add(Material.CHAINMAIL_HELMET);
            helmetSet.add(Material.GOLDEN_HELMET);
            helmetSet.add(Material.IRON_HELMET);
            helmetSet.add(Material.LEATHER_HELMET);
            helmetSet.add(Material.TURTLE_HELMET);
        }
        return helmetSet.contains(item.getType());
    }

    private static final HashSet<Material> chestplateSet = new HashSet<>();

    public static boolean isChestplate(ItemStack item) {
        if (item == null) return false;

        if (chestplateSet.isEmpty()) {
            chestplateSet.add(Material.CHAINMAIL_CHESTPLATE);
            chestplateSet.add(Material.DIAMOND_CHESTPLATE);
            chestplateSet.add(Material.GOLDEN_CHESTPLATE);
            chestplateSet.add(Material.LEATHER_CHESTPLATE);
            chestplateSet.add(Material.IRON_CHESTPLATE);
        }
        return chestplateSet.contains(item.getType());
    }

    private static final HashSet<Material> leggingSet = new HashSet<>();

    public static boolean isLeggings(ItemStack item) {
        if (item == null) return false;

        if (leggingSet.isEmpty()) {
            leggingSet.add(Material.LEATHER_LEGGINGS);
            leggingSet.add(Material.CHAINMAIL_LEGGINGS);
            leggingSet.add(Material.GOLDEN_LEGGINGS);
            leggingSet.add(Material.DIAMOND_LEGGINGS);
            leggingSet.add(Material.IRON_LEGGINGS);
        }
        return leggingSet.contains(item.getType());
    }

    private static final HashSet<Material> bootSet = new HashSet<>();

    public static boolean isBoots(ItemStack item) {
        if (item == null) return false;

        if (bootSet.isEmpty()) {
            bootSet.add(Material.LEATHER_BOOTS);
            bootSet.add(Material.IRON_BOOTS);
            bootSet.add(Material.GOLDEN_BOOTS);
            bootSet.add(Material.DIAMOND_BOOTS);
            bootSet.add(Material.CHAINMAIL_BOOTS);
        }
        return bootSet.contains(item.getType());
    }
}
