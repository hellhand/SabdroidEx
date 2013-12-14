package com.sabdroidex.utils.json.impl;

import com.sabdroidex.utils.json.Marshaller;
import com.sabdroidex.utils.json.UnMarshaller;

/**
 * Created by Marc on 17/11/13.
 */
public class JSONMarshaller implements Marshaller, UnMarshaller {

    private static final String TAG = JSONMarshaller.class.getCanonicalName();

    public JSONMarshaller() {}

    @Override
    public Object marshall(Object element) {
        throw new NoSuchMethodError();
    }

    @Override
    public Object unmarshall(Object element) {
        if (!(element instanceof String))
        {
            throw new IllegalArgumentException();
        }

        JSONParser jsonParser = new JSONParser();
        Object result = jsonParser.parse((String) element);

        return result;
    }
}
