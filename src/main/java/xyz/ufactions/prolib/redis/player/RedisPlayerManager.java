package xyz.ufactions.prolib.redis.player;

import xyz.ufactions.prolib.redis.data.DataRepository;
import xyz.ufactions.prolib.redis.repository.RedisDataRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Responsible for fetching real-time network data for type {@link org.bukkit.entity.Player}
 */
public class RedisPlayerManager {

    private static RedisPlayerManager instance;

    public static RedisPlayerManager getInstance() {
        if (instance == null) instance = new RedisPlayerManager();
        return instance;
    }

    private final DataRepository<PlayerStatus> repository;

    public RedisPlayerManager() {
        this.repository = new RedisDataRepository<>(PlayerStatus.class, "player");
    }

    public PlayerStatus getPlayer(UUID uuid) {
        return repository.getElement(uuid.toString());
    }

    public Collection<PlayerStatus> getPlayers() {
        return repository.getElements();
    }

    public List<PlayerStatus> getPlayers(String name) {
        List<PlayerStatus> players = new ArrayList<>();
        for (PlayerStatus status : getPlayers()) {
            if (status.getName().equalsIgnoreCase(name)) {
                players.add(status);
                break;
            }
            if (status.getName().toLowerCase().startsWith(name.toLowerCase())) {
                players.add(status);
            }
        }
        return players;
    }
}