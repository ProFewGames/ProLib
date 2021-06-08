package xyz.ufactions.prolib.npc;

import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import xyz.ufactions.prolib.api.Module;
import xyz.ufactions.prolib.npc.command.NPCCommand;
import xyz.ufactions.prolib.npc.data.NPC;
import xyz.ufactions.prolib.npc.file.NPCFile;
import xyz.ufactions.prolib.npc.listener.NPCListener;
import xyz.ufactions.prolib.script.ScriptManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NPCModule extends Module {

    private NPCFile file;

    private Map<org.bukkit.plugin.Plugin, List<NPC>> npcs;

    @Override
    public void enable() {
        this.file = new NPCFile(this);
        this.npcs = new HashMap<>();
        reload();

        registerEvents(new NPCListener(this));
        addCommand(new NPCCommand(this));
    }

    public void reload() {
        this.file.reload();
        for (List<NPC> list : this.npcs.values()) {
            for (NPC npc : list) {
                npc.despawn();
            }
        }
        this.npcs.getOrDefault(getPlugin(), new ArrayList<>()).clear();
        for (NPC npc : this.file.getNPCs()) {
            if (!this.npcs.containsKey(getPlugin())) this.npcs.put(getPlugin(), new ArrayList<>());
            this.npcs.get(getPlugin()).add(npc);
        }
        runSync(() -> {
            for (List<NPC> list : this.npcs.values()) {
                for (NPC npc : list) {
                    npc.spawn();
                }
            }
        });
    }

    @Override
    public void disable() {
        for (List<NPC> list : this.npcs.values()) {
            for (NPC npc : list) {
                npc.despawn();
            }
        }
    }

    public NPC createNPC(EntityType type, Location location, boolean save) {
        return createNPC(getPlugin(), type, location, save);
    }

    public NPC createNPC(org.bukkit.plugin.Plugin owner, EntityType type, Location location, boolean save) {
        Validate.notNull(owner);

        NPC npc = new NPC(file.getAndIncrementID(), type);
        npc.setLocation(location);
        npc.setPlugin(getPlugin());
        if (save)
            saveNPC(npc);
        if (!npcs.containsKey(owner)) npcs.put(owner, new ArrayList<>());
        npcs.get(owner).add(npc);
        return npc;
    }

    public void addCommand(NPC npc, String command) {
        file.addCommand(npc, command);

        npc.addInteraction(player -> player.performCommand(ScriptManager.getInstance().replace(player, command)));
    }

    public void saveNPC(NPC npc) {
        file.saveNPC(npc);
    }

    public List<NPC> getNPCs() {
        List<NPC> npcs = new ArrayList<>();
        for (List<NPC> list : this.npcs.values()) {
            npcs.addAll(list);
        }
        return npcs;
    }

    public List<NPC> getNPCs(org.bukkit.plugin.Plugin plugin) {
        return this.npcs.getOrDefault(plugin, new ArrayList<>());
    }

    public NPC getNPC(int id) {
        for (List<NPC> list : this.npcs.values())
            for (NPC npc : list)
                if (npc.getID() == id) return npc;
        return null;
    }
}