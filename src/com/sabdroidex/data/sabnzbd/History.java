package com.sabdroidex.data.sabnzbd;

import com.sabdroidex.data.JSONBased;
import com.sabdroidex.utils.json.JSONSetter;
import com.sabdroidex.utils.json.JSONType;

import java.util.ArrayList;
import java.util.List;


public class History extends SabnzbdStatus implements JSONBased {

    /**
     * Default fields
     */
    private static final long serialVersionUID = -2512044996381104486L;
    private String totalSize;
    private String monthSize;
    private String weekSize;
    private List<HistoryElement> historyElements;
    
    public String getTotalSize() {
        return totalSize;
    }
    
    public void setTotalSize(String totalSize) {
        this.totalSize = totalSize;
    }
    
    public String getMonthSize() {
        return monthSize;
    }
    
    public void setMonthSize(String monthSize) {
        this.monthSize = monthSize;
    }
    
    public String getWeekSize() {
        return weekSize;
    }
    
    public void setWeekSize(String weekSize) {
        this.weekSize = weekSize;
    }
    
    public List<HistoryElement> getHistoryElements() {
        if (historyElements == null) {
            historyElements = new ArrayList<HistoryElement>();
        }
        return historyElements;
    }

    @JSONSetter(name = "slots", type = JSONType.LIST, objectClazz = HistoryElement.class)
    public void setHistoryElements(List<HistoryElement> historyElements) {
        this.historyElements = historyElements;
    }
}
