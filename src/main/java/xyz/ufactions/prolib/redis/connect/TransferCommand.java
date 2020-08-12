package xyz.ufactions.prolib.redis.connect;

import xyz.ufactions.prolib.redis.ServerCommand;

public class TransferCommand extends ServerCommand {

    private final String player;
    private final String destination;

    public TransferCommand(String player, String destination) {
        super();

        this.player = player;
        this.destination = destination;
    }

    public String getPlayer() {
        return player;
    }

    public String getDestination() {
        return destination;
    }
}
