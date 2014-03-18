package com.sabdroidex.test;

import android.test.AndroidTestCase;

import com.sabdroidex.utils.json.impl.JSONParser;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Marc on 14/12/13.
 */
public class JSONParserTest extends AndroidTestCase {

    public void testParser() throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        InputStream stream = getClass().getResourceAsStream("show.json");
        int c;
        while ((c = stream.read()) != -1) {
            byteArrayOutputStream.write((char) c);
        }

        InputStream inputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());

        JSONParser jsonParser = new JSONParser();
        Map<String, Object> result = (Map<String, Object>) jsonParser.parse(inputStream, new AtomicInteger(0), null);

        System.out.println(result);
    }
}
