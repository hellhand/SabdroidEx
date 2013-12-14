package com.sabdroidex.data.sickbeard;

import com.sabdroidex.utils.json.JSONElement;
import com.sabdroidex.utils.json.JSONSetter;
import com.sabdroidex.utils.json.impl.JSONType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@JSONElement
public class Season implements Serializable {
    
    /**
     * Data fields
     */
    private static final long serialVersionUID = -9012012873806466684L;
    private List<Episode> episodes;
    /**
     * Additional fields
     */
    private int showId;
    private int seasonNr;
    
    public List<Episode> getEpisodes() {
        if (episodes == null) {
            episodes = new ArrayList<Episode>();
        }
        return episodes;
    }

    @JSONSetter(name="data", type=JSONType.UNKNOWN_KEY_ELEMENTS, objectClazz=Episode.class)
    public void setEpisodes(List<Episode> episodes) {
        this.episodes = episodes;
    }

    public int getShowId() {
        return showId;
    }

    public void setShowId(int showId) {
        this.showId = showId;
    }

    public int getSeasonNr() {
        return seasonNr;
    }

    public void setSeasonNr(int seasonNr) {
        this.seasonNr = seasonNr;
    }
}
