package xyz.ufactions.prolib.command.api;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import xyz.ufactions.prolib.ProLib;
import xyz.ufactions.prolib.api.IModule;
import xyz.ufactions.prolib.libs.F;
import xyz.ufactions.prolib.libs.UtilServer;
import xyz.ufactions.prolib.networking.NetworkModule;
import xyz.ufactions.prolib.recharge.Recharge;
import xyz.ufactions.prolib.redis.Utility;
import xyz.ufactions.prolib.redis.player.PlayerStatus;
import xyz.ufactions.prolib.redis.player.RedisPlayerManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

// TODO : Concatenate new aliases for sub command / Register new label
public class CommandBase<PluginType extends IModule> implements CommandExecutor, TabExecutor {

    protected final PluginType plugin;

    private final Map<Command, Method> commands;
    private final String[] aliases;

    private String permission = "";
    private String usage = "";
    private String description = "";
    private boolean requirePlayer = false;

    public CommandBase(PluginType plugin, String... aliases) {
        this.plugin = plugin;
        this.aliases = aliases;
        this.commands = new HashMap<>();

        scanForCommands();
    }

    @Override
    public final boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
        if (!checkPermission(sender, permission)) return true;
        if (sender instanceof Player)
            if (!Recharge.Instance.use(((Player) sender), "Command", 250, true, false)) return true;
        for (Map.Entry<Command, Method> entry : commands.entrySet()) {
            Command command = entry.getKey();
            if (args.length >= command.arguments()) {
                for (String alias : command.aliases()) {
                    if (args[0].equalsIgnoreCase(alias)) {
                        try {
                            if (!checkPermission(sender, command.permission())) return true;
                            Method method = entry.getValue();
                            if (method.getParameters()[0].getType().equals(Player.class))
                                if (!isPlayer(sender)) return true;
                            String[] newArgs = new String[args.length - 1];
                            System.arraycopy(args, 1, newArgs, 0, args.length - 1);
                            Object result = method.invoke(this, sender, label, newArgs);
                            if (result instanceof Boolean) {
                                if (!(Boolean) result) {
                                    sender.sendMessage(F.help("/" + label + " " + alias + " " + command.usage(), command.description()));
                                }
                            }
                            return true;
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            sender.sendMessage(F.error("Command", "Failed to execute command."));
                            e.printStackTrace();
                            return true;
                        } catch (Exception e) {
                            sender.sendMessage(F.error("Command", "An expected error has occurred."));
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        if (requirePlayer) {
            if (!isPlayer(sender)) return true;
            if (execute(((Player) sender), label, args)) return true;
        } else {
            if (execute(sender, label, args)) return true;
        }
        sender.sendMessage(F.line());
        sender.sendMessage(" ");
        if (!description.isEmpty() || !usage.isEmpty())
            sender.sendMessage(F.help("/" + label + (usage.isEmpty() ? "" : " " + usage), description));
        for (Command command : commands.keySet()) {
            sender.sendMessage(F.help(
                    "/" + label + " " +
                            F.concatenate(" ", command.aliases()) + "" +
                            (command.usage().isEmpty() ? "" : " " + command.usage())
                    , command.description()));
        }
        sender.sendMessage(" ");
        sender.sendMessage(F.line());
        return true;
    }

    @Override
    public final List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
        if (args.length == 1) {
            List<String> aliases = new ArrayList<>();
            for (Command command : commands.keySet()) {
                aliases.addAll(Arrays.asList(command.aliases()));
            }
            return getMatches(args[0], aliases);
        }
        return tabComplete(sender, label, args);
    }

    // Generics

    private void scanForCommands() {
        for (Method method : getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(Command.class)) {
                method.setAccessible(true);
                if (method.getParameterCount() != 3) {
                    plugin.warning("[COMMAND] Method[" + method.getName() + "] contains the incorrect amount of parameters. Ensure it contains: { CommandSender, String, String[] }");
                    continue;
                }
                if(!method.getParameters()[0].getType().equals(CommandSender.class)) {
                    if(!method.getParameters()[0].getType().equals(Player.class)) {
                        plugin.warning("[COMMAND] Parameter[0] in method[" + method.getName() + "] is of wrong type: CommandSender or Player");
                        continue;
                    }
                }
                if (!method.getParameters()[1].getType().equals(String.class)) {
                    plugin.warning("[COMMAND] Parameter[1] in method[" + method.getName() + "] is of wrong type: String");
                    continue;
                }
                if (!method.getParameters()[2].getType().equals(String[].class)) {
                    plugin.warning("[COMMAND] Parameter[2] in method[" + method.getName() + "] is of wrong type: String[]");
                    continue;
                }
                Command cmd = method.getAnnotation(Command.class);
                commands.put(cmd, method);
            }
        }
    }

    // Override

    protected boolean execute(CommandSender sender, String label, String[] args) {
        return false;
    }

    protected boolean execute(Player player, String label, String[] args) {
        return false;
    }

    protected List<String> tabComplete(CommandSender sender, String label, String[] args) {
        return null;
    }

    // Setters
    protected final void setDescription(String description) {
        this.description = description;
    }

    protected final void setUsage(String usage) {
        this.usage = usage;
    }

    protected final void setPermission(String permission) {
        this.permission = permission;
    }

    protected final void requirePlayer() {
        this.requirePlayer = true;
    }

    // Getters
    public final String getDescription() {
        return description;
    }

    public final String[] getAliases() {
        return aliases;
    }

    public final String getUsage() {
        return usage;
    }

    public final String getPermission() {
        return permission;
    }

    // Useful functions

    protected final List<String> getMatches(String start, List<String> possibleMatches) {
        return F.getMatches(possibleMatches.toArray(new String[0]), string -> string.toLowerCase().startsWith(start.toLowerCase()));
    }

    protected final List<String> getServerMatches(String start) {
        if (!Utility.allowRedis()) return Collections.emptyList();
        return getMatches(start, new ArrayList<>(((Objects.requireNonNull(ProLib.getPlugin(ProLib.class).getModule(NetworkModule.class)).getServerNames()))));
    }

    protected final List<String> getNetworkMatches(String start) {
        if (!Utility.allowRedis()) return getPlayerMatches(null, start);
        List<String> matches = new ArrayList<>();

        for (PlayerStatus player : RedisPlayerManager.getInstance().getPlayers())
            if (player.getName().toLowerCase().startsWith(start.toLowerCase()))
                matches.add(player.getName());

        return matches;
    }

    protected final List<String> getPlayerMatches(CommandSender sender, String start) {
        List<String> matches = new ArrayList<>();

        for (Player player : UtilServer.getPlayers()) {
            if (sender != null)
                if (sender instanceof Player)
                    if (!((Player) sender).canSee(player))
                        continue;
            if (player.getName().toLowerCase().startsWith(start.toLowerCase()))
                matches.add(player.getName());
        }

        return matches;
    }

    protected final void error(CommandSender sender, String... message) {
        for (String s : message) {
            sender.sendMessage(F.error(plugin.getName(), s));
        }
    }

    protected final void message(CommandSender sender, String... message) {
        for (String s : message) {
            sender.sendMessage(F.main(plugin.getName(), s));
        }
    }

    protected final void rawMessage(CommandSender sender, String... message) {
        for (String s : message) {
            sender.sendMessage(s);
        }
    }

    protected final void line(CommandSender sender) {
        line(sender, "");
    }

    protected final void line(CommandSender sender, String middle) {
        sender.sendMessage(F.line(middle));
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

    protected final boolean checkPermission(CommandSender sender, String permission) {
        return checkPermission(sender, permission, true);
    }

    protected final boolean checkPermission(CommandSender sender, String permission, boolean inform) {
        if (permission == null || permission.isEmpty()) return true;
        if (!sender.hasPermission(permission)) {
            if (inform)
                sender.sendMessage(F.noPermission());
            return false;
        }
        return true;
    }
}