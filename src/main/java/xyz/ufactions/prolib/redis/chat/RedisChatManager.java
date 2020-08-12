package xyz.ufactions.prolib.redis.chat;

import org.bukkit.Bukkit;
import xyz.ufactions.prolib.libs.Callback;
import xyz.ufactions.prolib.redis.JedisManager;

/**
 * Responsible for network chat
 */
public class RedisChatManager {

    private static RedisChatManager instance;

    public static RedisChatManager getInstance() {
        if (instance == null) instance = new RedisChatManager();
        return instance;
    }

    private Callback<RedisAlertCommand> handler;

    private RedisChatManager() {
        System.out.println("<Server> RedisChatManager has connected via redis.");

        RedisChatHandler handler = new RedisChatHandler(this);
        JedisManager.getInstance().registerDataType("RedisAlertCommand", RedisAlertCommand.class, handler);

        this.handler = command -> {
            Bukkit.broadcastMessage("");
            Bukkit.broadcastMessage("Global Alert. Handler not set, Contents: " + command.getSender() + " : " + command.getMessage());
            Bukkit.broadcastMessage("");
        };
    }

    /**
     * Send an alert to every server connected to the repository
     *
     * @param sender  The sender of the message (leave blank for none)
     * @param message The message we're broadcasting
     */
    public void alert(String sender, String message) {
        RedisAlertCommand command = new RedisAlertCommand(sender, message);
        command.publish();
    }

    /**
     * For internal use only...
     * Will handle RedisAlertCommands
     *
     * @param command The command instance received
     */
    public void handleCallback(RedisAlertCommand command) {
        System.out.println("<Redis> Handling request from: " + command.getSender() + ": " + command.getMessage());
        handler.run(command);
    }

    /**
     * When the server receives an alert this callback will be ran.
     *
     * @param handler The method that will handle callbacks
     */
    public void setHandler(Callback<RedisAlertCommand> handler) {
        this.handler = handler;
        System.out.println("(RedisChat) Handler set.");
    }
}