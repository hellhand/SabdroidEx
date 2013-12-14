package com.sabdroidex.test;

import android.test.AndroidTestCase;

import com.sabdroidex.utils.json.JSONParser;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Marc on 14/12/13.
 */
public class JSONParserTest extends AndroidTestCase {

    public void testParser() throws IOException {
        StringBuffer stringBuffer = new StringBuffer();

        InputStream stream = getClass().getResourceAsStream("show.json");
        int c;
        while ((c = stream.read()) != -1) {
            stringBuffer.append((char) c);
        }

        JSONParser jsonParser = new JSONParser();
        jsonParser.parse(stringBuffer.toString());
    }
}
