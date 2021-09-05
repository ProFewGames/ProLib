package xyz.ufactions.prolib.api;

import org.bukkit.Server;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import xyz.ufactions.prolib.command.ICommand;
import xyz.ufactions.prolib.command.api.CommandBase;

import java.io.File;

public interface IModule {

    String getName();

    BukkitScheduler getScheduler();

    void runSyncLater(Runnable runnable, long timer);

    void log(String message);

    void warning(String message);

    void debug(String message);

    Plugin getPlugin();

    Server getServer();

    File getDataFolder();

    void addCommand(CommandBase<?> command);

    @Deprecated
    void addCommand(ICommand command);

    @Deprecated
    void removeCommand(ICommand command);

    void registerEvents(Listener listener);

    void unregisterEvents(Listener listener);
}