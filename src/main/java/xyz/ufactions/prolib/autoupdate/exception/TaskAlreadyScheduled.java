package xyz.ufactions.prolib.autoupdate.exception;

import xyz.ufactions.prolib.api.exception.MegaException;

public class TaskAlreadyScheduled extends MegaException {

    public TaskAlreadyScheduled(String message) {
        super(message);
    }
}