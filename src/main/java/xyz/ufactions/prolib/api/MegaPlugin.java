package xyz.ufactions.prolib.api;

import org.bukkit.plugin.java.JavaPlugin;
import xyz.ufactions.prolib.ProLib;
import xyz.ufactions.prolib.api.exception.ModuleEnabledException;
import xyz.ufactions.prolib.api.exception.ModuleInitializationException;
import xyz.ufactions.prolib.libs.C;
import xyz.ufactions.prolib.libs.DummyModule;

import java.util.HashSet;

public abstract class MegaPlugin extends JavaPlugin {

    protected HashSet<Module> modules;

    private DummyModule dummy;

    @Override
    public final void onEnable() {
        this.modules = new HashSet<>();
        enable();
    }

    @Override
    public final void onDisable() {
        for (Module module : this.modules) {
            try {
                module.onDisable();
            } catch (Exception e) {
                System.err.println(module.getName() + " did not shutdown correctly. " + e.getMessage());
                if (ProLib.debugging())
                    e.printStackTrace();
            }
        }
        this.modules.clear();
        disable();
    }

    public abstract void enable();

    public void disable() {
    }

    public final void addModule(String name, Class<? extends Module> clazz) {
        if (getModule(clazz) != null) {
            try {
                throw new ModuleInitializationException("Module class '" + clazz.getSimpleName() + "' already initialized");
            } catch (ModuleInitializationException e) {
                e.printStackTrace();
                return;
            }
        }
        Module module;
        try {
            module = clazz.newInstance();
        } catch (IllegalAccessException | InstantiationException e) {
            try {
                throw new ModuleInitializationException(clazz, e);
            } catch (ModuleInitializationException moduleInitializationException) {
                moduleInitializationException.printStackTrace();
                return;
            }
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

    public final HashSet<Module> getModules() {
        return modules;
    }

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

    public final void warning(String message) {
        log(C.mError + message);
    }

    public final void log(String message) {
        getServer().getConsoleSender().sendMessage(C.mBody + message);
    }
}