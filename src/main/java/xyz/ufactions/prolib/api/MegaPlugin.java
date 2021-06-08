package xyz.ufactions.prolib.api;

import org.bukkit.Warning;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import xyz.ufactions.prolib.api.exception.ModuleEnabledException;
import xyz.ufactions.prolib.api.exception.ModuleInitializationException;
import xyz.ufactions.prolib.command.CommandCenter;
import xyz.ufactions.prolib.command.ICommand;
import xyz.ufactions.prolib.file.ProLibConfig;
import xyz.ufactions.prolib.libs.C;
import xyz.ufactions.prolib.libs.DummyModule;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class MegaPlugin extends JavaPlugin implements IModule, Listener {

    private Set<Module> modules;
    private DummyModule dummy;
    private Map<String, ICommand> commands;

    @Override
    public final void onEnable() {
        this.modules = new HashSet<>();
        this.commands = new HashMap<>();
        enable();
    }

    @Override
    public final void onDisable() {
        for (Module module : this.modules) {
            try {
                module.onDisable();
            } catch (Exception e) {
                System.err.println(module.getName() + " did not shutdown correctly. " + e.getMessage());
                e.printStackTrace();
            }
        }
        this.modules.clear();
        disable();
    }

    public final void addModule(String name, Class<? extends Module> clazz) {
        if (getModule(clazz) != null) {
            new ModuleInitializationException("Module class '" + clazz.getSimpleName() + "' already initialized").printStackTrace();
            return;
        }
        Module module;
        try {
            module = clazz.newInstance();
        } catch (IllegalAccessException | InstantiationException e) {
            new ModuleInitializationException(clazz, e).printStackTrace();
            return;
        }
        this.modules.add(module);
        try {
            module.onEnable(this, name);
        } catch (ModuleEnabledException e) {
            e.printStackTrace();
        }
    }

    public final <T extends Module> T getModule(Class<T> clazz) {
        for (Module module : this.modules) {
            if (clazz.isInstance(module)) {
                return clazz.cast(module);
            }
        }
        return null;
    }

    public final Set<Module> getModules() {
        return modules;
    }

    @Override
    public final BukkitScheduler getScheduler() {
        return getServer().getScheduler();
    }

    @Override
    public final void runSyncLater(Runnable runnable, long timer) {
        getScheduler().runTaskLater(getPlugin(), runnable, timer);
    }

    @Warning(reason = "Will return this thus pointless")
    @Override
    public final Plugin getPlugin() {
        return this;
    }

    public final void log(String prefix, String message) {
        getServer().getConsoleSender().sendMessage(C.mHead + "[" + getName() + "] " + (prefix == null ? "" : "[" + prefix + "] ") + C.mBody + message);
    }

    @Override
    public final void log(String message) {
        log(null, message);
    }

    @Override
    public final void warning(String message) {
        warning(null, message);
    }

    public final void warning(String prefix, String message) {
        log(C.cDRed + "[WARNING] " + (prefix == null ? "" : C.mHead + "[" + prefix + "] ") + C.mError + message);
    }

    @Override
    public final void debug(String message) {
        debug(null, message);
    }

    public final void debug(String prefix, String message) {
        if (ProLibConfig.getInstance().isDebuggingEnabled())
            log(C.mHead + "[DEBUG] " + (prefix == null ? "" : "[" + prefix + "] ") + C.mBody + message);
    }

    @Override
    public final void addCommand(ICommand command) {
        CommandCenter.instance.addCommand(this, command);
        for (String root : command.aliases()) {
            commands.put(root, command);
        }
    }

    @Override
    public final void removeCommand(ICommand command) {
        CommandCenter.instance.removeCommand(command);
    }

    @Override
    public final void registerEvents(Listener listener) {
        getServer().getPluginManager().registerEvents(listener, this);
    }

    @Override
    public final void unregisterEvents(Listener listener) {
        HandlerList.unregisterAll(listener);
    }

    public abstract void enable();

    public void disable() {
    }

    @Deprecated
    public final DummyModule getDummy() {
        if (dummy == null) {
            dummy = new DummyModule();
            try {
                dummy.onEnable(this, "Dummy");
            } catch (ModuleEnabledException e) {
                e.printStackTrace();
            }
        }
        return dummy;
    }
}