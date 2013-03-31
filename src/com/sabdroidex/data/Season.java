package com.sabdroidex.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.sabdroidex.utils.json.JSONElement;
import com.sabdroidex.utils.json.JSONSetter;
import com.sabdroidex.utils.json.JSONType;

@JSONElement
public class Season implements Serializable {
    
    /**
     * 
     */
    private static final long serialVersionUID = -9012012873806466684L;
    private List<Episode> episodes;
    
    public List<Episode> getEpisodes() {
        if (episodes == null) {
            episodes = new ArrayList<Episode>();
        }
        return episodes;
    }

    @JSONSetter(name="data", type=JSONType.UNKNOWN_KEY_ELEMENTS, listClazz=Episode.class)
    public void setEpisodes(List<Episode> episodes) {
        this.episodes = episodes;
    }
}
