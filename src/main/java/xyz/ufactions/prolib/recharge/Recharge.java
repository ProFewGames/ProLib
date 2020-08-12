package xyz.ufactions.prolib.recharge;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.ufactions.prolib.libs.F;
import xyz.ufactions.prolib.libs.UtilPlayer;
import xyz.ufactions.prolib.libs.UtilServer;
import xyz.ufactions.prolib.libs.UtilTime;
import xyz.ufactions.prolib.updater.UpdateType;
import xyz.ufactions.prolib.updater.event.UpdateEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class Recharge implements Listener {

    public static Recharge Instance;

    public HashSet<String> informSet = new HashSet<>();
    public HashMap<String, HashMap<String, RechargeData>> _recharge = new HashMap<>();

    protected Recharge(JavaPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public static void Initialize(JavaPlugin plugin) {
        if (Instance == null)
            Instance = new Recharge(plugin);
    }

    @EventHandler
    public void PlayerDeath(PlayerDeathEvent event) {
        Get(event.getEntity().getName()).clear();
    }

    public HashMap<String, RechargeData> Get(String name) {
        if (!_recharge.containsKey(name))
            _recharge.put(name, new HashMap<>());

        return _recharge.get(name);
    }

    public HashMap<String, RechargeData> Get(Player player) {
        return Get(player.getName());
    }

    @EventHandler
    public void update(UpdateEvent event) {
        if (event.getType() != UpdateType.TICK)
            return;

        recharge();
    }

    public void recharge() {
        for (Player cur : UtilServer.getPlayers()) {
            LinkedList<String> rechargeList = new LinkedList<String>();

            //Check Recharged
            for (String ability : Get(cur).keySet()) {
                if (Get(cur).get(ability).Update())
                    rechargeList.add(ability);
            }

            //Inform Recharge
            for (String ability : rechargeList) {
                Get(cur).remove(ability);

                //Event
                RechargedEvent rechargedEvent = new RechargedEvent(cur, ability);
                UtilServer.getServer().getPluginManager().callEvent(rechargedEvent);

                if (informSet.contains(ability))
                    UtilPlayer.message(cur, F.main("Recharge", "You can use " + F.skill(ability) + "."));
            }
        }
    }

    public boolean use(Player player, String ability, long recharge, boolean inform, boolean attachItem) {
        return use(player, ability, ability, recharge, inform, attachItem);
    }

    public boolean use(Player player, String ability, String abilityFull, long recharge, boolean inform, boolean attachItem) {
        return use(player, ability, abilityFull, recharge, inform, attachItem, false);
    }

    public boolean use(Player player, String ability, long recharge, boolean inform, boolean attachItem, boolean attachDurability) {
        return use(player, ability, ability, recharge, inform, attachItem, attachDurability);
    }

    public boolean use(Player player, String ability, String abilityFull, long recharge, boolean inform, boolean attachItem, boolean attachDurability) {
        if (recharge == 0)
            return true;

        //Ensure Expirey
        recharge();

        //Lodge Recharge Msg
        if (inform && recharge > 1000)
            informSet.add(ability);

        //Recharging
        if (Get(player).containsKey(ability)) {
            if (inform) {
                UtilPlayer.message(player, F.error("Recharge", "You cannot use " + F.skill(abilityFull) + " for " +
                        F.time(UtilTime.convertString((Get(player).get(ability).GetRemaining()), 1, UtilTime.TimeUnit.FIT)) + "."));
            }

            return false;
        }

        //Insert
        UseRecharge(player, ability, recharge, attachItem, attachDurability);

        return true;
    }

    public void useForce(Player player, String ability, long recharge) {
        useForce(player, ability, recharge, false);
    }

    public void useForce(Player player, String ability, long recharge, boolean attachItem) {
        UseRecharge(player, ability, recharge, attachItem, false);
    }

    public boolean usable(Player player, String ability) {
        return usable(player, ability, false);
    }

    public boolean usable(Player player, String ability, boolean inform) {
        if (!Get(player).containsKey(ability))
            return true;

        if (Get(player).get(ability).GetRemaining() <= 0) {
            return true;
        } else {
            if (inform)
                UtilPlayer.message(player, F.error("Recharge", "You cannot use " + F.skill(ability) + " for " +
                        F.time(UtilTime.convertString((Get(player).get(ability).GetRemaining()), 1, UtilTime.TimeUnit.FIT)) + "."));

            return false;
        }
    }

    public void UseRecharge(Player player, String ability, long recharge, boolean attachItem, boolean attachDurability) {
        //Event
        RechargeEvent rechargeEvent = new RechargeEvent(player, ability, recharge);
        UtilServer.getServer().getPluginManager().callEvent(rechargeEvent);

        Get(player).put(ability, new RechargeData(this, player, ability, player.getItemInHand(),
                rechargeEvent.GetRecharge(), attachItem, attachDurability));
    }

    public void recharge(Player player, String ability) {
        Get(player).remove(ability);
    }

    public void clearPlayer(PlayerQuitEvent event) {
        _recharge.remove(event.getPlayer().getName());
    }

    public void setDisplayForce(Player player, String ability, boolean displayForce) {
        if (!_recharge.containsKey(player.getName()))
            return;

        if (!_recharge.get(player.getName()).containsKey(ability))
            return;

        _recharge.get(player.getName()).get(ability).DisplayForce = displayForce;
    }

    public void setCountdown(Player player, String ability, boolean countdown) {
        if (!_recharge.containsKey(player.getName()))
            return;

        if (!_recharge.get(player.getName()).containsKey(ability))
            return;

        _recharge.get(player.getName()).get(ability).Countdown = countdown;
    }

    public void Reset(Player player) {
        _recharge.put(player.getName(), new HashMap<>());
    }

    public void Reset(Player player, String stringContains) {
        HashMap<String, RechargeData> data = _recharge.get(player.getName());

        if (data == null)
            return;

        data.keySet().removeIf(key -> key.toLowerCase().contains(stringContains.toLowerCase()));
    }

    public void debug(Player player, String ability) {
        if (!_recharge.containsKey(player.getName())) {
            player.sendMessage("No Recharge Map.");
            return;
        }

        if (!_recharge.get(player.getName()).containsKey(ability)) {
            player.sendMessage("Ability Not Found.");
            return;
        }

        _recharge.get(player.getName()).get(ability).debug(player);
    }
}
