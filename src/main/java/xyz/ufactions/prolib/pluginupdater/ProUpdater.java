package xyz.ufactions.prolib.pluginupdater;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.FileUtil;
import xyz.ufactions.prolib.api.MegaPlugin;
import xyz.ufactions.prolib.file.ProLibConfig;
import xyz.ufactions.prolib.libs.C;
import xyz.ufactions.prolib.libs.F;
import xyz.ufactions.prolib.pluginupdater.command.ProUpdaterCommand;
import xyz.ufactions.prolib.redis.connect.RedisTransferManager;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ProUpdater {

    private int scheduleID = -1;
    private boolean rebootQueued = false;

    private final MegaPlugin plugin;
    private final URL url;

    /**
     * {@link ProUpdater#ProUpdater(MegaPlugin, String, Authenticator)}
     */
    public ProUpdater(MegaPlugin plugin, String downloadURL) throws MalformedURLException {
        this(plugin, downloadURL, null);
    }

    /**
     * Plugin Updater.
     *
     * @param plugin        The updating plugin
     * @param downloadURL   Where the JAR file is located online
     * @param authenticator If the URL is password protected this will be the authenticator
     * @throws MalformedURLException The URL cannot be parsed
     */
    public ProUpdater(MegaPlugin plugin, String downloadURL, Authenticator authenticator) throws MalformedURLException {
        this.plugin = plugin;
        this.url = new URL(downloadURL);

        if (authenticator != null)
            Authenticator.setDefault(authenticator);

        if (plugin.getServer().getUpdateFolderFile().mkdirs()) plugin.log(getName(), "Created updates folder");

        plugin.addCommand(new ProUpdaterCommand(plugin, this));
    }

    /**
     * <p>Schedule an update checker to run every 1 hour
     * If an update is found it will attempt to download it
     * then queue a reboot if successful.</p>
     */
    public void scheduleUpdater() {
        if (scheduleID != -1) return;
        new BukkitRunnable() {

            @Override
            public void run() {
                if (checkUpdate(true)) {
                    downloadFromURL(true);
                }
            }
        }.runTaskTimerAsynchronously(plugin, 0L, 72000L /* Every Hour */);
    }

    /**
     * Cancel the scheduled plugin updater if already scheduled.
     */
    public void cancelUpdater() {
        if (scheduleID == -1) return;
        plugin.getScheduler().cancelTask(scheduleID);
        scheduleID = -1;
    }

    /**
     * @param notify Verbose
     * @return {@code true} if there is an update available online otherwise {@code false}.
     */
    public boolean checkUpdate(boolean notify) {
        if (rebootQueued) {
            if (notify)
                plugin.log(getName(), "A reboot is currently pending and updates may not be checked at the moment.");
            return false;
        }
        File blacklist = new File(plugin.getDataFolder(), "md5_blacklist.pro");
        if (notify) plugin.log(getName(), "Validating Blacklist...");
        if (blacklist.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(blacklist))) {
                String localMD5;
                String onlineMD5 = getMD5();
                while ((localMD5 = reader.readLine()) != null) {
                    if (onlineMD5.equals(localMD5)) {
                        if (notify) plugin.log(getName(), "Remote MD5 is blacklisted.");
                        return false;
                    }
                }
            } catch (NoSuchAlgorithmException | IOException e) {
                plugin.warning(getName(), "An error has occurred while checking blacklisted MD5's.");
                e.printStackTrace();
            }
        }
        if (notify) plugin.log(getName(), "Update Available!");
        return true;
    }

    /**
     * Downloads a plugin from the specified URL and queues a server restart if possible
     *
     * @param notify Verbose
     */
    public void downloadFromURL(boolean notify) {
        if (rebootQueued) {
            if (notify)
                plugin.log(getName(), "A reboot is currently pending and updates may not be downloaded at the moment.");
            return;
        }
        if (notify) plugin.log(getName(), "Starting download...");
        try {
            URLConnection connection = url.openConnection();
            int fileSize = connection.getContentLength();
            if (notify) plugin.log(getName(), "Total Download Size: " + fileSize + "kb");

            try (BufferedInputStream bis = new BufferedInputStream(connection.getInputStream());
                 BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(new File(this.plugin.getDataFolder(), "cached.jar")))) {
                byte[] data = new byte[1024];
                int i;
                while ((i = bis.read(data, 0, 1024)) >= 0) {
                    bos.write(data, 0, i);
                }
            }
            plugin.log(getName(), "Download Complete!");
        } catch (Exception e) {
            plugin.warning(getName(), "Download Failed!");
            e.printStackTrace();
            return;
        }
        try {
            validateDownload(notify);
        } catch (IOException | InvalidDescriptionException | NoSuchAlgorithmException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param notify Verbose
     * @return {@code true} if the download has been validated and moved to the updates folder otherwise {@code false}
     */
    private boolean validateDownload(boolean notify) throws IOException, InvalidDescriptionException, NoSuchAlgorithmException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if (rebootQueued) {
            if (notify)
                plugin.log(getName(), "A reboot is currently pending further action is not required.");
            return false;
        }
        if (notify) plugin.log(getName(), "Validating cached download...");
        File cached = new File(this.plugin.getDataFolder(), "cached.jar");
        if (!cached.exists()) return false;
        JarFile jar = new JarFile(cached);
        JarEntry entry = jar.getJarEntry("plugin.yml");
        double remoteVersion;
        try (BufferedInputStream bis = new BufferedInputStream(jar.getInputStream(entry))) {
            PluginDescriptionFile pdf = new PluginDescriptionFile(bis);
            try {
                remoteVersion = Double.parseDouble(pdf.getVersion());
            } catch (NumberFormatException e) {
                if (notify)
                    plugin.warning(getName(), "Could not verify remote version, use numerical format #.## in your PDF version.");
                cached.delete();
                return false;
            }
        }
        double localVersion;
        try {
            localVersion = Double.parseDouble(plugin.getDescription().getVersion());
        } catch (NumberFormatException e) {
            if (notify)
                plugin.warning(getName(), "Could not verify this installations version. If this is a build error fix the PDF then recompile and deploy with a version higher than " + remoteVersion + ". Allowing installation.");
            localVersion = -1;
        }
        if (remoteVersion <= localVersion) {
            if (notify) plugin.log(getName(), "Remote version is lower than current version. Invalidating Cache.");
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(new File(this.plugin.getDataFolder(), "md5_blacklist.pro")))) {
                writer.write(getMD5());
            }
            if (notify) plugin.log(getName(), "MD5 blacklisted for this download.");
            cached.delete();
            return false;
        } else {
            if (notify) plugin.log(getName(), "Valid Cache! Transferring cache to update folder");
            Method getFileMethod = JavaPlugin.class.getDeclaredMethod("getFile");
            getFileMethod.setAccessible(true);
            File localJarFile = (File) getFileMethod.invoke(plugin);
            FileUtil.copy(cached, new File(this.plugin.getServer().getUpdateFolderFile(), localJarFile.getName()));
            if (notify) plugin.log(getName(), "Validated.");
            queueReboot();
            return true;
        }
    }

    /**
     * @return The MD5 Hash of the file from the URL
     * @throws NoSuchAlgorithmException The MD5 Algorithm is not found
     * @throws IOException              URL Connection cannot be established
     */
    private String getMD5() throws NoSuchAlgorithmException, IOException {
        MessageDigest md = MessageDigest.getInstance("MD5");

        try (DigestInputStream dis = new DigestInputStream(url.openStream(), md)) {
            byte[] ignoredBuffer = new byte[8 * 1024]; // Up to 8K(B) per read
            //noinspection StatementWithEmptyBody
            while (dis.read(ignoredBuffer) != -1) ;
        }
        byte[] digest = md.digest();
        StringBuilder builder = new StringBuilder();

        for (byte b : digest)
            builder.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));

        return builder.toString();
    }

    /**
     * Queues a server reboot to occur 2 minutes after the original method call.
     */
    private void queueReboot() {
        if (rebootQueued) {
            plugin.warning(getName(), "A reboot is already queued, further queueing is not possible.");
            return;
        }
        plugin.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                RedisTransferManager.getInstance().handleTransfer(player, ProLibConfig.getInstance().getFallbackServer());
            }
        }, 2300);
        plugin.getScheduler().scheduleSyncDelayedTask(plugin, () -> plugin.getServer().spigot().restart(), 2400);
        Bukkit.broadcastMessage(F.line());
        Bukkit.broadcastMessage(" ");
        Bukkit.broadcastMessage(C.mBody + C.Bold + "A network update has been found. This server will restart in 2 minutes.");
        Bukkit.broadcastMessage(" ");
        Bukkit.broadcastMessage(F.line());
    }

    private String getName() {
        return "Plugin Updater";
    }
}