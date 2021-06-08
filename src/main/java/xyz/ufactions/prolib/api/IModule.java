package xyz.ufactions.prolib.api;

import org.bukkit.Server;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import xyz.ufactions.prolib.command.ICommand;

import java.io.File;

public interface IModule {

    BukkitScheduler getScheduler();

    void runSyncLater(Runnable runnable, long timer);

    void log(String message);

    void warning(String message);

    void debug(String message);

    Plugin getPlugin();

    Server getServer();

    File getDataFolder();

    void addCommand(ICommand command);

    void removeCommand(ICommand command);

    void registerEvents(Listener listener);

    void unregisterEvents(Listener listener);
}