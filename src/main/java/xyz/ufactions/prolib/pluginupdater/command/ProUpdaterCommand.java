package xyz.ufactions.prolib.pluginupdater.command;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import xyz.ufactions.prolib.api.IModule;
import xyz.ufactions.prolib.command.CommandBase;
import xyz.ufactions.prolib.libs.F;
import xyz.ufactions.prolib.pluginupdater.ProUpdater;

import java.util.List;

public class ProUpdaterCommand extends CommandBase<IModule> {

    private final ProUpdater updater;

    public ProUpdaterCommand(IModule plugin, ProUpdater updater) {
        super(plugin, plugin.getPlugin().getName() + ":updater");

        this.updater = updater;
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("check")) {
                sender.sendMessage(F.main("Plugin Updater", "Checking for updates..."));
                Bukkit.getScheduler().runTaskAsynchronously(plugin.getPlugin(), () -> {
                    boolean hasUpdate = updater.checkUpdate(false);
                    if (hasUpdate) {
                        sender.sendMessage(F.main("Plugin Updater", "There is an update available."));
                    } else {
                        sender.sendMessage(F.main("Plugin Updater", "There is no update available."));
                    }
                });
                return;
            }
        }
        sender.sendMessage(F.line("Plugin Updater"));
        sender.sendMessage(F.help("/" + AliasUsed + " check", "Checks if there is an update available."));
        sender.sendMessage(F.line());
    }
}