package xyz.ufactions.prolib.cg.file;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import xyz.ufactions.prolib.cg.CustomGUI;
import xyz.ufactions.prolib.command.CommandBase;
import xyz.ufactions.prolib.gui.GUI;
import xyz.ufactions.prolib.gui.button.Button;
import xyz.ufactions.prolib.libs.FileHandler;
import xyz.ufactions.prolib.libs.ItemBuilder;
import xyz.ufactions.prolib.script.ScriptManager;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GUIFileHandler {

    private final CustomGUI plugin;

    public GUIFileHandler(CustomGUI plugin) {
        this.plugin = plugin;
        if (getGUIFiles().length == 0) {
            new FileHandler(plugin.getPlugin(), plugin.getDataFolder(), "MasterGUI.yml") {
            };
            new FileHandler(plugin.getPlugin(), plugin.getDataFolder(), "SubGUI.yml") {
            };
        }
    }

    public Map<String, GUI<?>> getGUIs(boolean registerCommands) {
        Map<String, GUI<?>> guis = new HashMap<>();
        for (File file : getGUIFiles()) {
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);

            String name = config.getString("name");
            int slots = config.getInt("slots");
            ChatColor color = ChatColor.valueOf(config.getString("color"));
            GUI.GUIFiller filler;

            try {
                filler = GUI.GUIFiller.valueOf(config.getString("filler").toUpperCase());
            } catch (EnumConstantNotPresentException e) {
                filler = GUI.GUIFiller.PANE;
            }

            GUI<CustomGUI> gui = new GUI<CustomGUI>(plugin, name, slots, filler) {
            };
            gui.setPaneColor(color);

            for (String key : config.getConfigurationSection("buttons").getKeys(false)) {
                ItemBuilder builder = ItemBuilder.itemFromConfig(config, "buttons." + key);
                long refreshTime = config.getLong("buttons." + key + ".refreshTime", -1);
                int slot = config.getInt("buttons." + key + ".slot", -1);
                final List<String> actions = config.getStringList("buttons." + key + ".action");

                gui.addButton(new Button<CustomGUI>(plugin, builder, refreshTime, slot) {

                    @Override
                    public void onClick(Player player, ClickType type) {
                        ScriptManager scriptManager = plugin.getPlugin().getModule(ScriptManager.class);
                        for (String action : actions) {
                            if (scriptManager != null && scriptManager.isEnabled()) {
                                action = scriptManager.replace(player, action);
                            }
                            if (action.isEmpty()) continue;
                            action = action.replaceAll("%player%", player.getName());
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), action);
                        }
                    }
                });
            }
            guis.put(file.getName(), gui);

            if (registerCommands) {
                String command = config.getString("command", "");
                final String permission = config.getString("permission", "");
                if (command != null && !command.isEmpty()) {
                    plugin.addCommand(new CommandBase<CustomGUI>(plugin, command) {

                        @Override
                        protected void execute(CommandSender sender, String[] args) {
                            if (!isPlayer(sender)) return;
                            if (permission != null && !permission.isEmpty())
                                if (!permissionCheck(sender, permission)) return;
                            gui.openInventory((Player) sender);
                        }

                        @Override
                        public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
                            return null;
                        }
                    });
                }
            }
        }
        return guis;
    }

    public File[] getGUIFiles() {
        return plugin.getDataFolder().listFiles(file -> file.getName().toLowerCase().endsWith(".yml"));
    }
}