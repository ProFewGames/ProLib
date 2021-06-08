package xyz.ufactions.prolib.networking.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.ufactions.prolib.command.CommandBase;
import xyz.ufactions.prolib.libs.C;
import xyz.ufactions.prolib.libs.F;
import xyz.ufactions.prolib.libs.UtilPlayer;
import xyz.ufactions.prolib.networking.NetworkModule;
import xyz.ufactions.prolib.networking.gui.ServersGUI;

import java.util.Arrays;
import java.util.List;

public class ServerCommand extends CommandBase<NetworkModule> {

    public ServerCommand(NetworkModule plugin) {
        super(plugin, "server", "transfer");
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("reload")) {
                if (sender.isOp()) {
                    sender.sendMessage(F.main(plugin.getName(), "Reloading Config..."));
                    plugin.getServerGUIFile().reload();
                    sender.sendMessage(F.main(plugin.getName(), "Config Reloaded!"));
                    return;
                }
            }
        }
        if (isPlayer(sender)) {
            Player player = (Player) sender;
            if (plugin.isTransferring(player.getName())) {
                UtilPlayer.message(player, F.error(plugin.getName(), "You are already transferring!"));
                return;
            }
            if (args.length == 1) {
                plugin.transfer(player.getName(), args[0]);
                return;
            }
            if (plugin.getServerGUIFile().contains("servers")) {
                if (plugin.getServerGUIFile().getConfig().isConfigurationSection("servers")) {
                    new ServersGUI(plugin).openInventory(player);
                    return;
                }
            }
            player.sendMessage(F.help("/" + AliasUsed + " <server>", "Transfer to a server"));
            player.sendMessage(C.mHead + "Available Servers: " + F.concatenate(", ", plugin.getServerNames().toArray(new String[0])));
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 1) {
            return getMatches(args[0], Arrays.asList(plugin.getServerNames().toArray(new String[0])));
        }
        return super.onTabComplete(sender, cmd, label, args);
    }
}