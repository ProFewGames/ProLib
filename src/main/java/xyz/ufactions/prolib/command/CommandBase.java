package xyz.ufactions.prolib.command;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.ufactions.prolib.ProLib;
import xyz.ufactions.prolib.api.Module;
import xyz.ufactions.prolib.libs.F;
import xyz.ufactions.prolib.libs.UtilServer;
import xyz.ufactions.prolib.recharge.Recharge;
import xyz.ufactions.prolib.redis.Utility;
import xyz.ufactions.prolib.redis.player.PlayerStatus;
import xyz.ufactions.prolib.redis.player.RedisPlayerManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public abstract class CommandBase<PluginType extends Module>
        implements ICommand {

    private final List<String> aliases;
    private final List<CommandSubBase<PluginType>> subCommands;

    protected String description = "A MegaPlugin provided command.";
    protected PluginType Plugin;
    protected String AliasUsed;
    protected String permission;
    protected CommandCenter CommandCenter;

    public CommandBase(PluginType plugin, String... aliases) {
        this.Plugin = plugin;
        this.subCommands = new ArrayList<>();
        this.aliases = Arrays.asList(aliases);
    }

    @Override
    public final boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        setAliasUsed(label);
        if (permission != null && !permission.isEmpty())
            if (!permissionCheck(sender, permission)) return true;
        if (args.length == 1) {
            for (CommandSubBase<PluginType> command : subCommands) {
                for (String alias : command.aliases()) {
                    if (alias.equalsIgnoreCase(args[0])) {
                        command.onCommand(sender, cmd, label, args);
                        return true;
                    }
                }
            }
        }
        execute(sender, args);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        return null;
    }

    protected List<String> getMatches(String start, Enum<?>[] numerators) {
        List<String> names = new ArrayList<>();
        for (Enum<?> e : numerators) {
            names.add(e.toString());
        }
        return getMatches(start, names);
    }

    protected List<String> getMatches(String start, List<String> possibleMatches) {
        return F.getMatches(possibleMatches.toArray(new String[0]), string -> string.toLowerCase().startsWith(start.toLowerCase()));
    }

    protected List<String> getOfflineMatches(String start) {
        List<String> matches = new ArrayList<>();

        for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
            if (player.getName().toLowerCase().startsWith(start.toLowerCase())) {
                matches.add(player.getName());
            }
        }

        return matches;
    }

    protected List<String> getNetworkMatches(String start) {
        if (!Utility.allowRedis()) return getPlayerMatches(null, start);
        List<String> matches = new ArrayList<>();

        for (PlayerStatus status : RedisPlayerManager.getInstance().getPlayers()) {
            if (status.getName().toLowerCase().startsWith(start.toLowerCase())) {
                matches.add(status.getName());
            }
        }
        return matches;
    }

    protected List<String> getPlayerMatches(CommandSender sender, String start) {
        List<String> matches = new ArrayList<>();

        for (Player player : UtilServer.getPlayers()) {
            if ((sender instanceof Player && ((Player) sender).canSee(player)) && player.getName().toLowerCase().startsWith(start.toLowerCase())) {
                matches.add(player.getName());
            }
        }

        return matches;
    }

    protected final boolean isPlayer(CommandSender sender) {
        return isPlayer(sender, true);
    }

    protected final boolean isPlayer(CommandSender sender, boolean notify) {
        if (!(sender instanceof Player)) {
            if (notify)
                sender.sendMessage(F.noPlayer());
            return false;
        }
        return true;
    }

    protected boolean permissionCheck(CommandSender sender, String permission) {
        return permissionCheck(sender, permission, true);
    }

    protected boolean permissionCheck(CommandSender sender, String permission, boolean inform) {
        if (!sender.hasPermission(permission)) {
            if (inform) {
                sender.sendMessage(F.noPermission());
            }
            return false;
        }
        return true;
    }

    protected abstract void execute(CommandSender sender, String[] args);

    public final void registerSubCommand(CommandSubBase<PluginType> commandSubBase) {
        this.subCommands.add(commandSubBase);
    }

    public Collection<String> aliases() {
        return this.aliases;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String description() {
        return description;
    }

    public void setPermission(String permission) {
        this.permission = permission;

        ProLib.debug("Registered permission: " + permission);
    }

    public void setAliasUsed(String aliasUsed) {
        this.AliasUsed = aliasUsed;
    }

    public void setCommandCenter(CommandCenter commandCenter) {
        this.CommandCenter = commandCenter;
    }

    protected void resetCommandCharge(Player caller) {
        Recharge.Instance.recharge(caller, "Command");
    }
}