package xyz.ufactions.prolib.redis;

public interface CommandCallback {
    void run(ServerCommand command);
}
