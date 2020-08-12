package xyz.ufactions.prolib.redis.player;

import xyz.ufactions.prolib.redis.data.Data;

import java.util.UUID;

public class PlayerStatus implements Data {

    private final UUID uuid;
    private final String name;
    private final String server;

    public PlayerStatus(UUID uuid, String name, String server) {
        this.uuid = uuid;
        this.name = name;
        this.server = server;
    }

    public UUID getUUID() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public String getServer() {
        return server;
    }

    @Override
    public String getDataId() {
        return uuid.toString();
    }
}
