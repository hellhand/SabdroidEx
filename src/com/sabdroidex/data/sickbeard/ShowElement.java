package com.sabdroidex.data.sickbeard;

import java.io.Serializable;
import java.util.List;

import com.sabdroidex.utils.json.JSONElement;
import com.sabdroidex.utils.json.JSONSetter;
import com.sabdroidex.utils.json.JSONType;

@JSONElement
public class ShowElement implements Serializable {
    
    /**
     * 
     */
    private static final long serialVersionUID = -1673835783383831837L;
    private String id;
    private List<Show> shows;
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public List<Show> getShows() {
        return shows;
    }
    
    @JSONSetter(type=JSONType.UNKNOWN_KEY_ELEMENTS)
    public void setShows(List<Show> shows) {
        this.shows = shows;
    }
}
