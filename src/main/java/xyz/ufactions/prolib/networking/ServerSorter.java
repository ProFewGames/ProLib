package xyz.ufactions.prolib.networking;

import xyz.ufactions.prolib.redis.data.MinecraftServer;

import java.util.Comparator;

public class ServerSorter implements Comparator<MinecraftServer> {

    @Override
    public int compare(MinecraftServer first, MinecraftServer second) {
        if (second.getPlayerCount() == 999)
            return -1;

        if (first.getPlayerCount() == 999)
            return 1;

        if (first.getPlayerCount() < (first.getMaxPlayerCount() / 2)
                && second.getPlayerCount() >= (second.getMaxPlayerCount() / 2))
            return -1;

        if (second.getPlayerCount() < (second.getMaxPlayerCount() / 2)
                && first.getPlayerCount() >= (first.getMaxPlayerCount() / 2))
            return 1;

        if (first.getPlayerCount() < (first.getMaxPlayerCount() / 2)) {
            if (first.getPlayerCount() > second.getPlayerCount())
                return -1;

            if (second.getPlayerCount() > first.getPlayerCount())
                return 1;
        } else {
            if (first.getPlayerCount() < second.getPlayerCount())
                return -1;

            if (second.getPlayerCount() < first.getPlayerCount())
                return 1;
        }

        return 0;
    }
}
