package xyz.ufactions.prolib.api;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitScheduler;
import xyz.ufactions.prolib.api.event.ModuleDisableEvent;
import xyz.ufactions.prolib.api.event.ModuleEnableEvent;
import xyz.ufactions.prolib.api.exception.ModuleEnabledException;
import xyz.ufactions.prolib.command.ICommand;
import xyz.ufactions.prolib.command.api.CommandBase;
import xyz.ufactions.prolib.libs.UtilTime;

import java.io.File;

public abstract class Module implements Listener, IModule {

    protected String ModuleName;
    protected MegaPlugin plugin;
    @Deprecated
    protected MegaPlugin Plugin;
    private boolean enabled = false;

    public PluginManager getPluginManager() {
        return this.Plugin.getServer().getPluginManager();
    }

    public final void onEnable(MegaPlugin plugin, String name) throws ModuleEnabledException {
        if (enabled) throw new ModuleEnabledException(this);
        long epoch = System.currentTimeMillis();
        this.ModuleName = name;
        this.Plugin = plugin;
        this.plugin = plugin;
        log("Initializing...");
        this.enabled = true;
        enable();
        Bukkit.getPluginManager().callEvent(new ModuleEnableEvent(this));
        log("Enabled in " + UtilTime.convertString(System.currentTimeMillis() - epoch, 1, UtilTime.TimeUnit.FIT) + ".");
    }

    public final void onDisable() {
        try {
            Bukkit.getPluginManager().callEvent(new ModuleDisableEvent(this));
        } catch (Exception e) {
            warning("Module disable event failed to call. Ensure you are not modifying anything pre-disable.");
            e.printStackTrace();
        }
        unregisterEvents(this);
        disable();
        this.enabled = false;
        log("Disabled.");
    }

    public void enable() {
    }

    public void disable() {
    }

    @Override
    public final String getName() {
        return this.ModuleName;
    }

    @Override
    public void addCommand(CommandBase<?> command) {
        getPlugin().addCommand(command);
    }

    public final void addCommand(ICommand command) {
        getPlugin().addCommand(command);
    }

    @Override
    public final void removeCommand(ICommand command) {
        getPlugin().removeCommand(command);
    }

    public final boolean isEnabled() {
        return enabled;
    }

    @Override
    public final void warning(String message) {
        getPlugin().warning(getName(), message);
    }

    @Override
    public final void log(String message) {
        getPlugin().log(getName(), message);
    }

    @Override
    public final void debug(String message) {
        getPlugin().debug(getName(), message);
    }

    @Override
    public BukkitScheduler getScheduler() {
        return getPlugin().getScheduler();
    }

    @Override
    public final void runSyncLater(Runnable runnable, long timer) {
        this.getPlugin().runSyncLater(runnable, timer);
    }

    @Override
    public final void registerEvents(Listener listener) {
        this.getPlugin().registerEvents(listener);
    }

    @Override
    public final void unregisterEvents(Listener listener) {
        this.getPlugin().unregisterEvents(listener);
    }

    @Override
    public final MegaPlugin getPlugin() {
        return Plugin;
    }

    @Override
    public final Server getServer() {
        return getPlugin().getServer();
    }

    @Override
    public final File getDataFolder() {
        File file = new File(this.Plugin.getDataFolder(), "/" + this.ModuleName);
        if (file.mkdirs()) {
            log("Created module directory");
        }
        return file;
    }

    public void runAsync(Runnable runnable) {
        this.Plugin.getServer().getScheduler().runTaskAsynchronously(this.Plugin, runnable);
    }

    public void runSync(Runnable runnable) {
        this.Plugin.getServer().getScheduler().runTask(this.Plugin, runnable);
    }
}