package xyz.ufactions.prolib.libs;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import xyz.ufactions.prolib.ProLib;
import xyz.ufactions.prolib.api.IModule;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.NotDirectoryException;
import java.util.List;

public abstract class FileHandler<Plugin extends IModule> {

    public static <Plugin extends IModule> FileHandler<Plugin> instance(Plugin plugin, File directory, String fileName) {
        return new FileHandler<Plugin>(plugin, directory, fileName) {
        };
    }

    protected final Plugin plugin;
    protected final String fileName;
    private final File directory;
    private File file;
    private FileConfiguration config;

    public FileHandler(Plugin plugin, String fileName) {
        this(plugin, plugin.getDataFolder(), fileName);
    }

    public FileHandler(Plugin plugin, File directory, String fileName) {
        this.plugin = plugin;
        this.fileName = fileName;
        this.directory = directory;
        this.reload();
    }

    public final void set(String path, Object value) {
        this.getConfig().set(path, value);
    }

    public boolean contains(String path) {
        return this.getConfig().contains(path);
    }

    public ConfigurationSection getConfigurationSection(String path) {
        return this.getConfig().getConfigurationSection(path);
    }

    public List<String> getStringList(String path) {
        return this.getConfig().getStringList(path);
    }

    public long getLong(String path) {
        return this.getConfig().getLong(path);
    }

    public List<?> getList(String path) {
        return this.getConfig().getList(path);
    }

    public boolean getBoolean(String path) {
        return this.getConfig().getBoolean(path);
    }

    public int getInt(String path) {
        return this.getConfig().getInt(path);
    }

    public double getDouble(String path) {
        return this.getConfig().getDouble(path);
    }

    public String getString(String path) {
        return this.getConfig().getString(path);
    }

    public long getLong(String path, long def) {
        return this.getConfig().getLong(path, def);
    }

    public List<?> getList(String path, List<?> def) {
        return this.getConfig().getList(path, def);
    }

    public boolean getBoolean(String path, boolean def) {
        return this.getConfig().getBoolean(path, def);
    }

    public int getInt(String path, int def) {
        return this.getConfig().getInt(path, def);
    }

    public double getDouble(String path, double def) {
        return this.getConfig().getDouble(path, def);
    }

    public String getString(String path, String def) {
        return this.getConfig().getString(path, def);
    }

    public Object get(String path, Object def) {
        return this.getConfig().get(path, def);
    }

    public final void reload() {
        if (this.directory.exists()) { // Directory exist?
            if (!this.directory.isDirectory()) { // File is directory?
                try { // Not directory may contain important info! Error :)
                    throw new NotDirectoryException("'" + this.directory.getName() + "' is not a directory.");
                } catch (NotDirectoryException e) {
                    e.printStackTrace();
                }
            }
        } else { // Directory doesn't exist.
            this.directory.mkdir(); // Make directory
        }
        this.file = new File(this.directory, this.fileName);
        boolean created = false;
        if (!file.exists()) {
            URL url = plugin.getClass().getClassLoader().getResource(fileName);
            if (url != null) {
                plugin.debug("Attempting to create '" + this.fileName + "' from resources.");
                try {
                    URLConnection connection = url.openConnection();
                    connection.setUseCaches(false);
                    InputStream in = connection.getInputStream();

                    OutputStream out = new FileOutputStream(file);
                    byte[] buf = new byte[1024];

                    int len;
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                    out.close();
                    in.close();
                } catch (IOException e) {
                    plugin.debug("Failed to create file from resources.");
                    if (ProLib.debugging()) e.printStackTrace();
                }
            }
            created = true;
        }
        this.config = YamlConfiguration.loadConfiguration(this.file);
        if (created) create();
        onReload();
    }

    public final void save() throws IOException {
        config.save(file);
    }

    public final boolean exists() {
        return this.file.exists();
    }

    public final FileConfiguration getConfig() {
        return this.config;
    }

    /**
     * Called when a file is created.
     */
    public void create() {
    }

    /**
     * Called when the file is reloaded.
     */
    protected void onReload() {
    }
}