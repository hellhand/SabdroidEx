package com.sabdroidex.adapters;

import java.io.Serializable;

/**
 * This class is used to define a separator in a ListView.
 * @author Marc
 *
 */
public class ListSeparatorHolder implements Serializable {
    
    /**
     * 
     */
    private static final long serialVersionUID = 2245364834776948876L;
    /**
     * This field defines the string that will be displayed in the separator.
     */
    private int resId;

    /**
     * 
     * @param resId The resource id is a String resource.
     */
    public ListSeparatorHolder(int resId) {
        this.resId = resId;
    }
    
    public int getSeparator() {
        return resId;
    }
}
