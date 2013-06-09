package com.sabdroidex.data.sickbeard;

import java.util.LinkedList;
import java.util.List;

import com.sabdroidex.R;
import com.sabdroidex.data.JSONBased;
import com.sabdroidex.data.UnknowMappingElement;
import com.sabdroidex.utils.json.JSONSetter;
import com.sabdroidex.utils.json.JSONType;

public class FuturePeriod extends UnknowMappingElement implements JSONBased {

    /**
     * Data Fields
     */
    private static final long serialVersionUID = -319433971686256370L;
    private List<FutureEpisode> missed;
    private List<FutureEpisode> today;
    private List<FutureEpisode> soon;
    private List<FutureEpisode> later;

    public int getMissedTitle() {
        return R.string.coming_missed;
    }

    public List<FutureEpisode> getMissed() {
        return missed;
    }

    @JSONSetter(name = "missed", type = JSONType.LIST, listClazz = LinkedList.class, objectClazz = FutureEpisode.class)
    public void setMissed(List<FutureEpisode> missed) {
        this.missed = missed;
    }

    public int getTodayTitle() {
        return R.string.coming_today;
    }

    public List<FutureEpisode> getToday() {
        return today;
    }

    @JSONSetter(name = "today", type = JSONType.LIST, listClazz = LinkedList.class, objectClazz = FutureEpisode.class)
    public void setToday(List<FutureEpisode> today) {
        this.today = today;
    }

    public int getSoonTitle() {
        return R.string.coming_soon;
    }

    public List<FutureEpisode> getSoon() {
        return soon;
    }

    @JSONSetter(name = "soon", type = JSONType.LIST, listClazz = LinkedList.class, objectClazz = FutureEpisode.class)
    public void setSoon(List<FutureEpisode> soon) {
        this.soon = soon;
    }

    public int getLaterTitle() {
        return R.string.coming_later;
    }

    public List<FutureEpisode> getLater() {
        return later;
    }

    @JSONSetter(name = "later", type = JSONType.LIST, listClazz = LinkedList.class, objectClazz = FutureEpisode.class)
    public void setLater(List<FutureEpisode> later) {
        this.later = later;
    }
}
