package xyz.ufactions.prolib.autoupdate.exception;

import xyz.ufactions.prolib.api.exception.MegaException;

public class RemoteVersionVerificationException extends MegaException {

    public RemoteVersionVerificationException(String message) {
        super(message);
    }
}