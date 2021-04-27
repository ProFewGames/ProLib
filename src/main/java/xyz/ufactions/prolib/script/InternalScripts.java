package xyz.ufactions.prolib.script;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import xyz.ufactions.prolib.ProLib;
import xyz.ufactions.prolib.libs.C;

public class InternalScripts {

    private final ScriptManager scriptManager;

    protected InternalScripts(ScriptManager scriptManager) {
        this.scriptManager = scriptManager;
    }

    protected void registerAll() {
        ProLib.debug(scriptManager.getName(), "Attempting to register all scripts.");
        registerColors();
        registerPlayer();
        ProLib.debug(scriptManager.getName(), "All scripts registered.");
    }

    protected void registerColors() {
        for (ChatColor color : ChatColor.values()) {
            scriptManager.registerScript(color.name().replaceAll("_", " "), (player, script) -> String.valueOf(color));
        }
        scriptManager.registerScript("mHead", (player, script) -> C.mHead);
        scriptManager.registerScript("mBody", (player, script) -> C.mBody);
        scriptManager.registerScript("mError", (player, script) -> C.mError);
        scriptManager.registerScript("mElem", (player, script) -> C.mElem);
    }

    protected void registerPlayer() {
        scriptManager.registerScript("health", (player, script) -> String.valueOf(player.getHealth()));
        scriptManager.registerScript("name", (player, script) -> player.getName());
        scriptManager.registerScript("displayname", (player, script) -> player.getDisplayName());
        scriptManager.registerScript("gamemode", (player, script) -> player.getGameMode().name());
    }

    protected void registerServer() {
        Server server = Bukkit.getServer();
        scriptManager.registerScript("server_ip", (player, script) -> server.getIp());
        scriptManager.registerScript("server_name", (player, script) -> server.getName());
    }
}