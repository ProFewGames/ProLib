package xyz.ufactions.prolib.api.exception;

import xyz.ufactions.prolib.api.Module;

public class ModuleEnabledException extends MegaException {

    public ModuleEnabledException(Module module) {
        super("Module '" + module.getName() + "' already enabled");
    }
}