package xyz.ufactions.prolib.networking.gui;

import xyz.ufactions.prolib.gui.ButtonBuilder;
import xyz.ufactions.prolib.gui.GUI;
import xyz.ufactions.prolib.libs.C;
import xyz.ufactions.prolib.libs.ColorLib;
import xyz.ufactions.prolib.libs.ItemBuilder;
import xyz.ufactions.prolib.networking.NetworkModule;

public class ServersGUI extends GUI<NetworkModule> {

    public ServersGUI(NetworkModule plugin) {
        super(plugin, C.mHead + C.Bold + "Servers", 54, GUIFiller.PANE);

        setPaneColor(ColorLib.randomColor());

        for (String key : plugin.getServerGUIFile().getConfigurationSection("servers").getKeys(false)) {
            String path = "servers." + key;
            ItemBuilder builder = ItemBuilder.itemFromConfig(plugin.getServerGUIFile().getConfig(), path);
            if (builder == null) {
                plugin.warning("Failed to load server GUI item '" + path + "'.");
                continue;
            }
            int slot = plugin.getServerGUIFile().getInt(path + ".slot", -1);
            String server = plugin.getServerGUIFile().getString(path + ".server");
            addButton(ButtonBuilder.instance(plugin)
                    .slot(slot)
                    .item(builder)
                    .onClick((player, type) -> {
                        player.closeInventory();
                        plugin.transfer(player.getName(), server);
                    })
                    .build());
        }
    }
}