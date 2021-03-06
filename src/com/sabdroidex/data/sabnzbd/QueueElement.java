package com.sabdroidex.data.sabnzbd;

import com.sabdroidex.utils.json.JSONElement;
import com.sabdroidex.utils.json.JSONSetter;

import java.io.Serializable;

@JSONElement
public class QueueElement implements Serializable {
    
    /**
     * 
     */
    private static final long serialVersionUID = -6892836498990066699L;
    private String filename;
    private String mb;
    private String mbLeft;
    private String status;
    private String nzoId;
    private String timeLeft;
    private String priority;
    private String category;
    private String script;
    
    public String getFilename() {
        return filename;
    }
    
    @JSONSetter(name="filename")
    public void setFilename(String filename) {
        this.filename = filename;
    }
    
    public String getMb() {
        return mb;
    }
    
    @JSONSetter(name="mb")
    public void setMb(String mb) {
        this.mb = mb;
    }
    
    public String getMbLeft() {
        return mbLeft;
    }
    
    @JSONSetter(name="mbleft")
    public void setMbLeft(String mbLeft) {
        this.mbLeft = mbLeft;
    }
    
    public String getStatus() {
        return status;
    }
    
    @JSONSetter(name="status")
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getNzoId() {
        return nzoId;
    }
    
    @JSONSetter(name="nzo_id")
    public void setNzoId(String nzoId) {
        this.nzoId = nzoId;
    }
    
    public String getTimeLeft() {
        return timeLeft;
    }
    
    @JSONSetter(name="timeleft")
    public void setTimeLeft(String timeLeft) {
        this.timeLeft = timeLeft;
    }

    public String getPriority() {
        return priority;
    }

    @JSONSetter(name="priority")
    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getCategory() {
        return category;
    }

    @JSONSetter(name="cat")
    public void setCategory(String category) {
        this.category = category;
    }

    public String getScript() {
        return script;
    }

    @JSONSetter(name="script")
    public void setScript(String script) {
        this.script = script;
    }
}
