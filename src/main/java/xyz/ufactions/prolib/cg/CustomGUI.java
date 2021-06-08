package xyz.ufactions.prolib.cg;

import org.bukkit.entity.Player;
import xyz.ufactions.prolib.api.Module;
import xyz.ufactions.prolib.cg.file.GUIFileHandler;
import xyz.ufactions.prolib.gui.GUI;
import xyz.ufactions.prolib.libs.F;
import xyz.ufactions.prolib.script.Script;
import xyz.ufactions.prolib.script.ScriptManager;

import java.util.Map;

public class CustomGUI extends Module {

    private Map<String, GUI<?>> guis;
    private ScriptManager scriptManager;

    @Override
    public void enable() {
        this.scriptManager = ScriptManager.getInstance();
        reload();
    }

    public void reload() {
        GUIFileHandler guiFileHandler = new GUIFileHandler(this);
        if (guis != null && !guis.isEmpty()) {
            for (String identifier : guis.keySet()) {
                unregisterGUI(identifier);
            }
        }
        this.guis = guiFileHandler.getGUIs(this.guis == null);
        for (Map.Entry<String, GUI<?>> entry : guis.entrySet()) {
            scriptManager.registerScript("open gui=\"" + entry.getKey() + "\"", (player, script) -> {
                entry.getValue().openInventory(player);
                return "";
            });
        }
    }

    public boolean registerGUI(String identifier, GUI<?> gui) {
        if (guiExists(identifier)) return false;
        guis.put(identifier, gui);
        return true;
    }

    public boolean unregisterGUI(String identifier) {
        if (!guiExists(identifier)) return false;
        identifier = F.matchCase(guis.keySet(), identifier);
        guis.remove(identifier);
        scriptManager.unregisterScript("open gui=\"" + identifier + "\"");
        return true;
    }

    public GUI<?> getGUI(String identifier) {
        return guis.get(F.matchCase(guis.keySet(), identifier));
    }

    public boolean guiExists(String identifier) {
        return guis.containsKey(F.matchCase(guis.keySet(), identifier));
    }
}