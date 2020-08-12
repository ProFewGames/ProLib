package xyz.ufactions.prolib.redis.connect;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.ufactions.prolib.libs.F;
import xyz.ufactions.prolib.redis.JedisManager;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Listens on the network for transfer requests and tries to provide if the request is valid
 * Also is able to send requests.
 */
public class RedisTransferManager {

    private static RedisTransferManager instance;

    public static RedisTransferManager getInstance() {
        return instance;
    }

    public static void initialize(JavaPlugin plugin) {
        if (instance == null || !getInstance().plugin.isEnabled())
            instance = new RedisTransferManager(plugin);
    }

    private final JavaPlugin plugin;

    private RedisTransferManager(JavaPlugin plugin) {
        System.out.println("<Server> RedisTransferManager has connected via redis.");
        this.plugin = plugin;

        TransferHandler handler = new TransferHandler(this);
        JedisManager.getInstance().registerDataType("TransferCommand", TransferCommand.class, handler);

        Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord");
    }

    public void transfer(String playerName, String destination) {
        Player player = Bukkit.getPlayer(playerName);
        if (player != null) {
            handleTransfer(player, destination);
        } else {
            TransferCommand transferCommand = new TransferCommand(playerName, destination);
            transferCommand.publish();
        }
    }

    public void handleTransfer(Player player, String destination) {
        player.sendMessage(F.main("System", "Transferring you to " + F.elem(F.capitalizeFirstLetter(destination)) + "."));
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try {
            out.writeUTF("Connect");
            out.writeUTF(destination);
        } catch (IOException e) {
            // Can never happen
        }
        player.sendPluginMessage(this.plugin, "BungeeCord", b.toByteArray());
    }
}
