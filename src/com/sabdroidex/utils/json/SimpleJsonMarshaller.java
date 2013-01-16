package com.sabdroidex.utils.json;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SimpleJsonMarshaller {
    
    private Class<?> clazz;
    private Object result;
    
    public SimpleJsonMarshaller(Class<?> clazz) throws JSONException {
        this.clazz = clazz;
    }
    
    @SuppressWarnings("unchecked")
    public Object unmarshal(final JSONObject jsonObject) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        result = clazz.newInstance();
        
        try {
            Method[] methods = clazz.getDeclaredMethods();
            
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
                            exception.printStackTrace();
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
                                collection.add(element);
                            }
                            methods[i].invoke(result, collection);
                        }
                        catch (JSONException exception) {
                            exception.printStackTrace();
                        }
                    }
                    else if (setter.type() == JSONType.SIMPLE) {
                        try {
                            Object parameter = jsonObject.get(setter.name());
                            methods[i].invoke(result, parameter);
                        }
                        catch (JSONException exception) {
                            exception.printStackTrace();
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
