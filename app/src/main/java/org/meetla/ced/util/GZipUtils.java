package org.meetla.ced.util;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GZipUtils {
    private static final int BUFFER_SIZE = 512;

    public static void gzip(InputStream is, OutputStream os) throws IOException {
        GZIPOutputStream gzipOs = new GZIPOutputStream(os);
        byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead;
        while ((bytesRead = is.read(buffer)) > -1) {
            gzipOs.write(buffer, 0, bytesRead);
        }
        gzipOs.close();
    }

    public static void gunzip(InputStream is, OutputStream os) throws IOException {
        GZIPInputStream gzipIs = new GZIPInputStream(is);
        byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead;
        while ((bytesRead = gzipIs.read(buffer)) > -1) {
            os.write(buffer, 0, bytesRead);
        }
        gzipIs.close();
    }

    public static byte[] gzip(byte[] data) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        gzip(new ByteArrayInputStream(data), os);
        return os.toByteArray();
    }

    public static byte[] gunzip(byte[] data) throws IOException {
        ByteArrayInputStream is = new ByteArrayInputStream(data);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        gunzip(is, os);
        return os.toByteArray();
    }
}
