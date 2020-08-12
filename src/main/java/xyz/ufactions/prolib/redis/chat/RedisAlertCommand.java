package xyz.ufactions.prolib.redis.chat;

import xyz.ufactions.prolib.redis.ServerCommand;

public class RedisAlertCommand extends ServerCommand {

    private final String sender;
    private final String message;
    private final String permission;

    public RedisAlertCommand(String sender, String message) {
        this(sender, message, "");
    }

    public RedisAlertCommand(String sender, String message, String permission) {
        super();

        this.permission = permission;
        this.sender = sender;
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }

    public String getPermission() {
        return permission;
    }
}