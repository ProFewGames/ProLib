package xyz.ufactions.prolib.redis.connect;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import xyz.ufactions.prolib.redis.CommandCallback;
import xyz.ufactions.prolib.redis.ServerCommand;

public class TransferHandler implements CommandCallback {

    private RedisTransferManager manager;

    public TransferHandler(RedisTransferManager manager) {
        this.manager = manager;
    }

    @Override
    public void run(ServerCommand command) {
        if (command instanceof TransferCommand) {
            TransferCommand cmd = (TransferCommand) command;
            Player player = Bukkit.getPlayer(cmd.getPlayer());
            if (player != null) {
                manager.handleTransfer(player, cmd.getDestination());
            }
        }
    }
}