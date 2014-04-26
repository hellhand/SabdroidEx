package com.sabdroidex.utils.json.impl;

import com.sabdroidex.utils.json.Marshaller;
import com.sabdroidex.utils.json.UnMarshaller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Marc on 17/11/13.
 */
public class JSONMarshaller implements Marshaller, UnMarshaller {

    private static final String TAG = JSONMarshaller.class.getCanonicalName();

    public JSONMarshaller() {}

    @Override
    public Object marshall(CharSequence element, Class clazz) {
        throw new NoSuchMethodError();
    }

    @Override
    public Object unMarshall(CharSequence json, Class clazz) throws InstantiationException, IllegalAccessException, IOException, ParseException {

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(((String) json).getBytes());

        JSONParser jsonParser = new JSONParser();
        java.util.HashMap<String, Object> objectHashMap = (java.util.HashMap<String, Object>) jsonParser.parse(byteArrayInputStream, new java.util.concurrent.atomic.AtomicInteger(0), null);

        JSONPojoMapper simpleJSONMarshaller = new JSONPojoMapper(clazz);
        return simpleJSONMarshaller.unMarshal(objectHashMap);
    }
}
