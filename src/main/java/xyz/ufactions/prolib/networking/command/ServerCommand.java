package xyz.ufactions.prolib.networking.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.ufactions.prolib.command.api.Command;
import xyz.ufactions.prolib.command.api.CommandBase;
import xyz.ufactions.prolib.networking.NetworkModule;
import xyz.ufactions.prolib.networking.gui.ServersGUI;

import java.util.Collections;
import java.util.List;

public class ServerCommand extends CommandBase<NetworkModule> {

    public ServerCommand(NetworkModule plugin) {
        super(plugin, "server", "transfer");

        setUsage("<server>");
        setDescription("Transfer to another server");
        requirePlayer();
    }

    @Override
    protected boolean execute(Player player, String label, String[] args) {
        if (args.length == 0) {
            if (plugin.getServerGUIFile().getConfig().isConfigurationSection("servers")) {
                new ServersGUI(plugin).openInventory(player);
                return true;
            }
        }
        if (args.length == 1) {
            if (plugin.isTransferring(player.getName())) {
                error(player, "You are already transferring!");
                return true;
            }
            plugin.transfer(player.getName(), args[0]);
            return true;
        }
        return false;
    }

    @Command(aliases = {"reload"}, permission = "prolib.command.server.reload", description = "Reload the server's GUI")
    public void reloadCommand(CommandSender sender, String label, String[] args) {
        message(sender, "Reloading Config...");
        plugin.getServerGUIFile().reload();
        message(sender, "Config Reloaded!");
    }

    @Override
    protected List<String> tabComplete(CommandSender sender, String label, String[] args) {
        if (args.length == 1) {
            return getServerMatches(args[0]);
        }
        return Collections.emptyList();
    }
}