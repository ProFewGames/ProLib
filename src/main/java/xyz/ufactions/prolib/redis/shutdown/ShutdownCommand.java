package xyz.ufactions.prolib.redis.shutdown;

import xyz.ufactions.prolib.redis.ServerCommand;

import java.util.UUID;

public class ShutdownCommand extends ServerCommand {

    private final String sending;

    private final String target;

    private final UUID uuid = UUID.randomUUID();

    public ShutdownCommand(String sending, String target) {
        super();
        this.sending = sending;
        this.target = target;
    }

    public String getSendingServer() {
        return this.sending;
    }

    public String getTargetServer() {
        return this.target;
    }

    public UUID getUUID() {
        return this.uuid;
    }
}
