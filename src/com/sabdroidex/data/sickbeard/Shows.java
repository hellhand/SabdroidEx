package com.sabdroidex.data.sickbeard;

import com.sabdroidex.data.JSONBased;
import com.sabdroidex.utils.json.JSONElement;
import com.sabdroidex.utils.json.JSONSetter;
import com.sabdroidex.utils.json.JSONType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@JSONElement
public class Shows implements JSONBased, Serializable {
    
    /**
     * 
     */
    private static final long serialVersionUID = -214015312686586531L;
    private List<Show> showElements;
    
    public List<Show> getShowElements() {
        if (showElements == null) {
            showElements = new ArrayList<Show>();
        }
        return showElements;
    }

    @JSONSetter(type=JSONType.UNKNOWN_KEY_ELEMENTS, objectClazz=Show.class)
    public void setShowElements(List<Show> showElements) {
        this.showElements = showElements;
    }
}
