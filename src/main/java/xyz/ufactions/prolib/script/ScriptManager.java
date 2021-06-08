package xyz.ufactions.prolib.script;

import org.bukkit.entity.Player;
import xyz.ufactions.prolib.ProLib;
import xyz.ufactions.prolib.api.MegaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ScriptManager {

    private static ScriptManager instance;

    public static ScriptManager getInstance() {
        return instance;
    }

    public static void initialize(MegaPlugin plugin) {
        if (instance == null) instance = new ScriptManager(plugin);
    }

    private final Map<String, Script> scripts;
    private final Pattern pattern;
    private final MegaPlugin plugin;

    private ScriptManager(MegaPlugin plugin) {
        this.plugin = plugin;
        this.scripts = new HashMap<>();
        this.pattern = Pattern.compile("(?<=<)(.*?)(?=>)");

        new InternalScripts(this).registerAll();
    }

    public String replace(String string) {
        return replace(null, string);
    }

    public String replace(Player player, String string) {
        Matcher matcher = pattern.matcher(string);
        while (matcher.find()) {
            String script = matcher.group();
            for (Map.Entry<String, Script> entry : scripts.entrySet()) {
                if (Pattern.compile(entry.getKey(), Pattern.CASE_INSENSITIVE).matcher(script).matches()) {
                    String replacer = entry.getValue().execute(player, script);
                    if (replacer != null)
                        string = string.replaceFirst("<" + script + ">", replacer);
                    break;
                }
            }
        }
        return string;
    }

    public boolean registerScript(String identifier, Script script) {
        if (isScriptRegistered(identifier)) {
            plugin.debug("SCRIPT", "Attempted to register '" + identifier + "' but failed because it is already registered.");
            return false;
        }
        scripts.put(identifier, script);
        plugin.debug("SCRIPT", "Registered '" + identifier + "'");
        return true;
    }

    public boolean unregisterScript(String identifier) {
        for (String s : scripts.keySet()) {
            if (s.equalsIgnoreCase(identifier)) {
                scripts.remove(s);
                plugin.debug("SCRIPT", "Unregistered '" + identifier + "'.");
                return true;
            }
        }
        return false;
    }

    public boolean isScriptRegistered(String identifier) {
        for (String s : scripts.keySet())
            if (s.equalsIgnoreCase(identifier))
                return true;
        return false;
    }

    protected MegaPlugin getPlugin() {
        return plugin;
    }
}