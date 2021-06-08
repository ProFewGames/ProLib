package xyz.ufactions.prolib.npc.file;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import xyz.ufactions.prolib.libs.FileHandler;
import xyz.ufactions.prolib.libs.ItemBuilder;
import xyz.ufactions.prolib.libs.UtilLoc;
import xyz.ufactions.prolib.npc.NPCModule;
import xyz.ufactions.prolib.npc.data.NPC;
import xyz.ufactions.prolib.script.ScriptManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NPCFile extends FileHandler<NPCModule> {

    public NPCFile(NPCModule npcModule) {
        super(npcModule, "npcs.yml");
    }

    public List<NPC> getNPCs() {
        List<NPC> npcs = new ArrayList<>();
        if (!getConfig().isConfigurationSection("npcs")) return npcs;
        for (String key : getConfigurationSection("npcs").getKeys(false)) {
            String path = "npcs." + key;
            int id = getInt(path + ".id");
            EntityType type = EntityType.valueOf(getString(path + ".type"));
            Location location = UtilLoc.deserialize(getString(path + ".location"));
            NPC npc = new NPC(id, type, location);

            if (contains(path + ".name")) npc.setName(getString(path + ".name"));

            if (contains(path + ".inhand"))
                npc.setItemInHand(ItemBuilder.itemFromConfig(getConfig(), path + ".inhand"));

            if (contains(path + ".helmet")) npc.setHelmet(ItemBuilder.itemFromConfig(getConfig(), path + ".helmet"));

            if (contains(path + ".chestplate"))
                npc.setChestplate(ItemBuilder.itemFromConfig(getConfig(), path + ".chestplate"));

            if (contains(path + ".leggings"))
                npc.setLeggings(ItemBuilder.itemFromConfig(getConfig(), path + ".leggings"));

            if (contains(path + ".boots"))
                npc.setBoots(ItemBuilder.itemFromConfig(getConfig(), path + ".boots"));

            if (contains(path + ".commands")) {
                for (final String command : getStringList(path + ".commands")) {
                    npc.addInteraction(player -> player.performCommand(ScriptManager.getInstance().replace(player, command)));
                }
            }

            npcs.add(npc);
        }
        return npcs;
    }

    public void saveNPC(NPC npc) {
        String path = "npcs." + npc.getID();
        set(path + ".id", npc.getID());
        set(path + ".type", npc.getType().name());
        set(path + ".location", UtilLoc.serialize(npc.getLocation()));
        if (npc.getName() != null)
            set(path + ".name", npc.getName());
        if (npc.getItemInHand() != null)
            ItemBuilder.saveToConfig(npc.getItemInHand(), getConfig(), path + ".inhand");
        if (npc.getHelmet() != null)
            ItemBuilder.saveToConfig(npc.getHelmet(), getConfig(), path + ".helmet");
        if (npc.getChestplate() != null)
            ItemBuilder.saveToConfig(npc.getChestplate(), getConfig(), path + ".chestplate");
        if (npc.getLeggings() != null)
            ItemBuilder.saveToConfig(npc.getLeggings(), getConfig(), path + ".leggings");
        if (npc.getBoots() != null)
            ItemBuilder.saveToConfig(npc.getBoots(), getConfig(), path + ".boots");

        try {
            save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addCommand(NPC npc, String command) {
        String path = "npcs." + npc.getID();
        List<String> commands = getStringList(path + ".commands");
        commands.add(command);
        set(path + ".commands", commands);

        try {
            save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Simplicity / Accessible to user - Most searching should be done through uuid
    public int getAndIncrementID() {
        int index = getInt("index", 1);
        set("index", ++index);

        try {
            save();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return index;
    }
}