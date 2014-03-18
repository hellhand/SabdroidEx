package com.sabdroidex.utils.json.impl;

/**
 * Created by Marc on 17/03/14.
 */
public class JSONContext {

    private String _elementName;
    private Object _elementValue;

    public String getElementName() {
        return _elementName;
    }

    public void setElementName(String _elementName) {
        this._elementName = _elementName;
    }

    public Object getElementValue() {
        return _elementValue;
    }

    public void setElementValue(Object _elementValue) {
        this._elementValue = _elementValue;
    }

    public void clear() {
        this._elementName = null;
        this._elementValue = null;
    }

    public void clearValue() {
        this._elementValue = null;
    }
}
