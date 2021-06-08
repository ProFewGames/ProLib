package xyz.ufactions.prolib.visibility;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import xyz.ufactions.prolib.recharge.Recharge;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class VisibilityData {

    private final HashMap<UUID, Boolean> shouldHide = new HashMap<>();

    public void updateVisibility(Player player, Player target, boolean hide) {
        if (attemptToProcess(player, target, hide)) {
            shouldHide.remove(target.getUniqueId());
        } else {
            shouldHide.put(target.getUniqueId(), hide);
        }
    }

    public void attemptToProcessUpdate(Player player) {
        if (!shouldHide.isEmpty()) {
            for (Map.Entry<UUID, Boolean> entry : shouldHide.entrySet()) {
                Player target = Bukkit.getPlayer(entry.getKey());
                boolean hide = entry.getValue();
                if (target == null || !target.isOnline() || !target.isValid() || attemptToProcess(player, target, hide)) {
                    shouldHide.remove(entry.getKey());
                }
            }
        }
    }

    private boolean attemptToProcess(Player player, Player target, boolean hide) {
        if (Recharge.Instance.use(player, "VIS " + target.getUniqueId(), 250, false, false)) {
            if (hide) {
                player.hidePlayer(target);
            } else {
                player.showPlayer(target);
            }
            return true;
        }
        return false;
    }
}