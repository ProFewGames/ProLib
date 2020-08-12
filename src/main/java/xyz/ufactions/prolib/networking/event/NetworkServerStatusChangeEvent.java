package xyz.ufactions.prolib.networking.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import xyz.ufactions.prolib.networking.NetworkModule;
import xyz.ufactions.prolib.redis.data.MinecraftServer;

public class NetworkServerStatusChangeEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private final NetworkModule networkModule;
	private final MinecraftServer server;
	private final ServerStatus status;

	public NetworkServerStatusChangeEvent(NetworkModule networkModule, MinecraftServer server,
			ServerStatus status) {
		this.networkModule = networkModule;
		this.server = server;
		this.status = status;
	}

	public ServerStatus getStatus() {
		return status;
	}

	public NetworkModule getNetworkModule() {
		return networkModule;
	}

	public MinecraftServer getServer() {
		return server;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public enum ServerStatus {
		REMOVED, ADDED;
	}
}