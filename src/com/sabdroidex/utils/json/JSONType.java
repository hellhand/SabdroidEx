package com.sabdroidex.utils.json;

public enum JSONType {

    /**
     * Simple is used for every simple object that is understood by the JSON framework
     * List is used when the List interface is needed (An ArrayList is generated is the parameter of the JSONSetter method is a List)
     * JSONObject is used when the unmarshall need to be processed for that item too (complex Object)
     */
    SIMPLE, LIST, JSONOBJECT
}
