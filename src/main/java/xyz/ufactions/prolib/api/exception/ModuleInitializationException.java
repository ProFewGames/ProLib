package xyz.ufactions.prolib.api.exception;

import xyz.ufactions.prolib.api.Module;

public class ModuleInitializationException extends Exception {

    public ModuleInitializationException(Class<? extends Module> clazz, Throwable cause) {
        super("Module '" + clazz.getName() + " (" + clazz.getSimpleName() + ")" + "' could not be initialized; Ensure that you're not passing any parameters through the main constructor.", cause);
    }

    public ModuleInitializationException(String message) {
        super(message);
    }
}