package xyz.ufactions.prolib.networking;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import xyz.ufactions.prolib.api.Module;
import xyz.ufactions.prolib.file.ProLibConfig;
import xyz.ufactions.prolib.libs.F;
import xyz.ufactions.prolib.libs.FileHandler;
import xyz.ufactions.prolib.libs.UtilPlayer;
import xyz.ufactions.prolib.libs.UtilServer;
import xyz.ufactions.prolib.networking.command.HubCommand;
import xyz.ufactions.prolib.networking.command.SendCommand;
import xyz.ufactions.prolib.networking.command.ServerCommand;
import xyz.ufactions.prolib.networking.event.NetworkServerStatusChangeEvent;
import xyz.ufactions.prolib.redis.Utility;
import xyz.ufactions.prolib.redis.connect.RedisTransferManager;
import xyz.ufactions.prolib.redis.data.MinecraftServer;
import xyz.ufactions.prolib.script.ScriptManager;
import xyz.ufactions.prolib.updater.UpdateType;
import xyz.ufactions.prolib.updater.event.UpdateEvent;

import java.util.*;

public class NetworkModule extends Module {

    private String serverName;

    private FileHandler<?> serverGUIFile;

    private Collection<MinecraftServer> servers;
    private Set<String> transferring;

    @Override
    public void enable() {
        if (Utility.allowRedis()) {
            this.transferring = new HashSet<>();
            this.serverName = ProLibConfig.getInstance().getServerName();
            this.servers = Utility.getServerRepository().getServerStatuses();
            this.serverGUIFile = FileHandler.instance(this, getDataFolder(), "servergui.yml");

            registerEvents(this);

            for (MinecraftServer server : this.servers) {
                registerScript(server);
            }

            addCommand(new ServerCommand(this));
            addCommand(new SendCommand(this));
            addCommand(new HubCommand(this));
        }
    }

    // Methods

    public void transfer(String player, String destination) {
        if (getServer(destination) != null) {
            destination = getServer(destination).getName();
        }
        transferring.add(player);
        RedisTransferManager.getInstance().transfer(player, destination);
        runSyncLater(() -> transferring.remove(player), 60);
    }

