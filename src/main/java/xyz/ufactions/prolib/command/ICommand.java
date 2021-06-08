package xyz.ufactions.prolib.command;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;

import java.util.Collection;

@Deprecated
public interface ICommand extends CommandExecutor, TabCompleter {

    Collection<String> aliases();

    void setAliasUsed(String alias);

    String description();
}