package com.sabdroidex.data.sabnzbd;

import java.util.ArrayList;
import java.util.List;

import com.sabdroidex.utils.json.JSONElement;
import com.sabdroidex.utils.json.JSONSetter;
import com.sabdroidex.utils.json.JSONType;

@JSONElement
public class Queue extends SabnzbdStatus {
        
    /**
     * 
     */
    private static final long serialVersionUID = 4456160108318268111L;
    private String totalSize;
    private String monthSize;
    private String weekSize;
    private List<QueueElement> queueElements;
    
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
    
    
    public List<QueueElement> getQueueElements() {
        if (queueElements == null) {
            queueElements = new ArrayList<QueueElement>();
        }
        return queueElements;
    }
    
    @JSONSetter(name = "slots", type = JSONType.LIST, objectClazz = QueueElement.class)
    public void setQueueElements(List<QueueElement> queueElements) {
        this.queueElements = queueElements;
    }
}
