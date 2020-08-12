package xyz.ufactions.prolib.updater.exception;

import xyz.ufactions.prolib.api.exception.MegaException;

public class UpdaterInitializationException extends MegaException {

    public UpdaterInitializationException() {
        super();
    }

    public UpdaterInitializationException(String message) {
        super(message);
    }

    public UpdaterInitializationException(String message, Throwable cause) {
        super(message, cause);
    }

    public UpdaterInitializationException(Throwable cause) {
        super(cause);
    }

    public UpdaterInitializationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}