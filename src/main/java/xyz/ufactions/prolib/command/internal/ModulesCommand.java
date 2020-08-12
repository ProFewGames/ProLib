package xyz.ufactions.prolib.command.internal;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import xyz.ufactions.prolib.api.MegaPlugin;
import xyz.ufactions.prolib.api.Module;
import xyz.ufactions.prolib.libs.F;
import xyz.ufactions.prolib.libs.UtilServer;

public class ModulesCommand extends Command {

    public ModulesCommand(String name) {
        super(name);
        this.description = "Get a list of all modules";
        this.usageMessage = "/modules";
        this.setPermission("mega.command.modules");
    }

    @Override
    public boolean execute(CommandSender sender, String alias, String[] args) {
        if (!testPermission(sender)) return true;

        MegaPlugin[] plugins = UtilServer.getMegaPlugins();
        if (plugins.length == 0) {
            sender.sendMessage(ChatColor.RED + "No available MegaPlugins.");
        } else {
            sender.sendMessage(F.line());
            for (MegaPlugin plugin : plugins) {
                sender.sendMessage("Parent: " + (plugin.isEnabled() ? ChatColor.GREEN : ChatColor.RED) + plugin.getDescription().getName());
                StringBuilder builder = new StringBuilder();
                for (Module module : plugin.getModules()) {
                    if (builder.length() > 0) {
                        builder.append(ChatColor.WHITE);
                        builder.append(", ");
                    }
                    builder.append(module.isEnabled() ? ChatColor.GREEN : ChatColor.RED);
                    builder.append(module.getName());
                }
                sender.sendMessage("Modules: " + (builder.length() == 0 ? ChatColor.RED + "No Linked Modules" : builder.toString()));
            }
            sender.sendMessage(F.line());
        }
        return true;
    }
}