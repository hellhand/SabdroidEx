package com.sabdroidex.data.sabnzbd;

import java.io.Serializable;

import com.sabdroidex.utils.json.JSONElement;
import com.sabdroidex.utils.json.JSONSetter;

@JSONElement
public class SabnzbdStatus implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 8468302905638017297L;
    
    private String cacheLimit;
    private Boolean paused;
    private String newRelURL;
    private String restartReq;
    private String helpURI;
    private String uptime;    
    private String version;
    private String diskSpaceTotal2;
    private String colorScheme;
    private Boolean darwin;
    private Boolean nt;
    private String status;
    private String lastWarning;
    private String haveWarnings;
    private String cacheArt;
    private String finishAction;
    private Integer noofSlots;
    private String cacheSize;
    private String newRelease;
    private String pauseInt;
    private String mbLeft;
    private String diskSpace2;
    private String diskSpace1;
    private String diskSpaceTotal1;
    private String timeLeft;    
    private String mb;
    private String eta;
    private String nzbQuota;
    private String loadAvg;
    private String kbPerSec;
    private String speedLimit;
    private String webDir;
    
    public String getCacheLimit() {
        return cacheLimit;
    }
    
    public void setCacheLimit(String cacheLimit) {
        this.cacheLimit = cacheLimit;
    }
    
    public Boolean getPaused() {
        return paused;
    }
    
    @JSONSetter(name="paused")
    public void setPaused(Boolean paused) {
        this.paused = paused;
    }
    
    public String getNewRelURL() {
        return newRelURL;
    }
    
    public void setNewRelURL(String newRelURL) {
        this.newRelURL = newRelURL;
    }
    
    public String getRestartReq() {
        return restartReq;
    }
    
    public void setRestartReq(String restartReq) {
        this.restartReq = restartReq;
    }
    
    public String getHelpURI() {
        return helpURI;
    }
    
    public void setHelpURI(String helpURI) {
        this.helpURI = helpURI;
    }
    
    public String getUptime() {
        return uptime;
    }
    
    public void setUptime(String uptime) {
        this.uptime = uptime;
    }
    
    public String getVersion() {
        return version;
    }
    
    public void setVersion(String version) {
        this.version = version;
    }
    
    public String getDiskSpaceTotal2() {
        return diskSpaceTotal2;
    }
    
    public void setDiskSpaceTotal2(String diskSpaceTotal2) {
        this.diskSpaceTotal2 = diskSpaceTotal2;
    }
    
    public String getColorScheme() {
        return colorScheme;
    }
    
    public void setColorScheme(String colorScheme) {
        this.colorScheme = colorScheme;
    }
    
    public Boolean getDarwin() {
        return darwin;
    }
    
    public void setDarwin(Boolean darwin) {
        this.darwin = darwin;
    }
    
    public Boolean getNt() {
        return nt;
    }
    
    public void setNt(Boolean nt) {
        this.nt = nt;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getLastWarning() {
        return lastWarning;
    }
    
    public void setLastWarning(String lastWarning) {
        this.lastWarning = lastWarning;
    }
    
    public String getHaveWarnings() {
        return haveWarnings;
    }
    
    public void setHaveWarnings(String haveWarnings) {
        this.haveWarnings = haveWarnings;
    }
    
    public String getCacheArt() {
        return cacheArt;
    }
    
    public void setCacheArt(String cacheArt) {
        this.cacheArt = cacheArt;
    }
    
    public String getFinishAction() {
        return finishAction;
    }
    
    public void setFinishAction(String finishAction) {
        this.finishAction = finishAction;
    }
    
    public Integer getNoofSlots() {
        return noofSlots;
    }
    
    public void setNoofSlots(Integer noofSlots) {
        this.noofSlots = noofSlots;
    }
    
    public String getCacheSize() {
        return cacheSize;
    }
    
    public void setCacheSize(String cacheSize) {
        this.cacheSize = cacheSize;
    }
    
    public String getNewRelease() {
        return newRelease;
    }
    
    public void setNewRelease(String newRelease) {
        this.newRelease = newRelease;
    }
    
    public String getPauseInt() {
        return pauseInt;
    }
    
    public void setPauseInt(String pauseInt) {
        this.pauseInt = pauseInt;
    }
    
    public String getMbLeft() {
        return mbLeft;
    }
    
    @JSONSetter(name="mbleft")
    public void setMbLeft(String mbLeft) {
        this.mbLeft = mbLeft;
    }
    
    public String getDiskSpace2() {
        return diskSpace2;
    }
    
    @JSONSetter(name="diskspace2")
    public void setDiskSpace2(String diskSpace2) {
        this.diskSpace2 = diskSpace2;
    }
    
    public String getDiskSpace1() {
        return diskSpace1;
    }
    
    public void setDiskSpace1(String diskSpace1) {
        this.diskSpace1 = diskSpace1;
    }
    
    public String getDiskSpaceTotal1() {
        return diskSpaceTotal1;
    }
    
    public void setDiskSpaceTotal1(String diskSpaceTotal1) {
        this.diskSpaceTotal1 = diskSpaceTotal1;
    }
    
    public String getTimeLeft() {
        return timeLeft;
    }
    
    public void setTimeLeft(String timeLeft) {
        this.timeLeft = timeLeft;
    }
    
    public String getMb() {
        return mb;
    }
    
    @JSONSetter(name="mb")
    public void setMb(String mb) {
        this.mb = mb;
    }
    
    public String getEta() {
        return eta;
    }
    
    public void setEta(String eta) {
        this.eta = eta;
    }
    
    public String getNzbQuota() {
        return nzbQuota;
    }
    
    public void setNzbQuota(String nzbQuota) {
        this.nzbQuota = nzbQuota;
    }
    
    public String getLoadAvg() {
        return loadAvg;
    }
    
    public void setLoadAvg(String loadAvg) {
        this.loadAvg = loadAvg;
    }
    
    public String getKbPerSec() {
        return kbPerSec;
    }
    
    @JSONSetter(name="kbpersec")
    public void setKbPerSec(String kbPerSec) {
        this.kbPerSec = kbPerSec;
    }
    
    public String getSpeedLimit() {
        return speedLimit;
    }
    
    public void setSpeedLimit(String speedLimit) {
        this.speedLimit = speedLimit;
    }
    
    public String getWebDir() {
        return webDir;
    }
    
    public void setWebDir(String webDir) {
        this.webDir = webDir;
    }
}
