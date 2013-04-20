package com.sabdroidex.data.sickbeard;

import java.util.LinkedList;
import java.util.List;

import com.sabdroidex.R;
import com.sabdroidex.adapters.ListSeparatorHolder;
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
    /**
     * Additional Fields
     */
    private LinkedList<Object> elements;
    
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
    
    /**
     * This method returns a count of all the periods elements including their header.
     * If the {@link LinkedList} is <code>null</code> is it then created.
     * @return
     */
    public int getCount() {
        if (elements == null) {
            mergePeriods();
        }
        return elements.size();
    }
    
    /**
     * creates a {@link LinkedList} containing all the elements from the different periods as well as a header for each.
     */
    private void mergePeriods() {
        elements = new LinkedList<Object>();
        if (hasMissed()) {
            elements.add(new ListSeparatorHolder(R.string.comming_missed));
            elements.addAll(missed);
        }
        if (hasToday()) {
            elements.add(new ListSeparatorHolder(R.string.comming_today));
            elements.addAll(today);
        }
        if (hasSoon()) {
            elements.add(new ListSeparatorHolder(R.string.comming_soon));
            elements.addAll(soon);
        }
        if (hasLater()) {
            elements.add(new ListSeparatorHolder(R.string.comming_later));
            elements.addAll(later);
        }
    }
    
    /**
     * This method returns a {@link LinkedList} containing all the periods elements with their header.
     * If the {@link LinkedList} is <code>null</code> is it then created.
     * @return
     */
    public LinkedList<Object> getElements() {
        if (elements == null) {
            mergePeriods();
        }
        return elements;
    }
}
