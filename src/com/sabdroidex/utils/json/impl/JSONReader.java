package com.sabdroidex.utils.json.impl;

import java.io.IOException;
import java.io.Reader;

/**
 * Created by Marc on 17/11/13.
 */
public class JSONReader extends Reader {

    @Override
    public void close() throws IOException {

    }

    @Override
    public int read(char[] chars, int i, int i2) throws IOException {
        return 0;
    }

    @Override
    public int read() throws IOException {
        return super.read();
    }
}
