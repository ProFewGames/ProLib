package xyz.ufactions.prolib.networking.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import xyz.ufactions.prolib.command.CommandBase;
import xyz.ufactions.prolib.libs.F;
import xyz.ufactions.prolib.libs.UtilPlayer;
import xyz.ufactions.prolib.networking.NetworkModule;
import xyz.ufactions.prolib.redis.player.PlayerStatus;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SendCommand extends CommandBase<NetworkModule> {

    public SendCommand(NetworkModule plugin) {
        super(plugin, "send");
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        if (args.length == 2) {
            if (Plugin.isTransferring(args[0])) {
                sender.sendMessage(F.error(Plugin.getName(), "This player is already transferring!"));
                return;
            }
            UtilPlayer.searchNetwork(list -> {
                if (list.size() == 1) {
                    PlayerStatus status = list.get(0);
                    String name = args[1];
                    if (Plugin.getServer(name) != null) {
                        name = Plugin.getServer(name).getName();
                    }
                    sender.sendMessage(F.main(Plugin.getName(), "Sending transfer request for " + F.elem(status.getName()) + " to " + F.elem(name) + "..."));
                    Plugin.transfer(status.getName(), name);
                }
            }, sender, args[0], true);
            return;
        }
        sender.sendMessage(F.help("/" + AliasUsed + " <player> <server>", "Send a player to the designated server."));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 1) {
            return getNetworkMatches(args[0]);
        }
        if (args.length == 2) {
            return getMatches(args[1], Arrays.asList(Plugin.getServerNames().toArray(new String[0])));
        }
        return Collections.emptyList();
    }
}