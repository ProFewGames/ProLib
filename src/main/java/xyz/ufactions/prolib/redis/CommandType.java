package xyz.ufactions.prolib.redis;

public class CommandType {
    private final Class<? extends ServerCommand> dataClazz;

    private final CommandCallback dataCallback;

    public Class<? extends ServerCommand> getDataType() {
        return this.dataClazz;
    }

    public CommandCallback getCallback() {
        return this.dataCallback;
    }

    public CommandType(Class<? extends ServerCommand> dataClazz, CommandCallback dataCallback) {
        this.dataClazz = dataClazz;
        this.dataCallback = dataCallback;
    }
}
