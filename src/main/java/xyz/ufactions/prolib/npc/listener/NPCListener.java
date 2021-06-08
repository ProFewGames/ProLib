package xyz.ufactions.prolib.npc.listener;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import xyz.ufactions.prolib.animation.Animation;
import xyz.ufactions.prolib.npc.NPCModule;
import xyz.ufactions.prolib.npc.data.NPC;

public class NPCListener implements Listener {

    private final NPCModule plugin;

    public NPCListener(NPCModule plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onTest(PlayerJoinEvent e) {
        NPC npc = plugin.createNPC(EntityType.ZOMBIE, e.getPlayer().getLocation(), false);
        npc.setName(plugin.getPlugin(), new Animation("HELLO WORLD", Animation.AnimationType.WAVE));
        npc.spawn();
    }

    @EventHandler
    public void onInteract(PlayerInteractEntityEvent e) {
        NPC npc = getNPC(e.getRightClicked());
        if (npc != null) {
            npc.doInteract(e.getPlayer());
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        NPC npc = getNPC(e.getEntity());
        if (npc != null) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDamageByEntity(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player)) return;
        NPC npc = getNPC(e.getEntity());
        if (npc != null) {
            npc.doInteract(((Player) e.getDamager()));
        }
    }

    @EventHandler
    public void onCombust(EntityCombustEvent e) {
        if (getNPC(e.getEntity()) != null) e.setCancelled(true);
    }

    @EventHandler
    public void onTarget(EntityTargetLivingEntityEvent e) {
        if (getNPC(e.getTarget()) != null) e.setTarget(null);
    }

    private NPC getNPC(Entity entity) {
        if (entity == null) return null;
        for (NPC npc : plugin.getNPCs()) {
            if (!npc.isAlive()) continue;
            if (npc.getEntity().getEntityId() != entity.getEntityId()) continue;
            return npc;
        }
        return null;
    }
}