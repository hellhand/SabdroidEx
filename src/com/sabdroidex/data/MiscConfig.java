package com.sabdroidex.data;

import com.sabdroidex.utils.json.JSONSetter;


public class MiscConfig {
    
    private Integer banwidthLimit;
    private String cacheDir;
    private String cacheLimit;
    private String dirscanDir;
    private Integer dirscanSpeed;
    private String downloadDir;
    private String completeDir;
    
    public Integer getBanwidthLimit() {
        return banwidthLimit;
    }
    
    @JSONSetter(name="bandwidth_limit")
    public void setBanwidthLimit(Integer banwidthLimit) {
        this.banwidthLimit = banwidthLimit;
    }
    
    public String getCacheDir() {
        return cacheDir;
    }
    
    @JSONSetter(name="cache_dir")
    public void setCacheDir(String cacheDir) {
        this.cacheDir = cacheDir;
    }
    
    public String getCacheLimit() {
        return cacheLimit;
    }
    
    @JSONSetter(name="cache_limit")
    public void setCacheLimit(String cacheLimit) {
        this.cacheLimit = cacheLimit;
    }
    
    public String getDirscanDir() {
        return dirscanDir;
    }
    
    @JSONSetter(name="dirscan_dir")
    public void setDirscanDir(String dirscanDir) {
        this.dirscanDir = dirscanDir;
    }
    
    public Integer getDirscanSpeed() {
        return dirscanSpeed;
    }
    
    @JSONSetter(name="dirscan_speed")
    public void setDirscanSpeed(Integer dirscanSpeed) {
        this.dirscanSpeed = dirscanSpeed;
    }
    
    public String getDownloadDir() {
        return downloadDir;
    }
    
    @JSONSetter(name="download_dir")
    public void setDownloadDir(String downloadDir) {
        this.downloadDir = downloadDir;
    }
    
    public String getCompleteDir() {
        return completeDir;
    }
    
    @JSONSetter(name="complete_dir")
    public void setCompleteDir(String completeDir) {
        this.completeDir = completeDir;
    }
    
}
