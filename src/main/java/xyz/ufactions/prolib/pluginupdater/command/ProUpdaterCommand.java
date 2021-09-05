package xyz.ufactions.prolib.pluginupdater.command;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import xyz.ufactions.prolib.api.IModule;
import xyz.ufactions.prolib.command.api.Command;
import xyz.ufactions.prolib.command.api.CommandBase;
import xyz.ufactions.prolib.pluginupdater.ProUpdater;


public class ProUpdaterCommand extends CommandBase<IModule> {

    private final ProUpdater updater;

    public ProUpdaterCommand(IModule plugin, ProUpdater updater) {
        super(plugin, plugin.getPlugin().getName() + ":updater");

        this.updater = updater;

        setPermission("prolib.command.updater");
    }

    @Command(aliases = {"check"}, description = "Checks if there is an update available")
    public void checkCommand(CommandSender sender, String label, String[] args) {
        message(sender, "Checking for updates...");
        Bukkit.getScheduler().runTaskAsynchronously(plugin.getPlugin(), () -> {
            boolean hasUpdate = updater.checkUpdate(false);
            if (hasUpdate) {
                message(sender, "There is an update available.");
            } else {
                message(sender, "There is no update available.");
            }
        });
    }
}