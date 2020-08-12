package xyz.ufactions.prolib.command.internal;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.ufactions.prolib.file.ProLibConfig;
import xyz.ufactions.prolib.libs.F;
import xyz.ufactions.prolib.redis.connect.RedisTransferManager;

import java.util.Collections;

public class LobbyCommand extends Command {

    public LobbyCommand() {
        super("lobby");

        this.description = "Get sent to the fallback server.";
        this.usageMessage = "/lobby";
        this.setPermission("mega.command.lobby");
        this.setAliases(Collections.singletonList("hub"));
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!testPermission(sender)) return true;

        if (!(sender instanceof Player)) {
            sender.sendMessage(F.noPlayer());
            return true;
        }
        RedisTransferManager.getInstance().transfer(sender.getName(), ProLibConfig.getInstance().fallbackServer());
        return true;
    }
}