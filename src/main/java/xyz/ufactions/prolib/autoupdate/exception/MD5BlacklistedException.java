package xyz.ufactions.prolib.autoupdate.exception;

import xyz.ufactions.prolib.api.exception.MegaException;

public class MD5BlacklistedException extends MegaException {

    public MD5BlacklistedException(String message) {
        super(message);
    }
}