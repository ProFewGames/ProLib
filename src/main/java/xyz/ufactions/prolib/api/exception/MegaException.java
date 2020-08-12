package xyz.ufactions.prolib.api.exception;

public abstract class MegaException extends Exception {

    public MegaException() {
    }

    public MegaException(String message) {
        super(message);
    }

    public MegaException(String message, Throwable cause) {
        super(message, cause);
    }

    public MegaException(Throwable cause) {
        super(cause);
    }

    public MegaException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}