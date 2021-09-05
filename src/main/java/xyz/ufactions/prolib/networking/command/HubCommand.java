package xyz.ufactions.prolib.networking.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.ufactions.prolib.command.api.CommandBase;
import xyz.ufactions.prolib.file.ProLibConfig;
import xyz.ufactions.prolib.libs.F;
import xyz.ufactions.prolib.libs.UtilPlayer;
import xyz.ufactions.prolib.networking.NetworkModule;

import java.util.Collections;
import java.util.List;

public class HubCommand extends CommandBase<NetworkModule> {

    public HubCommand(NetworkModule plugin) {
        super(plugin, "hub", "lobby");

        setDescription("Get transferred to the server's lobby.");
        requirePlayer();
    }

    @Override
    protected boolean execute(Player player, String label, String[] args) {
        if (plugin.isTransferring(player.getName())) {
            UtilPlayer.message(player, F.error(plugin.getName(), "You are already transferring"));
            return true;
        }
        plugin.transfer(player.getName(), ProLibConfig.getInstance().getFallbackServer());
        return true;
    }

    @Override
    protected List<String> tabComplete(CommandSender sender, String label, String[] args) {
        return Collections.emptyList();
    }
}