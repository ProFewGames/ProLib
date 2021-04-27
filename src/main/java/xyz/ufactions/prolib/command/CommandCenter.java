package xyz.ufactions.prolib.command;

import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.Plugin;
import xyz.ufactions.prolib.ProLib;
import xyz.ufactions.prolib.api.MegaPlugin;
import xyz.ufactions.prolib.command.internal.AlertCommand;
import xyz.ufactions.prolib.command.internal.ItemBuilderCommand;
import xyz.ufactions.prolib.command.internal.LobbyCommand;
import xyz.ufactions.prolib.command.internal.ModulesCommand;
import xyz.ufactions.prolib.libs.F;
import xyz.ufactions.prolib.redis.Utility;
import xyz.ufactions.prolib.reflection.ReflectionUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class CommandCenter {

    private final MegaPlugin plugin;

    public static CommandCenter instance;
    private final SimpleCommandMap commandMap;
    protected HashMap<String, ICommand> Commands;

    public static void initialize(MegaPlugin plugin) {
        if (instance == null) {
            instance = new CommandCenter(plugin);
        }
    }

    private CommandCenter(MegaPlugin plugin) {
        this.Commands = new HashMap<>();
        this.plugin = plugin;
        ReflectionUtils.RefClass ClassCraftServer = ReflectionUtils.getRefClass("{cb}.CraftServer");
        ReflectionUtils.RefMethod MethodGetCommandMap = ClassCraftServer.getMethod("getCommandMap");
        this.commandMap = (SimpleCommandMap) MethodGetCommandMap.of(Bukkit.getServer()).call();

        registerDefaultCommands();
    }

    protected void registerDefaultCommands() {
        commandMap.register("megabukkit", new ModulesCommand("modules"));

        if (Utility.allowRedis()) {
            commandMap.register("megabukkit", new LobbyCommand());
            commandMap.register("megabukkit", new AlertCommand());
        }

        addCommand(plugin, new ItemBuilderCommand(plugin.getDummy()));
    }

    public final HashMap<String, ICommand> getCommands() {
        return Commands;
    }

    public void addCommand(MegaPlugin plugin, ICommand command) {
        registerCommand(plugin, command);
        for (String commandRoot : command.aliases()) {
            this.Commands.put(commandRoot.toLowerCase(), command);
            command.setCommandCenter(this);
        }
        ProLib.debug("(COMMAND) " + F.concatenate(", ", command.aliases().toArray(new String[0])) + " registered.");
    }

    public void removeCommand(ICommand command) {
        unregisterCommand(command);
        for (String commandRoot : command.aliases()) {
            this.Commands.remove(commandRoot.toLowerCase());
            command.setCommandCenter(null);
        }
    }

    private void registerCommand(MegaPlugin plugin, ICommand command) {
        try {
            PluginCommand cmd = parse(plugin, command);
            if (cmd != null) {
//                 Register command
                commandMap.register(plugin.getDescription().getName(), cmd);
                cmd.setExecutor(command);
                cmd.setTabCompleter(command);
            }
        } catch (Exception e) {
            plugin.log("Failed to register command aliases " + command.aliases());
        }
    }

    private void unregisterCommand(ICommand command) {
        // TODO
    }

    private PluginCommand parse(MegaPlugin plugin, ICommand command) throws IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException {
        String name = null;
        List<String> aliases = new ArrayList<>();

        Iterator<String> iterator = command.aliases().iterator();

        while (iterator.hasNext()) {
            if (name == null) {
                name = iterator.next();
            } else {
                aliases.add(iterator.next());
            }
        }

        if (name == null) {
            Bukkit.getServer().getLogger().severe("Could not load command " + command + " empty name set");
            return null;
        }

        Constructor<PluginCommand> constructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
        constructor.setAccessible(true);
        PluginCommand cmd = constructor.newInstance(name, plugin);

        cmd.setDescription(command.description());
        cmd.setAliases(aliases);
        return cmd;
    }
}