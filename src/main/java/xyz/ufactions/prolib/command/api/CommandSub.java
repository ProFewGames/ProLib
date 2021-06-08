package xyz.ufactions.prolib.command.api;

import xyz.ufactions.prolib.api.IModule;
import xyz.ufactions.prolib.libs.F;

public abstract class CommandSub<PluginType extends IModule> extends CommandBase<PluginType> {

    public CommandSub(PluginType plugin, String... aliases) {
        super(plugin, aliases);
    }

    protected String help(String label) {
        return F.help("/" + label, getDescription());
    }
}