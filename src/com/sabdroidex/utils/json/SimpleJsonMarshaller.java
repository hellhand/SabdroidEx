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

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Debug;
import android.util.Log;

import com.sabdroidex.data.UnknowMappingElement;

public class SimpleJsonMarshaller {

    private Class<?> clazz;
    private Object result;
    private static final String TAG = SimpleJsonMarshaller.class.getCanonicalName();

    public SimpleJsonMarshaller(Class<?> clazz) throws JSONException {
        this.clazz = clazz;
    }

    /**
     * This method creates an instance of the object targeted by the Class passed to the constructor,
     * it then tries to call all the methods with the JSONSetter annotation with the corresponding field.
     * 
     * @param jsonObject
     *            This is the JSON object that will be used to fill-up an
     *            instance of the Class that is targeted.
     * @return
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    @SuppressWarnings("unchecked")
    public Object unmarshal(final JSONObject jsonObject) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        result = clazz.newInstance();

        try {
            Method[] methods = clazz.getMethods();

            for (int i = 0; i < methods.length; i++) {
                JSONSetter setter = methods[i].getAnnotation(JSONSetter.class);
                /**
                 * Checking if the method has the JSONSetter annotation This
                 * only allows one parameter per setter method
                 */
                if (setter != null) {
                    /**
                     * Checking the JSONType defined in the annotation
                     */
                    if (setter.type() == JSONType.SIMPLE) {
                        /**
                         * Used for simple object (String, Integer, etc ...)
                         */
                        try {
                            Object parameter = jsonObject.get(setter.name());
                            methods[i].invoke(result, parameter);
                        }
                        catch (JSONException exception) {
                            if (Debug.isDebuggerConnected()) {
                                Log.e(TAG, methods[i].getName() + " " + exception.getLocalizedMessage());
                            }
                        }
                        catch (IllegalArgumentException exception) {
                            /**
                             * This happens if the object returned by the getter
                             * is a null, it would be defined as a JSONObject
                             * and thus cause an IllegalArgumentException when
                             * calling the targeted method.
                             * 
                             * This is yet another problem in an Android API
                             * which is bypassed by creating an empty object of
                             * the awaited type.
                             */
                            if (Debug.isDebuggerConnected()) {
                                Log.w(TAG, methods[i].getName() + " " + exception.getLocalizedMessage());
                            }
                            try {
                                Constructor<?>[] constructors = methods[i].getParameterTypes()[0].getConstructors();
                                Object parameter = null;

                                /**
                                 * Checking all the constructor of the parameter
                                 * object.
                                 */
                                for (Constructor<?> constructor : constructors) {
                                    Class<?>[] params = constructor.getParameterTypes();
                                    if (params.length == 0) {
                                        /**
                                         * If the empty constructor is found we
                                         * use it.
                                         */
                                        parameter = constructor.newInstance();
                                        break;
                                    }
                                    else if (params[0].isPrimitive()) {
                                        /**
                                         * If a constructor using a primitive is
                                         * found we use it, this happens for
                                         * classes that extend Number.
                                         */
                                        parameter = constructor.newInstance(0);
                                    }
                                }

                                methods[i].invoke(result, parameter);
                            }
                            catch (Exception e) {
                                if (Debug.isDebuggerConnected()) {
                                    Log.e(TAG, methods[i].getName() + " " + e.getLocalizedMessage());
                                }
                            }
                        }
                    }
                    else if (setter.type() == JSONType.JSON_OBJECT) {
                        /**
                         * Used for object that are instantiated from JSON
                         * (String, Integer, etc ...)
                         */
                        try {
                            SimpleJsonMarshaller simpleJsonMarshaller = new SimpleJsonMarshaller(methods[i].getParameterTypes()[0]);
                            JSONObject jsonElement = jsonObject.getJSONObject(setter.name());
                            Object parameter = simpleJsonMarshaller.unmarshal(jsonElement);
                            methods[i].invoke(result, parameter);
                        }
                        catch (JSONException exception) {
                            if (Debug.isDebuggerConnected()) {
                                Log.e(TAG, methods[i].getName() + " " + exception.getLocalizedMessage());
                            }
                            try {
                                Constructor<?>[] constructors = methods[i].getParameterTypes()[0].getConstructors();
                                Object parameter = null;

                                /**
                                 * Checking all the constructor of the parameter
                                 * object.
                                 */
                                for (Constructor<?> constructor : constructors) {
                                    Class<?>[] params = constructor.getParameterTypes();
                                    if (params.length == 0) {
                                        /**
                                         * If the empty constructor is found we
                                         * use it.
                                         */
                                        parameter = constructor.newInstance();
                                        break;
                                    }
                                    else if (params[0].isPrimitive()) {
                                        /**
                                         * If a constructor using a primitive is
                                         * found we use it, this happens for
                                         * classes that extend Number.
                                         */
                                        parameter = constructor.newInstance(0);
                                    }
                                }

                                methods[i].invoke(result, parameter);
                            }
                            catch (Exception e) {
                                if (Debug.isDebuggerConnected()) {
                                    Log.e(TAG, methods[i].getName() + " " + e.getLocalizedMessage());
                                }
                            }
                        }
                    }
                    else if (setter.type() == JSONType.LIST) {
                        /**
                         * Used for object that represent a Collection (List,
                         * ArrayList, Vector, etc ...)
                         */
                        try {
                            JSONArray jsonArray = jsonObject.getJSONArray(setter.name());
                            Collection<Object> collection = null;
                            if (methods[i].getParameterTypes()[0] == List.class) {
                                collection = (Collection<Object>) setter.listClazz().newInstance();
                            }
                            else {
                                collection = (Collection<Object>) methods[i].getParameterTypes()[0].newInstance();
                            }

                            for (int j = 0; j < jsonArray.length(); j++) {
                                Object element = jsonArray.get(j);
                                if (setter.objectClazz() != Void.class) {
                                    SimpleJsonMarshaller simpleJsonMarshaller = new SimpleJsonMarshaller(setter.objectClazz());
                                    element = simpleJsonMarshaller.unmarshal((JSONObject) element);
                                }
                                collection.add(element);
                            }
                            methods[i].invoke(result, collection);
                        }
                        catch (JSONException exception) {
                            if (Debug.isDebuggerConnected()) {
                                Log.e(TAG, methods[i].getName() + " " + exception.getLocalizedMessage());
                            }
                        }
                    }
                    else if (setter.type() == JSONType.UNKNOWN_KEY_ELEMENTS) {
                        /**
                         * Used for object that represent a Collection (List,
                         * ArrayList, Vector, etc ...) and that do not have a
                         * key that is predefined.
                         */
                        try {
                            Collection<Object> collection = null;
                            if (methods[i].getParameterTypes()[0] == List.class) {
                                collection = (Collection<Object>) setter.listClazz().newInstance();
                            }
                            else {
                                collection = (Collection<Object>) methods[i].getParameterTypes()[0].newInstance();
                            }

                            Iterator<?> iterator = jsonObject.keys();
                            while (iterator.hasNext()) {
                                String key = (String) iterator.next();
                                Object element = jsonObject.get(key);
                                if (setter.objectClazz() != Void.class) {
                                    SimpleJsonMarshaller simpleJsonMarshaller = new SimpleJsonMarshaller(setter.objectClazz());
                                    element = simpleJsonMarshaller.unmarshal((JSONObject) element);
                                    if (element instanceof UnknowMappingElement) {
                                        ((UnknowMappingElement) element).setId(key);
                                    }
                                }
                                collection.add(element);
                            }
                            methods[i].invoke(result, collection);
                        }
                        catch (JSONException exception) {
                            if (Debug.isDebuggerConnected()) {
                                Log.e(TAG, methods[i].getName() + " " + exception.getLocalizedMessage());
                            }
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            if (Debug.isDebuggerConnected()) {
                Log.e(TAG, e.getLocalizedMessage());
            }
        }
        return result;
    }
}
