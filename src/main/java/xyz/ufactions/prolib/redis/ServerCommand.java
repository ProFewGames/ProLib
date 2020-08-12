package xyz.ufactions.prolib.redis;

public abstract class ServerCommand {
    private String[] targetServers;

    public ServerCommand(String... targetServers) {
        this.targetServers = targetServers;
    }

    public void setTargetServers(String... targetServers) {
        this.targetServers = targetServers;
    }

    public String[] getTargetServers() {
        if (this.targetServers == null)
            this.targetServers = new String[0];
        return this.targetServers;
    }

    public void run() {
    }

    public boolean isTargetServer(String serverName) {
        if (getTargetServers().length == 0)
            return true;
        for (String targetServer : targetServers)
            if (targetServer.equalsIgnoreCase(serverName)) return true;
        return false;
    }

    public void publish() {
        JedisManager.getInstance().publishCommand(this);
    }
}
