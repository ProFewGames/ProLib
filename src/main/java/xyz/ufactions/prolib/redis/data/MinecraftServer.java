package xyz.ufactions.prolib.redis.data;

public class MinecraftServer {

    private String name;
    private final String motd;
    private int playerCount;
    private final int maxPlayerCount;
    private final int tps;
    private final int ram;
    private final int maxRam;
    private final String publicAddress;
    private final int port;
    private final long startupDate;

    private final long lastCheckup; // Last time the server pinged the network

    public String getName() {
        return name;
    }

    public String getMotd() {
        return motd;
    }

    public int getPlayerCount() {
        return playerCount;
    }

    public void increasePlayerCount(int amount) {
        this.playerCount += amount;
    }

    public int getMaxPlayerCount() {
        return maxPlayerCount;
    }

    public int getTPS() {
        return tps;
    }

    public int getRam() {
        return ram;
    }

    public int getMaxRam() {
        return maxRam;
    }

    public String getPublicAddress() {
        return publicAddress;
    }

    public int getPort() {
        return port;
    }

    public long getLastCheckup() {
        return lastCheckup;
    }

    public MinecraftServer(String name, String motd, int playerCount, int maxPlayerCount, int tps, int ram, int maxRam, String publicAddress, int port, long startupDate, long lastCheckup) {
        this.name = name;
        this.motd = motd;
        this.playerCount = playerCount;
        this.maxPlayerCount = maxPlayerCount;
        this.tps = tps;
        this.ram = ram;
        this.maxRam = maxRam;
        this.publicAddress = publicAddress;
        this.port = port;
        this.startupDate = startupDate;
        this.lastCheckup = lastCheckup;
    }

    @Override
    public String toString() {
        return "MinecraftServer{" +
                "name='" + name + '\'' +
                ", motd='" + motd + '\'' +
                ", playerCount=" + playerCount +
                ", maxPlayerCount=" + maxPlayerCount +
                ", tps=" + tps +
                ", ram=" + ram +
                ", maxRam=" + maxRam +
                ", publicAddress='" + publicAddress + '\'' +
                ", port=" + port +
                ", startupDate=" + startupDate +
                '}';
    }

    public boolean isEmpty() {
        return this.playerCount == 0;
    }

    public double getUptime() {
        return System.currentTimeMillis() / 1000.0D - this.startupDate;
    }

    public boolean isJoinable() {
        return playerCount < maxPlayerCount;
    }

    public void setName(String name) {
        this.name = name;
    }
}