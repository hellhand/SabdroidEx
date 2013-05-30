/*
 * Copyright (C) 2011-2012  Marc Boulanger
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.*
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.utils;

import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;

public class FileUtil {

    private static final String TAG = FileUtil.class.getSimpleName();

    /**
     * This method reads the file at a given path and puts it in a byte array.
     * This is mostly used to read bitmap files.
     *
     * @param path it is the path of the file containing the filename.
     * @return the byte array representing the bitmap.
     */
    public static byte[] getFileAsByteArray(final String path) {

        byte[] dat = null;
        try {
            final File file = new File(path);
            final FileInputStream fileInputStream = new FileInputStream(file);

            final ByteArrayOutputStream bao = new ByteArrayOutputStream();
            final byte[] buf = new byte[4096];
            for (; ; ) {
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
        catch (final FileNotFoundException e) {
            Log.e(TAG, e.getLocalizedMessage());
        }
        catch (final IOException e) {
            Log.e(TAG, e.getMessage());
        }
        return dat;
    }

    /**
     * This method reads the file at a given path and puts it in a char array.
     * This is mostly used to read nzb files.
     *
     * @param path it is the path of the file containing the filename.
     * @return the char array representing the nzb.
     */
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
        catch (final FileNotFoundException e) {
            Log.e(TAG, e.getLocalizedMessage());
        }
        catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return dat;
    }

    /**
     * This method retrieves the file name out a complete path.
     *
     * @param path it is the path of the file containing the filename.
     * @return a String containing the filename.
     */
    public static String getFileName(final String path) {

        final File file = new File(path);
        return file.getName();
    }

    /**
     * This method creates the given path.
     * The path can be made of multiple non-existent directories.
     * @param path the path to be created.
     */
    public static void createDirectory(final String path) {

        final File file = new File(path);
        file.mkdirs();
    }
}
