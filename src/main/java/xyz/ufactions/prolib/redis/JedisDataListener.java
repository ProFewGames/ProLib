package xyz.ufactions.prolib.redis;

import redis.clients.jedis.JedisPubSub;

public class JedisDataListener extends JedisPubSub {

    @Override
    public void onPMessage(String pattern, String channel, String message) {
        try {
            String dataType = channel.split(":")[1];
            JedisManager.getInstance().handleCommand(dataType, message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}