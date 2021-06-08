package xyz.ufactions.prolib.block.exception;

public final class WorldsMismatchException extends Exception {

    public WorldsMismatchException() {
    }

    public WorldsMismatchException(String message) {
        super(message);
    }

    public WorldsMismatchException(String message, Throwable cause) {
        super(message, cause);
    }

    public WorldsMismatchException(Throwable cause) {
        super(cause);
    }

    public WorldsMismatchException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}