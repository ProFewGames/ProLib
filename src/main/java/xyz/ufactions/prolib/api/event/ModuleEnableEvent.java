package xyz.ufactions.prolib.api.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import xyz.ufactions.prolib.api.Module;

public class ModuleEnableEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final Module module;

    public ModuleEnableEvent(Module module) {
        this.module = module;
    }

    public Module getModule() {
        return module;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}