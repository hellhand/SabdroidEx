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
        return R.string.comming_missed;
    }

    public List<FutureEpisode> getMissed() {
        return missed;
    }

    @JSONSetter(name = "missed", type = JSONType.LIST, listClazz = LinkedList.class, objectClazz = FutureEpisode.class)
    public void setMissed(List<FutureEpisode> missed) {
        this.missed = missed;
    }

    public boolean hasMissed() {
        if (missed == null || missed.size() == 0) {
            return false;
        }
        return true;
    }

    public int getTodayTitle() {
        return R.string.comming_today;
    }

    public List<FutureEpisode> getToday() {
        return today;
    }

    @JSONSetter(name = "today", type = JSONType.LIST, listClazz = LinkedList.class, objectClazz = FutureEpisode.class)
    public void setToday(List<FutureEpisode> today) {
        this.today = today;
    }

    public boolean hasToday() {
        if (today == null || today.size() == 0) {
            return false;
        }
        return true;
    }

    public int getSoonTitle() {
        return R.string.comming_soon;
    }

    public List<FutureEpisode> getSoon() {
        return soon;
    }

    @JSONSetter(name = "soon", type = JSONType.LIST, listClazz = LinkedList.class, objectClazz = FutureEpisode.class)
    public void setSoon(List<FutureEpisode> soon) {
        this.soon = soon;
    }

    public boolean hasSoon() {
        if (soon == null || soon.size() == 0) {
            return false;
        }
        return true;
    }

    public int getLaterTitle() {
        return R.string.comming_later;
    }

    public List<FutureEpisode> getLater() {
        return later;
    }

    @JSONSetter(name = "later", type = JSONType.LIST, listClazz = LinkedList.class, objectClazz = FutureEpisode.class)
    public void setLater(List<FutureEpisode> later) {
        this.later = later;
    }

    public boolean hasLater() {
        if (later == null || later.size() == 0) {
            return false;
        }
        return true;
    }
}