    private Collection<MinecraftServer> findDifference(Collection<MinecraftServer> preCollection,
                                                       Collection<MinecraftServer> postCollection) {
        Collection<MinecraftServer> difference = new HashSet<>();
        for (MinecraftServer post : postCollection) {
            boolean found = false;
            for (MinecraftServer pre : preCollection) {
                if (post.getName().equalsIgnoreCase(pre.getName())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                difference.add(post);
            }
        }
        return difference;
    }

    private void registerScript(MinecraftServer server) {
        ScriptManager.getInstance().registerScript("server_" + server.getName() + "_tps", (player, script) -> String.valueOf(Utility.getServerRepository().getServerStatus(server.getName()).getTPS()));
        ScriptManager.getInstance().registerScript("server_" + server.getName() + "_players", (player, script) -> String.valueOf(Utility.getServerRepository().getServerStatus(server.getName()).getPlayerCount()));
        ScriptManager.getInstance().registerScript("server_" + server.getName() + "_maxplayers", (player, script) -> String.valueOf(Utility.getServerRepository().getServerStatus(server.getName()).getMaxPlayerCount()));
        ScriptManager.getInstance().registerScript("server_" + server.getName() + "_port", (player, script) -> String.valueOf(Utility.getServerRepository().getServerStatus(server.getName()).getPort()));
        ScriptManager.getInstance().registerScript("server_" + server.getName() + "_ram", (player, script) -> String.valueOf(Utility.getServerRepository().getServerStatus(server.getName()).getRam()));
        ScriptManager.getInstance().registerScript("server_" + server.getName() + "_maxram", (player, script) -> String.valueOf(Utility.getServerRepository().getServerStatus(server.getName()).getMaxRam()));
        ScriptManager.getInstance().registerScript("server_" + server.getName() + "_lastping", (player, script) -> String.valueOf(Utility.getServerRepository().getServerStatus(server.getName()).getLastCheckup()));
        ScriptManager.getInstance().registerScript("server_" + server.getName() + "_motd", (player, script) -> Utility.getServerRepository().getServerStatus(server.getName()).getMotd());
        ScriptManager.getInstance().registerScript("server_" + server.getName() + "_ip", (player, script) -> Utility.getServerRepository().getServerStatus(server.getName()).getPublicAddress());
    }

    // Events

    @EventHandler
    public void statusUpdate(UpdateEvent e) {
        if (e.getType() != UpdateType.SEC) return;

        Collection<MinecraftServer> postServers = Utility.getServerRepository().getServerStatuses();
        Collection<MinecraftServer> addedServers = findDifference(servers, postServers);
        Collection<MinecraftServer> removedServers = findDifference(postServers, servers);

        for (MinecraftServer server : addedServers) {
            for (Player player : UtilServer.getPlayers()) {
                if (player.hasPermission("prolib.networking.notify")) {
                    player.sendMessage(F.line());
                    player.sendMessage("");
                    UtilPlayer.message(player, F.main(Plugin.getName(), F.elem(server.getName()) + " " + F.cd(true) + " to the network."));
                    player.sendMessage("");
                    player.sendMessage(F.line());
                }
            }
            Bukkit.getServer().getPluginManager()
                    .callEvent(new NetworkServerStatusChangeEvent(this, server, NetworkServerStatusChangeEvent.ServerStatus.ADDED));
        }

        for (MinecraftServer server : removedServers) {
            for (Player player : UtilServer.getPlayers()) {
                if (player.hasPermission("prolib.networking.notify")) {
                    player.sendMessage(F.line());
                    player.sendMessage("");
                    UtilPlayer.message(player, F.main(Plugin.getName(), F.elem(server.getName()) + " " + F.cd(false) + " from the network."));
                    player.sendMessage("");
                    player.sendMessage(F.line());
                }
            }
            Bukkit.getServer().getPluginManager()
                    .callEvent(new NetworkServerStatusChangeEvent(this, server, NetworkServerStatusChangeEvent.ServerStatus.REMOVED));
        }

        this.servers = postServers;
    }

    @EventHandler
    public void onStatusChange(NetworkServerStatusChangeEvent e) {
        MinecraftServer server = e.getServer();
        if (e.getStatus() == NetworkServerStatusChangeEvent.ServerStatus.ADDED) {
            registerScript(server);
        } else if (e.getStatus() == NetworkServerStatusChangeEvent.ServerStatus.REMOVED) {
            ScriptManager.getInstance().unregisterScript("server_" + server.getName() + "_tps");
            ScriptManager.getInstance().unregisterScript("server_" + server.getName() + "_players");
            ScriptManager.getInstance().unregisterScript("server_" + server.getName() + "_maxplayers");
            ScriptManager.getInstance().unregisterScript("server_" + server.getName() + "_port");
            ScriptManager.getInstance().unregisterScript("server_" + server.getName() + "_ram");
            ScriptManager.getInstance().unregisterScript("server_" + server.getName() + "_maxram");
            ScriptManager.getInstance().unregisterScript("server_" + server.getName() + "_lastping");
            ScriptManager.getInstance().unregisterScript("server_" + server.getName() + "_motd");
            ScriptManager.getInstance().unregisterScript("server_" + server.getName() + "_ip");
        }
    }

    // Getters

    public int getGlobalPlayerCount() {
        int players = 0;
        for (MinecraftServer server : getServers()) {
            players += server.getPlayerCount();
        }
        return players;
    }

    public int getPlayerCount(String name) {
        for (MinecraftServer server : getServers()) {
            if (server.getName().equalsIgnoreCase(name)) {
                return server.getPlayerCount();
            }
        }
        return 0;
    }

    public boolean isOnline(String name) {
        for (MinecraftServer server : getServers()) {
            if (server.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public boolean isTransferring(String player) {
        return transferring.contains(player);
    }

    public Collection<MinecraftServer> getServers() {
        List<MinecraftServer> servers = new ArrayList<>(this.servers);
        servers.sort(new ServerSorter());
        return servers;
    }

    public Collection<String> getServerNames() {
        Collection<String> collection = new HashSet<>();
        for (MinecraftServer server : getServers()) {
            collection.add(server.getName());
        }
        return collection;
    }

    public MinecraftServer getServer(String name) {
        for (MinecraftServer server : getServers()) {
            if (server.getName().equalsIgnoreCase(name)) {
                return server;
            }
            if (server.getName().toLowerCase().startsWith(name.toLowerCase())) {
                return server;
            }
        }
        return null;
    }

    public String getServerName() {
        return serverName;
    }

    public FileHandler<?> getServerGUIFile() {
        return serverGUIFile;
    }
}