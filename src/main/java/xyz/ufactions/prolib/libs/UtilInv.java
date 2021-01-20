package xyz.ufactions.prolib.libs;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;
import xyz.ufactions.enchantmentlib.EnchantmentLib;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public class UtilInv {

    public static void addDullEnchantment(ItemStack itemStack) {
        itemStack.addEnchantment(getDullEnchantment(), 1);
    }

    public static void removeDullEnchantment(ItemStack itemStack) {
        itemStack.removeEnchantment(getDullEnchantment());
    }

    public static Enchantment getDullEnchantment() {
        return EnchantmentLib.getGlowEnchantment();
    }

    public static boolean insert(Player player, ItemStack stack) {
        player.getInventory().addItem(stack);
        player.updateInventory();
        return true;
    }

    public static boolean contains(Player player, Material item, byte data, int required) {
        return contains(player, null, item, data, required);
    }

    public static boolean contains(Player player, String itemNameContains, Material item, byte data, int required) {
        return contains(player, itemNameContains, item, data, required, true, true);
    }

    public static boolean contains(Player player, String itemNameContains, Material item, byte data, int required, boolean checkArmor, boolean checkCursor) {
        for (ItemStack stack : getItems(player, checkArmor, checkCursor)) {
            if (required <= 0) {
                return true;
            }

            if (stack == null) {
                continue;
            }
            if (stack.getType() != item) {
                continue;
            }
            if (stack.getAmount() <= 0) {
                continue;
            }
            if (data >= 0 && stack.getData() != null && stack.getData().getData() != data) {
                continue;
            }
            if (itemNameContains != null && (stack.getItemMeta().getDisplayName() == null ||
                    !stack.getItemMeta().getDisplayName().contains(itemNameContains))) {
                continue;
            }
            required -= stack.getAmount();
        }

        return (required <= 0);
    }

    public static boolean remove(Player player, Material item, byte data, int toRemove) {
        if (!contains(player, item, data, toRemove)) {
            return false;
        }
        for (Iterator<Integer> iterator = player.getInventory().all(item).keySet().iterator(); iterator.hasNext(); ) {
            int i = iterator.next();
            if (toRemove <= 0) {
                continue;
            }
            ItemStack stack = player.getInventory().getItem(i);

            if (stack.getData() == null || stack.getData().getData() == data) {
                int foundAmount = stack.getAmount();

                if (toRemove >= foundAmount) {
                    toRemove -= foundAmount;
                    player.getInventory().setItem(i, null);

                    continue;
                }
                stack.setAmount(foundAmount - toRemove);
                player.getInventory().setItem(i, stack);
                toRemove = 0;
            }
        }


        player.updateInventory();
        return true;
    }

    public static void Clear(Player player) {
        PlayerInventory inv = player.getInventory();

        inv.clear();
        inv.setArmorContents(new ItemStack[4]);
        player.setItemOnCursor(new ItemStack(Material.AIR));

        player.saveData();
    }

    public static ArrayList<ItemStack> getItems(Player player) {
        return getItems(player, true, true);
    }

    public static ArrayList<ItemStack> getItems(Player player, boolean getArmor, boolean getCursor) {
        ArrayList<ItemStack> items = new ArrayList<ItemStack>();
        PlayerInventory inv = player.getInventory();
        for (ItemStack item : inv.getContents()) {
            if (item != null && item.getType() != Material.AIR) {
                items.add(item.clone());
            }
        }
        if (getArmor) {
            for (ItemStack item : inv.getArmorContents()) {
                if (item != null && item.getType() != Material.AIR) {
                    items.add(item.clone());
                }
            }
        }
        if (getCursor) {
            ItemStack cursorItem = player.getItemOnCursor();

            if (cursorItem != null && cursorItem.getType() != Material.AIR) {
                items.add(cursorItem.clone());
            }
        }
        return items;
    }

    public static void drop(Player player, boolean clear) {
        for (ItemStack cur : getItems(player)) {
            player.getWorld().dropItemNaturally(player.getLocation(), cur);
        }

        if (clear)
            Clear(player);
    }

    public static void Update(Entity player) {
        if (!(player instanceof Player)) {
            return;
        }
        ((Player) player).updateInventory();
    }


    public static int removeAll(Player player, Material type, byte data) {
        HashSet<ItemStack> remove = new HashSet<ItemStack>();
        int count = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == type && (data == -1 || item.getData() == null || (item.getData() != null && item.getData().getData() == data))) {
                count += item.getAmount();
                remove.add(item);
            }
        }
        for (ItemStack item : remove) {
            player.getInventory().remove(item);
        }
        return count;
    }

    public static byte GetData(ItemStack stack) {
        if (stack == null) {
            return 0;
        }
        if (stack.getData() == null) {
            return 0;
        }
        return stack.getData().getData();
    }

    public static boolean IsItem(ItemStack item, Material type, byte data) {
        return IsItem(item, null, type, data);
    }

    public static boolean IsItem(ItemStack item, String name, Material type, byte data) {
        if (item == null) {
            return false;
        }
        if (item.getType() != type) {
            return false;
        }
        if (data != -1 && GetData(item) != data) {
            return false;
        }
        return !(name != null && (
                item.getItemMeta().getDisplayName() == null || !item.getItemMeta().getDisplayName().contains(name)));
    }

    public static void DisallowMovementOf(InventoryClickEvent event, String name, Material type, byte data, boolean inform) {
        DisallowMovementOf(event, name, type, data, inform, false);
    }

    public static void DisallowMovementOf(InventoryClickEvent event, String name, Material type, byte data, boolean inform, boolean allInventorties) {
        if (!allInventorties && event.getInventory().getType() == InventoryType.CRAFTING) {
            return;
        }

        if (event.getAction() == InventoryAction.HOTBAR_SWAP ||
                event.getAction() == InventoryAction.HOTBAR_MOVE_AND_READD) {
            boolean match = false;

            if (IsItem(event.getCurrentItem(), name, type, data)) {
                match = true;
            }
            if (IsItem(event.getWhoClicked().getInventory().getItem(event.getHotbarButton()), name, type, data)) {
                match = true;
            }
            if (!match) {
                return;
            }

            if (inform)
                UtilPlayer.message(event.getWhoClicked(),
                        F.main("Inventory", "You cannot hotbar swap " + F.elem(name) + "."));
            event.setCancelled(true);
        } else {

            if (event.getCurrentItem() == null) {
                return;
            }
            IsItem(event.getCurrentItem(), name, type, data);


            if (!IsItem(event.getCurrentItem(), name, type, data)) {
                return;
            }
            if (inform)
                UtilPlayer.message(event.getWhoClicked(), F.main("Inventory", "You cannot move " + F.elem(name) + "."));
            event.setCancelled(true);
        }
    }

    public static void UseItemInHand(Player player) {
        if (player.getItemInHand().getAmount() > 1) {
            player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
        } else {
            player.setItemInHand(null);
        }
        Update(player);
    }

    public static void clone(Inventory from, Inventory to) {
        to.setContents(from.getContents());
    }

    public static String serialize(Inventory inventory) throws IllegalAccessException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream(); BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream)) {
            dataOutput.writeInt(inventory.getSize());

            for (int i = 0; i < inventory.getSize(); i++) {
                dataOutput.writeObject(inventory.getItem(i));
            }

            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            throw new IllegalAccessException("Failed to write serialized data");
        }
    }

    public static Inventory deserialize(String data) throws IllegalAccessException {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data)); BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream)) {
            Inventory inventory = Bukkit.getServer().createInventory(null, dataInput.readInt());

            for (int i = 0; i < inventory.getSize(); i++) {
                inventory.setItem(i, (ItemStack) dataInput.readObject());
            }
            return inventory;
        } catch (Exception e) {
            throw new IllegalAccessException("Failed to read serialized data");
        }
    }
}