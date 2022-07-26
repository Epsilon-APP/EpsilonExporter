package fr.epsilon.exporter;

import java.io.*;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipFolder {
    public static void zipFiles(File parent, File destination, File... files) throws IOException {
        try (
                FileOutputStream fileWriter = new FileOutputStream(destination);
                ZipOutputStream zip = new ZipOutputStream(fileWriter)
        ) {
            zip.setLevel(Deflater.NO_COMPRESSION);

            for (File file : files) {
                addFileToZip(parent, file, file.getPath(), zip);
            }
        }
    }

    public static void zip(File src, File destination) throws IOException {
        try (
                FileOutputStream fileWriter = new FileOutputStream(destination);
                ZipOutputStream zip = new ZipOutputStream(fileWriter)
        ) {
            zip.setLevel(Deflater.NO_COMPRESSION);

            addFileToZip(src, src, src.getPath().substring(1), zip);
        }
    }

    public static void unzip(InputStream zip, File destination) throws IOException {
        if (!destination.exists())
            destination.mkdir();

        try (ZipInputStream zipIn = new ZipInputStream(zip)) {
            ZipEntry entry = zipIn.getNextEntry();
            while (entry != null) {
                String filePath = destination + File.separator + entry.getName();
                if (!entry.isDirectory()) {
                    extractFile(filePath, zipIn);
                } else {
                    File dir = new File(filePath);
                    dir.mkdirs();
                }
                zipIn.closeEntry();
                entry = zipIn.getNextEntry();
            }
        }
    }

    private static void addFileToZip(File parent, File file, String path, ZipOutputStream zip) throws IOException {
        if (file.isDirectory()) {
            for (File listFile : file.listFiles()) {
                addFileToZip(parent, listFile, listFile.getPath(), zip);
            }
        } else {
            byte[] buf = new byte[1024];
            int len;
            try (FileInputStream in = new FileInputStream(file)) {
                zip.putNextEntry(new ZipEntry(path.replace(parent != null ? parent.getPath() : "", "").substring(1)));
                while ((len = in.read(buf)) > 0) {
                    zip.write(buf, 0, len);
                }
            }
        }
    }

    private static void extractFile(String filePath, ZipInputStream zip) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
        byte[] bytesIn = new byte[1024];
        int read = 0;

        while ((read = zip.read(bytesIn)) != -1) {
            bos.write(bytesIn, 0, read);
        }
        bos.close();
    }
}

