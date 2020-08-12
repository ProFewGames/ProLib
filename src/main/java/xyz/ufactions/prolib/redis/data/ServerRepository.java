package xyz.ufactions.prolib.redis.data;

import java.util.Collection;

public interface ServerRepository {

    Collection<MinecraftServer> getServerStatuses();

    MinecraftServer getServerStatus(String server);

    void updateServerStatus(MinecraftServer server, int timeout);

    void removeServerStatus(MinecraftServer server);

    boolean serverExists(String server);
}