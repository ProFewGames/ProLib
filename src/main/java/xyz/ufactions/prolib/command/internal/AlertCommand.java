package xyz.ufactions.prolib.command.internal;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import xyz.ufactions.prolib.libs.F;
import xyz.ufactions.prolib.redis.chat.RedisChatManager;

import java.util.Arrays;

public class AlertCommand extends Command {

    public AlertCommand() {
        super("alert");

        this.description = "Send an alert message via Jedis";
        this.usageMessage = "/alert <message>";
        this.setPermission("mega.command.alert");
        this.setAliases(Arrays.asList("announce", "announcement"));
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!testPermission(sender)) return true;

        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "/alert <message>");
        } else {
            RedisChatManager.getInstance().alert(sender.getName(), F.concatenate(" ", args));
        }
        return true;
    }
}