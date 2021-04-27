package xyz.ufactions.prolib.script;

import org.bukkit.entity.Player;

public interface Script {

    default String execute(String script) {
        return execute(null, script);
    }

    String execute(Player player, String script);
}