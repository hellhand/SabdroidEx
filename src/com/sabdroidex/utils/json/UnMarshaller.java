package com.sabdroidex.utils.json;

import java.io.IOException;
import java.text.ParseException;

/**
 * Created by Marc on 17/11/13.
 */
public interface UnMarshaller {

    Object unMarshall(CharSequence element, Class clazz) throws InstantiationException, IllegalAccessException, IOException, ParseException;
}
