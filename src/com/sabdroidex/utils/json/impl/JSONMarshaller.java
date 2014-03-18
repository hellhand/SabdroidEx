package com.sabdroidex.utils.json.impl;

import com.sabdroidex.utils.json.Marshaller;
import com.sabdroidex.utils.json.UnMarshaller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Marc on 17/11/13.
 */
public class JSONMarshaller implements Marshaller, UnMarshaller {

    private static final String TAG = JSONMarshaller.class.getCanonicalName();

    public JSONMarshaller() {}

    @Override
    public Object marshall(Object element, Class clazz) {
        throw new NoSuchMethodError();
    }

    @Override
    public Object unMarshall(Object element, Class clazz) throws InstantiationException, IllegalAccessException, IOException {
        if (!(element instanceof String))
        {
            throw new IllegalArgumentException();
        }

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(((String) element).getBytes());

        JSONParser jsonParser = new JSONParser();
        return jsonParser.parse(byteArrayInputStream, new AtomicInteger(0), null);
    }
}
