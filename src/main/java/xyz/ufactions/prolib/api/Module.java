package xyz.ufactions.prolib.api;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitScheduler;
import xyz.ufactions.prolib.api.exception.ModuleEnabledException;
import xyz.ufactions.prolib.command.CommandCenter;
import xyz.ufactions.prolib.command.ICommand;
import xyz.ufactions.prolib.libs.C;
import xyz.ufactions.prolib.libs.F;
import xyz.ufactions.prolib.libs.UtilTime;

import java.io.File;
import java.util.HashMap;

public abstract class Module implements Listener {

    protected String ModuleName;
    protected MegaPlugin Plugin;
    protected HashMap<String, ICommand> Commands;
    private boolean enabled = false;

    public PluginManager getPluginManager() {
        return this.Plugin.getServer().getPluginManager();
    }

    public BukkitScheduler getScheduler() {
        return this.Plugin.getServer().getScheduler();
    }

    public Server getServer() {
        return this.Plugin.getServer();
    }

    public MegaPlugin getPlugin() {
        return this.Plugin;
    }

    public File getDataFolder() {
        File file = new File(this.Plugin.getDataFolder(), "/" + this.ModuleName);
        if (!file.exists()) file.mkdirs();
        return file;
    }

    public void registerEvents(Listener listener) {
        this.Plugin.getServer().getPluginManager().registerEvents(listener, this.Plugin);
    }

    public void registerSelf() {
        registerEvents(this);
    }

    public void deregisterSelf() {
        HandlerList.unregisterAll(this);
    }

    public final void onEnable(MegaPlugin plugin, String name) throws ModuleEnabledException {
        if (enabled) throw new ModuleEnabledException(this);
        long epoch = System.currentTimeMillis();
        this.ModuleName = name;
        log("Initializing...");
        Plugin = plugin;
        Commands = new HashMap<>();
        enable();
        this.enabled = true;
        log("Enabled in " + UtilTime.convertString(System.currentTimeMillis() - epoch, 1, UtilTime.TimeUnit.FIT) + ".");
    }

    public final void onDisable() {
        deregisterSelf();
        unregisterCommands();
        disable();
        this.enabled = false;
        log("Disabled.");
    }

    public void enable() {
    }

    public void disable() {
    }

    public final String getName() {
        return this.ModuleName;
    }

    public final void addCommand(ICommand command) {
        CommandCenter.instance.addCommand(Plugin, command);
        for (String root : command.aliases()) {
            Commands.put(root, command);
        }
    }

    private void unregisterCommands() {
        for (ICommand command : Commands.values()) {
            removeCommand(command);
        }
        Commands.clear();
    }

    public final void removeCommand(ICommand command) {
        CommandCenter.instance.removeCommand(command);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void warning(String message) {
        log(C.mError + message);
    }

    public void log(String message) {
        Bukkit.getConsoleSender().sendMessage(F.main(this.ModuleName, message));
    }

    public void runAsync(Runnable runnable) {
        this.Plugin.getServer().getScheduler().runTaskAsynchronously(this.Plugin, runnable);
    }

    public void runSync(Runnable runnable) {
        this.Plugin.getServer().getScheduler().runTask(this.Plugin, runnable);
    }

    public void runSyncLater(Runnable runnable, long delay) {
        this.Plugin.getServer().getScheduler().runTaskLater(this.Plugin, runnable, delay);
    }
}