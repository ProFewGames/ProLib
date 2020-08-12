package xyz.ufactions.prolib.redis.chat;

import xyz.ufactions.prolib.redis.CommandCallback;
import xyz.ufactions.prolib.redis.ServerCommand;

public class RedisChatHandler implements CommandCallback {

    private final RedisChatManager manager;

    public RedisChatHandler(RedisChatManager manager) {
        this.manager = manager;
    }

    @Override
    public void run(ServerCommand command) {
        if (command instanceof RedisAlertCommand) {
            manager.handleCallback((RedisAlertCommand) command);
        }
    }
}