package com.sabdroidex.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Marc on 17/03/14.
 */
public class IOUtil {

    public static InputStream copy(InputStream inputStream) throws IOException {

        inputStream.reset();

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = inputStream.read(buffer)) > -1 ) {
            byteArrayOutputStream.write(buffer, 0, len);
        }
        byteArrayOutputStream.flush();

        inputStream.reset();
        return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());

    }
}
