package xyz.ufactions.prolib.command;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;

import java.util.Collection;

public interface ICommand extends CommandExecutor, TabCompleter {

    void setCommandCenter(CommandCenter commandCenter);

    Collection<String> aliases();

    void setAliasUsed(String alias);

    String description();
}