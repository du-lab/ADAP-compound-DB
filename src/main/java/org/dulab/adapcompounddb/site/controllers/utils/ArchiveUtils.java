package org.dulab.adapcompounddb.site.controllers.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ArchiveUtils {

    public static byte[] zipBytes(String filename, byte[] input) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream)) {

            ZipEntry zipEntry = new ZipEntry(filename);
            zipEntry.setSize(input.length);
            zipOutputStream.putNextEntry(zipEntry);
            zipOutputStream.write(input);
            zipOutputStream.closeEntry();
            return byteArrayOutputStream.toByteArray();

        } catch (IOException e) {
            throw new IllegalStateException("Cannot create an archive: " + filename, e);
        }
    }

    public static byte[] unzipBytes(byte[] input) {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(input);
             ZipInputStream zipInputStream = new ZipInputStream(byteArrayInputStream);
             ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {

            ZipEntry zipEntry = zipInputStream.getNextEntry();
            if (zipEntry == null)
                return input;

            byte[] buffer = new byte[1024 * 1024];
            int length;

            while ((length = zipInputStream.read(buffer)) != -1)
                byteArrayOutputStream.write(buffer, 0, length);

            return byteArrayOutputStream.toByteArray();

        } catch (IOException e) {
            throw new IllegalStateException("Cannot read an archive", e);
        }
    }
}
