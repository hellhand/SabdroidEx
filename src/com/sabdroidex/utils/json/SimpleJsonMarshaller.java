/*
 * Copyright (C) 2011-2012  Marc Boulanger
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.*
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.sabdroidex.utils.json;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class SimpleJsonMarshaller {
    
    private Class<?> clazz;
    private Object result;
    private static final String TAG = SimpleJsonMarshaller.class.getCanonicalName();
    
    public SimpleJsonMarshaller(Class<?> clazz) throws JSONException {
        this.clazz = clazz;
    }
    
    @SuppressWarnings("unchecked")
    public Object unmarshal(final JSONObject jsonObject) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        result = clazz.newInstance();
        
        try {
            Method[] methods = clazz.getMethods();
            
            for (int i = 0; i < methods.length; i++) {
                JSONSetter setter = methods[i].getAnnotation(JSONSetter.class);
                if (setter != null) {
                    if (setter.type() == JSONType.JSONOBJECT) {
                        try {
                            SimpleJsonMarshaller simpleJsonMarshaller = new SimpleJsonMarshaller(methods[i].getParameterTypes()[0]);
                            JSONObject jsonElement = jsonObject.getJSONObject(setter.name());
                            Object parameter = simpleJsonMarshaller.unmarshal(jsonElement);
                            methods[i].invoke(result, parameter);
                        }
                        catch (JSONException exception) {
                            Log.d(TAG, exception.getLocalizedMessage());
                        }
                    }
                    else if (setter.type() == JSONType.LIST) {
                        try {
                            JSONArray jsonArray = jsonObject.getJSONArray(setter.name());
                            Collection<Object> collection = null;
                            if (methods[i].getParameterTypes()[0] == List.class) {
                                collection = new ArrayList<Object>();
                            }
                            else {
                                collection = (Collection<Object>) methods[i].getParameterTypes()[0].newInstance();
                            }
                            for (int j = 0; j < jsonArray.length(); j++) {
                                Object element = jsonArray.get(j);
                                if (setter.listClazz() != Void.class) {
                                    SimpleJsonMarshaller simpleJsonMarshaller = new SimpleJsonMarshaller(setter.listClazz());
                                    element = simpleJsonMarshaller.unmarshal((JSONObject) element);
                                }
                                collection.add(element);
                            }
                            methods[i].invoke(result, collection);
                        }
                        catch (JSONException exception) {
                            Log.d(TAG, exception.getLocalizedMessage());
                        }
                    }
                    else if (setter.type() == JSONType.SIMPLE) {
                        try {
                            Object parameter = jsonObject.get(setter.name());
                            methods[i].invoke(result, parameter);
                        }
                        catch (JSONException exception) {
                            Log.d(TAG, exception.getLocalizedMessage());
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            Log.d(TAG, e.getLocalizedMessage());
        }
        return result;
    }
}
