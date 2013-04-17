package com.sabdroidex.data.sabnzbd;

import java.io.Serializable;

import com.sabdroidex.utils.json.JSONElement;
import com.sabdroidex.utils.json.JSONSetter;

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
    
}
