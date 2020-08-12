package xyz.ufactions.prolib.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;
import xyz.ufactions.prolib.file.ProLibConfig;

import java.util.HashMap;
import java.util.Map;

// TODO add manager interface
public class JedisManager {

    private static JedisManager instance;

    public final String CHANNEL = "megabukkit.redis.:";

    private final Map<String, CommandType> commandTypes;

    private String localServerName;

    public void initializeServer(String serverName) {
        this.localServerName = serverName;
    }

    public boolean isServerInitialized() {
        return this.localServerName != null;
    }

    private final JedisPool pool;

    private JedisManager() {
        pool = Utility.getJedisPool();
        this.commandTypes = new HashMap<>();
        initializeServer(ProLibConfig.getInstance().serverName());
        initialize();
        System.out.println("<Jedis> JedisManager initialized");
    }

    private void initialize() {
        final Jedis jedis = pool.getResource();
        Thread thread = new Thread("Jedis Manager") {
            @Override
            public void run() {
                try {
                    jedis.psubscribe(new JedisDataListener(), CHANNEL + "*");
                } catch (JedisConnectionException e) {
                    e.printStackTrace();
                    pool.returnBrokenResource(jedis);
                } finally {
                    if (pool != null) pool.returnResource(jedis);
                }
            }
        };
        thread.start();
    }

    public void publishCommand(final ServerCommand serverCommand) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Jedis jedis = pool.getResource();
                try {
                    String commandType = serverCommand.getClass().getSimpleName();
                    String serializedData = Utility.serialize(serverCommand);
                    jedis.publish(CHANNEL + commandType, serializedData);
                } catch (JedisConnectionException e) {
                    e.printStackTrace();
                    pool.returnBrokenResource(jedis);
                    jedis = null;
                } finally {
                    if (jedis != null) pool.returnResource(jedis);
                }
            }
        }).start();
    }

    public void handleCommand(String commandType, String serializedData) {
        if (!isServerInitialized()) return;
        if (this.commandTypes.containsKey(commandType)) {
            Class<? extends ServerCommand> commandClazz = this.commandTypes.get(commandType).getDataType();
            ServerCommand serverCommand = Utility.deserialize(serializedData, commandClazz);
            if (serverCommand.isTargetServer(this.localServerName)) {
                CommandCallback callback = this.commandTypes.get(commandType).getCallback();
                serverCommand.run();
                if (callback != null) callback.run(serverCommand);
            }
        }
    }

    public void registerDataType(String dataName, Class<? extends ServerCommand> dataType, CommandCallback
            callback) {
        CommandType cmdType = new CommandType(dataType, callback);
        if (commandTypes.containsKey(dataName)) {
            System.out.println("<MegaBukkit> '" + dataName + "' is trying to re-register so we're just going to remove the previous instance");
            commandTypes.remove(dataName);
        }
        this.commandTypes.put(dataName, cmdType);
        System.out.println("Registered : " + dataName);
    }

    public void registerDataType(String dataName, Class<? extends ServerCommand> dataType) {
        registerDataType(dataName, dataType, null);
    }

    public static JedisManager getInstance() {
        if (instance == null && Utility.allowRedis()) {
            instance = new JedisManager();
        }
        return instance;
    }
}