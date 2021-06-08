package xyz.ufactions.prolib.command.api;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import xyz.ufactions.prolib.api.IModule;
import xyz.ufactions.prolib.libs.F;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class CommandBase<PluginType extends IModule> implements CommandExecutor, TabExecutor {

    private final List<String> aliases;
    private final List<CommandSub<PluginType>> subCommands;
    protected final PluginType plugin;

    private String description = "A MegaPlugin provided command.";
    private String permission;

    public CommandBase(PluginType plugin, String... aliases) {
        this.plugin = plugin;
        this.aliases = Arrays.asList(aliases);
        this.subCommands = new ArrayList<>();
    }

    @Override
    public final boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
        if (!checkPermission(sender)) return true;
        if (args.length >= 1) {
            for (CommandSub<PluginType> command : subCommands) {
                for (String alias : command.getAliases()) {
                    if (alias.equalsIgnoreCase(args[0])) {
                        String[] newArgs = new String[args.length - 1];
                        System.arraycopy(args, 1, newArgs, 0, args.length - 1);
                        boolean executed = command.execute(sender, label, newArgs);
                        if (!command.execute(sender, label, newArgs))
                            displayHelp(sender, label);
                        return true;
                    }
                }
            }
        }
        if (!execute(sender, label, args)) displayHelp(sender, label);
        return true;
    }

    @Override
    public final List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
        if (args.length >= 1) {
            for (CommandSub<PluginType> command : subCommands) {
                for (String alias : command.getAliases()) {
                    if (alias.equalsIgnoreCase(args[0])) {
                        String[] newArgs = new String[args.length - 1];
                        System.arraycopy(args, 1, newArgs, 0, args.length - 1);
                        return command.tabComplete(sender, label, newArgs);
                    }
                }
            }
        }
        return tabComplete(sender, label, args);
    }

    // Protected Methods

    protected final void displayHelp(CommandSender sender, String label) {
        sender.sendMessage(F.line());
        sender.sendMessage("");
        sender.sendMessage(F.help("/" + label, getDescription()));
        for (CommandSub<PluginType> command : subCommands) {
            sender.sendMessage(command.help(label));
        }
        sender.sendMessage("");
        sender.sendMessage(F.line());
    }

    protected final void registerSubCommand(CommandSub<PluginType> command) {
        this.subCommands.add(command);
    }

    public final List<String> getAliases() {
        return aliases;
    }

    protected final void setDescription(String description) {
        this.description = description;
    }

    public final String getDescription() {
        return description;
    }

    protected final boolean isPlayer(CommandSender sender) {
        return isPlayer(sender, true);
    }

    protected final boolean isPlayer(CommandSender sender, boolean inform) {
        if (!(sender instanceof Player)) {
            if (inform) sender.sendMessage(F.noPlayer());
            return false;
        }
        return true;
    }

    protected final boolean checkPermission(CommandSender sender) {
        return checkPermission(sender, permission);
    }

    protected final boolean checkPermission(CommandSender sender, boolean inform) {
        return checkPermission(sender, permission, inform);
    }

    protected final boolean checkPermission(CommandSender sender, String permission) {
        return checkPermission(sender, permission, true);
    }

    protected final boolean checkPermission(CommandSender sender, String permission, boolean inform) {
        if (permission == null) return true;
        if (!sender.hasPermission(permission)) {
            if (inform)
                sender.sendMessage(F.noPermission());
            return false;
        }
        return true;
    }

    protected final void setPermission(String permission) {
        setPermission(permission, PermissionDefault.OP);
    }

    protected final void setPermission(String permission, PermissionDefault permissionDefault) {
        this.permission = permission;

        // TODO SET RIGHT DEFAULTS
        plugin.debug("Registered permission: " + permission);
    }

    // Abstract
    protected abstract boolean execute(CommandSender sender, String label, String[] args);

    protected List<String> tabComplete(CommandSender sender, String label, String[] args) {
        return Collections.emptyList();
    }
}