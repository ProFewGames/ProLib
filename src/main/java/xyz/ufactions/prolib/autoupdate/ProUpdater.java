package xyz.ufactions.prolib.autoupdate;

import org.bukkit.Bukkit;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.FileUtil;
import xyz.ufactions.prolib.autoupdate.exception.RemoteVersionVerificationException;
import xyz.ufactions.prolib.autoupdate.exception.TaskAlreadyScheduled;
import xyz.ufactions.prolib.autoupdate.exception.URLTypeNotJar;
import xyz.ufactions.prolib.libs.C;
import xyz.ufactions.prolib.libs.F;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ProUpdater {

    private boolean scheduled = false;
    private boolean needsRestart = false;

    private final JavaPlugin plugin;
    private final File file;
    private final URL url;

    /**
     * {@link ProUpdater#ProUpdater(JavaPlugin, File, String, boolean)}
     */
    public ProUpdater(JavaPlugin plugin, File file, String url) throws IOException, URLTypeNotJar {
        this(plugin, file, url, true);
    }

    /**
     * Update mechanics for said plugin from global repository
     *
     * @param plugin         The updating plugin
     * @param file           The plugins data file; Get this by doing {@code this.getDataFile();} in your main class
     * @param url            The url we're checking there's an update in
     * @param jarCheck Should we check if the URL is going to return a Jar before downloading?
     * @throws IOException   If the URL cannot be parsed
     * @throws URLTypeNotJar The URL is not of type .jar
     */
    public ProUpdater(JavaPlugin plugin, File file, String url, boolean jarCheck) throws IOException, URLTypeNotJar {
        this.plugin = plugin;
        this.file = file;
        this.url = new URL(url);

        if (!plugin.getServer().getUpdateFolderFile().exists())
            plugin.getServer().getUpdateFolderFile().mkdirs();

        if (jarCheck)
            isOfJavaType();
        plugin.getLogger().info("Initialized plugin updater");
    }

    /**
     * Schedule an update checker to run every 2 minutes
     * If an update is found it will attempt to download it
     * then announce a reboot and reboot after 2 minutes
     * <p>
     * ALL SETTINGS HERE ARE SET TO NOTIFY BY DEFAULT
     *
     * @throws TaskAlreadyScheduled If the specified task is already scheduled
     */
    public void scheduleUpdater() throws TaskAlreadyScheduled {
        if (scheduled) throw new TaskAlreadyScheduled("Update Scheduler already running");
        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            if (needsRestart) {
                Bukkit.spigot().restart();
                return;
            }
            try {
                if (checkUpdate(false)) {
                    try {
                        if (downloadFromURL(true)) {
                            needsRestart = true;
                            Bukkit.broadcastMessage(F.line());
                            Bukkit.broadcastMessage(C.mBody + "Update scheduled! This server will restart in 2 minutes.");
                            Bukkit.broadcastMessage(F.line());
                        }
                    } catch (Throwable e) {
                        plugin.getLogger().info("Download from global repository failed.");
                        e.printStackTrace();
                    }
                }
            } catch (IOException | NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }, 0L, 2400L);
        scheduled = true;
    }

    /**
     * @param notify Shall console be notified of the download actions
     * @throws IOException URL Connection cannot be established
     */
    public boolean downloadFromURL(boolean notify) throws Throwable {
        if (notify) plugin.getLogger().info("Starting download...");
        try {
            URLConnection connection = url.openConnection();
            int fileSize = connection.getContentLength();
            if (notify) plugin.getLogger().info("Total download size: " + fileSize + "kb");
            float totalDataRead = 0;

            try (BufferedInputStream in = new BufferedInputStream(connection.getInputStream())) {
                FileOutputStream out = new FileOutputStream(new File(this.plugin.getDataFolder(), "cached.jar"));
                try (BufferedOutputStream bout = new BufferedOutputStream(out, 1024)) {
                    byte[] data = new byte[1024];
                    int i;
                    while ((i = in.read(data, 0, 1024)) >= 0) {
                        totalDataRead += i;
                        bout.write(data, 0, i);
                        float percent = (totalDataRead * 100) / fileSize;
                        if (notify) plugin.getLogger().info(percent + "% complete");
                    }
                }
            }
            if (validateDownload(notify)) {
                if (notify) plugin.getLogger().info("Download complete!");
                return true;
            } else {
                return false;
            }
        } catch (IOException | InvalidDescriptionException e) {
            if (notify)
                plugin.getLogger().severe("Download/Verification cannot be completed! " + e.getLocalizedMessage());
            throw e.getCause();
        }
    }

    /**
     * @param notify Verbose?
     * @return True if there is an update available in the repository
     */
    public boolean checkUpdate(boolean notify) throws IOException, NoSuchAlgorithmException {
        File blacklist = new File(plugin.getDataFolder(), "md5_blacklist.pro");
        if (notify) plugin.getLogger().info("Validating blacklist...");
        if (blacklist.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(blacklist))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (getMD5().equals(line)) {
                        if (notify) plugin.getLogger().info("Remote MD5 is Blacklisted");
                        return false; // MD5 is Blacklisted
                    }
                }
            }
        }
        if (notify)
            plugin.getLogger().info("Update available!");
        return true;
    }

    /**
     * @param notify Verbose?
     * @throws IOException                 Jar inputstream cannot be obtained
     * @throws InvalidDescriptionException Invalid plugin description file in the jar
     */
    private boolean validateDownload(boolean notify) throws IOException, InvalidDescriptionException, RemoteVersionVerificationException, NoSuchAlgorithmException {
        if (notify) plugin.getLogger().info("Validating cached download...");
        File cached = new File(this.plugin.getDataFolder(), "cached.jar");
        if (!cached.exists())
            return false;
        JarFile jar = new JarFile(cached);
        JarEntry entry = jar.getJarEntry("plugin.yml");
        InputStream stream = jar.getInputStream(entry);
        PluginDescriptionFile pdf = new PluginDescriptionFile(stream);

        double remoteVersion;
        double version;
        try {
            remoteVersion = Double.parseDouble(pdf.getVersion());
        } catch (NumberFormatException e) {
            if (notify)
                plugin.getLogger().warning("Could not verify remote version, use numerical format #.## in your PDF version.");
            cached.delete();
            throw new RemoteVersionVerificationException("Cannot verify remote version! Please use #.## numerical format in your PDF version. This error will persist if not resolved globally.");
        }
        try {
            version = Double.parseDouble(plugin.getDescription().getVersion());
        } catch (NumberFormatException e) {
            if (notify)
                plugin.getLogger().warning("Could not verify this installations version. If this is a build error fix the PDF then recompile and deply with a version higher than " + remoteVersion + ". Allowing installation.");
            version = -1;
        }

        if (remoteVersion <= version) {
            if (notify)
                plugin.getLogger().warning("Remote version is lower than current version... Invalidating cache");
            FileWriter fw = new FileWriter(new File(this.plugin.getDataFolder(), "md5_blacklist.pro"));
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(getMD5());
            bw.flush();
            if (notify)
                plugin.getLogger().warning("MD5 Blacklisted for this download.");
            cached.delete();
            return false;
        } else {
            if (notify) plugin.getLogger().info("Valid cache! Transferring cache to update folder");
            FileUtil.copy(cached, new File(this.plugin.getServer().getUpdateFolderFile(), this.file.getName()));
            if (notify) plugin.getLogger().info("Validated.");
        }
        cached.delete();
        return true;
    }

    /**
     * @return The MD5 Hash of the file from the URL
     * @throws NoSuchAlgorithmException The MD5 Algorithm is not found
     * @throws IOException              URL Connection cannot be established
     */
    private String getMD5() throws NoSuchAlgorithmException, IOException {
        MessageDigest md = MessageDigest.getInstance("MD5");

        try (DigestInputStream in = new DigestInputStream(url.openStream(), md)) {
            byte[] ignoredBuffer = new byte[8 * 1024]; // Up to 8K per read
            while (in.read(ignoredBuffer) != -1) {
            }
        }
        byte[] digest = md.digest();
        StringBuilder builder = new StringBuilder();

        for (byte b : digest) {
            builder.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
        }
        return builder.toString();
    }

    /**
     * @return If the file from the URL is of type JAVA
     * @throws IOException URL Connection cannot be established
     */
    private boolean isOfJavaType() throws IOException, URLTypeNotJar {
        String contentType = url.openConnection().getContentType();
        boolean isJar = contentType.equals("application/java-archive");
        if (!isJar) {
            throw new URLTypeNotJar("URL is not of type 'JAR'. Content type: \"" + contentType + "\" != application/java-archive");
        }
        return true;
    }
}