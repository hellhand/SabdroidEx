package com.sabdroidex.utils.json;

/**
 * Created by Marc on 17/11/13.
 */
public interface Marshaller {

    Object marshall(CharSequence element, Class clazz);
}
