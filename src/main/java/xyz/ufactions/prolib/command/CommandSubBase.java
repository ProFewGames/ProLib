package xyz.ufactions.prolib.command;

import xyz.ufactions.prolib.api.Module;

public abstract class CommandSubBase<PluginType extends Module> extends CommandBase<PluginType> {

    public CommandSubBase(PluginType plugin, String... aliases) {
        super(plugin, aliases);
    }

    protected abstract String help();
}