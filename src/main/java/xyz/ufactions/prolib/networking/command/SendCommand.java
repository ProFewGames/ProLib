package xyz.ufactions.prolib.networking.command;

import org.bukkit.command.CommandSender;
import xyz.ufactions.prolib.command.api.CommandBase;
import xyz.ufactions.prolib.libs.F;
import xyz.ufactions.prolib.libs.UtilPlayer;
import xyz.ufactions.prolib.networking.NetworkModule;
import xyz.ufactions.prolib.redis.player.PlayerStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SendCommand extends CommandBase<NetworkModule> {

    public SendCommand(NetworkModule plugin) {
        super(plugin, "send");

        setUsage("<player> <server>");
        setDescription("Send a player to another server");
        setPermission("prolib.command.send");
    }

    @Override
    protected boolean execute(CommandSender sender, String label, String[] args) {
        if (args.length == 2) {
            if (plugin.isTransferring(args[0])) {
                sender.sendMessage(F.error(plugin.getName(), "This player is already transferring!"));
                return true;
            }
            UtilPlayer.searchNetwork(list -> {
                if (list.size() == 1) {
                    PlayerStatus status = list.get(0);
                    String name = args[1];
                    if (plugin.getServer(name) != null) {
                        name = plugin.getServer(name).getName();
                    }
                    sender.sendMessage(F.main(plugin.getName(), "Sending transfer request for " + F.elem(status.getName()) + " to " + F.elem(name) + "..."));
                    plugin.transfer(status.getName(), name);
                }
            }, sender, args[0], true);
            return true;
        }
        return false;
    }

    @Override
    protected List<String> tabComplete(CommandSender sender, String label, String[] args) {
        if(args.length == 1) {
            return getNetworkMatches(args[0]);
        }
        if(args.length == 2) {
            return getServerMatches(args[1]);
        }
        return Collections.emptyList();
    }
}