package xyz.ufactions.prolib.redis.shutdown;

import xyz.ufactions.prolib.redis.ServerCommand;

import java.util.UUID;

public class ShutdownCallback extends ServerCommand {

    private final String server;

    private final int players;

    private final UUID uuid;

    public ShutdownCallback(ShutdownCommand command, String server, int players) {
        super();
        this.server = server;
        this.players = players;
        this.uuid = command.getUUID();
        setTargetServers(command.getSendingServer());
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public int getPlayers() {
        return this.players;
    }

    public String getServer() {
        return this.server;
    }
}
