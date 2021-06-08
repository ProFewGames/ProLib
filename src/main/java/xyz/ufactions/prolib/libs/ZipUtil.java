package xyz.ufactions.prolib.libs;

import org.apache.commons.lang.Validate;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipUtil {

    private static List<File> truncateFiles(File... files) {
        List<File> truncated = new ArrayList<>();

        for (File file : files) {
            if (file.isDirectory()) {
                for (File subfile : file.listFiles()) {
                    truncated.addAll(truncateFiles(subfile));
                }
                continue;
            }
            truncated.add(file);
        }

        return truncated;
    }

    public static void zipFolders(File destinationZip, File... filesToZip) {
        Validate.isTrue(!destinationZip.exists());

        List<File> files = truncateFiles(filesToZip);

        byte[] buffer = new byte[2048];
        try (ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(destinationZip)))) {
            for (File file : filesToZip) {
                zos.putNextEntry(new ZipEntry(file.getName()));
                try (FileInputStream fis = new FileInputStream(file)) {
                    int len;
                    while ((len = fis.read(buffer)) > 0) {
                        zos.write(buffer, 0, len);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void unzip(File zipFile, File destDir) {
        if (!destDir.exists()) destDir.mkdirs();

        byte[] buffer = new byte[1024];
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
            ZipEntry zipEntry;
            while ((zipEntry = zis.getNextEntry()) != null) {
                String fileName = zipEntry.getName();
                File newFile = new File(destDir, fileName);
                new File(newFile.getParent()).mkdirs();
                try (FileOutputStream fos = new FileOutputStream(newFile)) {
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}