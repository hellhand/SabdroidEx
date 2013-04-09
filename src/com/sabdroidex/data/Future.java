package com.sabdroidex.data;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import com.sabdroidex.utils.json.JSONElement;
import com.sabdroidex.utils.json.JSONSetter;
import com.sabdroidex.utils.json.JSONType;

@JSONElement
public class Future implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 8363254087580757089L;
    private List<FutureEpisode> futureEpisodes;
    
    public List<FutureEpisode> getFutureEpisodes() {
        return futureEpisodes;
    }
    
    @JSONSetter(name="data", type=JSONType.UNKNOWN_KEY_ELEMENTS, listClazz=LinkedList.class, objectClazz=FutureEpisode.class)
    public void setFutureEpisodes(List<FutureEpisode> futureEpisodes) {
        this.futureEpisodes = futureEpisodes;
    }
}
