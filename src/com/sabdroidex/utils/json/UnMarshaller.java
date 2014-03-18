package com.sabdroidex.utils.json;

import java.io.IOException;

/**
 * Created by Marc on 17/11/13.
 */
public interface UnMarshaller {

    Object unMarshall(Object element, Class clazz) throws InstantiationException, IllegalAccessException, IOException;
}
