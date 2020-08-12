package xyz.ufactions.prolib.redis;

public class ConnectionData {

    private final String host;

    private final int port;

    private final String password;

    public String getHost() {
        return this.host;
    }

    public int getPort() {
        return this.port;
    }

    public String getPassword() {
        return this.password;
    }

    public ConnectionData(String host, int port, String password) {
        this.host = host;
        this.port = port;
        this.password = password;
    }
}
