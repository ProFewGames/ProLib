package xyz.ufactions.prolib.networking.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.ufactions.prolib.command.CommandBase;
import xyz.ufactions.prolib.file.ProLibConfig;
import xyz.ufactions.prolib.libs.F;
import xyz.ufactions.prolib.libs.UtilPlayer;
import xyz.ufactions.prolib.networking.NetworkModule;

import java.util.Collections;
import java.util.List;

public class HubCommand extends CommandBase<NetworkModule> {

    public HubCommand(NetworkModule plugin) {
        super(plugin, "hub", "lobby");
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        if(isPlayer(sender)) {
            Player player = (Player) sender;
            if(plugin.isTransferring(player.getName())) {
                UtilPlayer.message(player, F.error(plugin.getName(), "You are already transferring!"));
                return;
            }
            plugin.transfer(player.getName(), ProLibConfig.getInstance().getFallbackServer());
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        return Collections.emptyList();
    }
}