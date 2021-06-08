package xyz.ufactions.prolib.npc.data;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import xyz.ufactions.prolib.animation.Animation;
import xyz.ufactions.prolib.api.MegaPlugin;
import xyz.ufactions.prolib.libs.C;
import xyz.ufactions.prolib.libs.Callback;
import xyz.ufactions.prolib.libs.ItemBuilder;
import xyz.ufactions.prolib.libs.UtilEntity;
import xyz.ufactions.prolib.script.ScriptManager;

import java.util.ArrayList;
import java.util.List;

public class NPC {

    private final int id;
    private final EntityType type;

    // Location
    private Location location;

    // Meta Data
    private final List<Callback<Player>> onInteract;
    private String name;

    // Inventory
    private ItemBuilder inHand;
    private ItemBuilder helmet;
    private ItemBuilder chestplate;
    private ItemBuilder leggings;
    private ItemBuilder boots;

    // Ambient
    private MegaPlugin plugin;
    private LivingEntity entity;

    public NPC(int id, EntityType type) {
        this(id, type, null);
    }

    public NPC(int id, EntityType type, Location location) {
        this.id = id;
        this.type = type;
        this.location = location;
        this.onInteract = new ArrayList<>();
        if (this.location == null) this.location = new Location(Bukkit.getWorlds().get(0), 0, 0, 0);
    }

    // Methods

    public void spawn() {
        if (entity != null) return;
        if (getLocation().getWorld() == null) {
            if (plugin != null) {
                plugin.warning("NPC", "This NPC does not have a valid world.");
            }
            return;
        }
        entity = (LivingEntity) getLocation().getWorld().spawnEntity(getLocation(), type);
        entity.setCanPickupItems(false);
        entity.setRemoveWhenFarAway(false);
        UtilEntity.setAI(entity, false);
        if (getName() != null) {
            entity.setCustomName(C.color(ScriptManager.getInstance().replace(getName())));
            entity.setCustomNameVisible(true);
        }
        if (getItemInHand() != null) entity.getEquipment().setItemInHand(getItemInHand().build());
        if (getHelmet() != null) entity.getEquipment().setHelmet(getHelmet().build());
        if (getChestplate() != null) entity.getEquipment().setChestplate(getChestplate().build());
        if (getLeggings() != null) entity.getEquipment().setLeggings(getLeggings().build());
        if (getBoots() != null) entity.getEquipment().setBoots(getBoots().build());
    }

    public void despawn() {
        if (entity == null) return;
        entity.remove();
        entity = null;
    }

    public boolean isAlive() {
        return entity != null && !entity.isDead();
    }

    public void doInteract(Player player) {
        for (Callback<Player> interaction : getInteractions()) {
            interaction.run(player);
        }
    }

    // Location Setter

    public void setLocation(Location location) {
        this.location = location;
    }

    // Meta Setter

    public void setName(String name) {
        this.name = name;
    }

    // XXX - Memory Leak Issue - Does not know if temporary NPC or when the NPC spawns/despawns etc... add memcache
    public void setName(Plugin plugin, Animation animation) {
        animation.setAnimatable(string -> {
            if (!isAlive()) return;
            if (!getEntity().isCustomNameVisible()) getEntity().setCustomNameVisible(true);
            getEntity().setCustomName(ScriptManager.getInstance().replace(string));
        });
        animation.start(plugin);
    }

    public void addInteraction(Callback<Player> onInteract) {
        this.onInteract.add(onInteract);
    }

    // Inventory Setter

    public void setItemInHand(ItemBuilder inHand) {
        this.inHand = inHand;
    }

    public void setHelmet(ItemBuilder helmet) {
        this.helmet = helmet;
    }

    public void setChestplate(ItemBuilder chestplate) {
        this.chestplate = chestplate;
    }

    public void setLeggings(ItemBuilder leggings) {
        this.leggings = leggings;
    }

    public void setBoots(ItemBuilder boots) {
        this.boots = boots;
    }

    // Build Getter

    public int getID() {
        return id;
    }

    public EntityType getType() {
        return type;
    }

    // Build Setter

    public void setPlugin(MegaPlugin plugin) {
        if (this.plugin != null) return;
        this.plugin = plugin;
    }

    // Location Getter

    public Location getLocation() {
        return location;
    }

    // Meta Getter

    public String getName() {
        return name;
    }

    public List<Callback<Player>> getInteractions() {
        return onInteract;
    }

    // Inventory Getter

    public ItemBuilder getItemInHand() {
        return inHand;
    }

    public ItemBuilder getHelmet() {
        return helmet;
    }

    public ItemBuilder getChestplate() {
        return chestplate;
    }

    public ItemBuilder getLeggings() {
        return leggings;
    }

    public ItemBuilder getBoots() {
        return boots;
    }

    // Ambient Getter

    public LivingEntity getEntity() {
        return entity;
    }
}