package com.utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;

import android.util.Log;

public class FileUtil {

    private static final String TAG = FileUtil.class.getSimpleName();

    public static byte[] getFileAsByteArray(final String path) {

        byte[] dat = null;
        try {
            final File file = new File(path);
            final FileInputStream fileInputStream = new FileInputStream(file);

            final ByteArrayOutputStream bao = new ByteArrayOutputStream();
            final byte[] buf = new byte[4096];
            for (;;) {
                final int nb = fileInputStream.read(buf);
                if (nb <= 0) {
                    break;
                }
                bao.write(buf, 0, nb);
            }
            dat = bao.toByteArray();
            bao.close();

            fileInputStream.close();
        }
        catch (final IOException e) {
            Log.e(TAG, e.getMessage());
        }
        return dat;
    }

    public static char[] getFileAsCharArray(final String path) {

        char[] dat = null;
        try {
            final File file = new File(path);
            final FileInputStream fileInputStream = new FileInputStream(file);

            BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream, Charset.defaultCharset()));

            int len = (int) file.length();
            
            dat = new char[len];
            int i = 0;
            int c;
            while ((c = reader.read()) != -1) {
                char character = (char) c;
                dat[i] = character;
                i++;
            }
            
            reader.close();
        }
        catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return dat;
    }
    
    public static byte[] getFileAsByteArray(final URL url) {

        byte[] dat = null;
        try {
            final File file = new File(url.getFile());
            final FileInputStream fileInputStream = new FileInputStream(file);

            final ByteArrayOutputStream bao = new ByteArrayOutputStream();
            final byte[] buf = new byte[4096];
            for (;;) {
                final int nb = fileInputStream.read(buf);
                if (nb <= 0) {
                    break;
                }
                bao.write(buf, 0, nb);
            }
            dat = bao.toByteArray();
            bao.close();

            fileInputStream.close();
        }
        catch (final IOException e) {
            Log.e(TAG, e.getMessage());
        }
        return dat;
    }

    public static char[] getFileAsCharArray(final URL url) {

        char[] dat = null;
        try {
            final File file = new File(url.getFile());
            final FileInputStream fileInputStream = new FileInputStream(file);

            BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream, Charset.defaultCharset()));

            int len = (int) file.length();
            
            dat = new char[len];
            int i = 0;
            int c;
            while ((c = reader.read()) != -1) {
                char character = (char) c;
                dat[i] = character;
                i++;
            }
            
            reader.close();
        }
        catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return dat;
    }
    
    
    public static String getFileName(final String path) {
        final File file = new File(path);
        return file.getName();
    }
}
