package xyz.ufactions.prolib.script;

import org.bukkit.entity.Player;
import xyz.ufactions.prolib.ProLib;
import xyz.ufactions.prolib.api.Module;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScriptManager extends Module {

    private Map<String, Script> scripts;
    private Pattern pattern;

    @Override
    public void enable() {
        this.scripts = new HashMap<>();
        this.pattern = Pattern.compile("(?<=\\<)(.*?)(?=\\>)");

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
            ProLib.debug(getName(), "Attempted to register '" + identifier + "' but failed because it is already registered.");
            return false;
        }
        scripts.put(identifier, script);
        ProLib.debug(getName(), "Registered '" + identifier + "'");
        return true;
    }

    public boolean unregisterScript(String identifier) {
        for (String s : scripts.keySet()) {
            if (s.equalsIgnoreCase(identifier)) {
                scripts.remove(s);
                ProLib.debug(getName() + "Unregistered '" + identifier + "'");
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
}