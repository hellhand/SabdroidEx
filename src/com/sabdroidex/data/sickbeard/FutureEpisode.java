package com.sabdroidex.data.sickbeard;

import com.sabdroidex.data.UnknowMappingElement;
import com.sabdroidex.utils.json.JSONElement;
import com.sabdroidex.utils.json.JSONSetter;

@JSONElement
public class FutureEpisode extends UnknowMappingElement {
    
    /**
     * 
     */
    private static final long serialVersionUID = -3362846744908179433L;
    private String airDate;
    private String airs;
    private String epName;
    private String epPlot;
    private Integer episode;
    private String network;
    private Integer paused;
    private String quality;
    private Integer season;
    private String showName;
    private String showStatus;
    private Integer tvdbId;
    private Integer weekday;
    
    public String getAirDate() {
        return airDate;
    }
    
    @JSONSetter(name="airdate")
    public void setAirDate(String airDate) {
        this.airDate = airDate;
    }
    
    public String getAirs() {
        return airs;
    }
    
    @JSONSetter(name="airs")
    public void setAirs(String airs) {
        this.airs = airs;
    }
    
    public String getEpName() {
        return epName;
    }
    
    @JSONSetter(name="ep_name")
    public void setEpName(String epName) {
        this.epName = epName;
    }
    
    public String getEpPlot() {
        return epPlot;
    }
    
    public void setEpPlot(String epPlot) {
        this.epPlot = epPlot;
    }
    
    public Integer getEpisode() {
        return episode;
    }
    
    @JSONSetter(name="episode")
    public void setEpisode(Integer episode) {
        this.episode = episode;
    }
    
    public String getNetwork() {
        return network;
    }
    
    @JSONSetter(name="network")
    public void setNetwork(String network) {
        this.network = network;
    }
    
    public Integer getPaused() {
        return paused;
    }
    
    public void setPaused(Integer paused) {
        this.paused = paused;
    }
    
    public String getQuality() {
        return quality;
    }
    
    @JSONSetter(name="quality")
    public void setQuality(String quality) {
        this.quality = quality;
    }
    
    public Integer getSeason() {
        return season;
    }
    
    @JSONSetter(name="season")
    public void setSeason(Integer season) {
        this.season = season;
    }
    
    public String getShowName() {
        return showName;
    }
    
    @JSONSetter(name="show_name")
    public void setShowName(String showName) {
        this.showName = showName;
    }
    
    public String getShowStatus() {
        return showStatus;
    }
    
    public void setShowStatus(String showStatus) {
        this.showStatus = showStatus;
    }
    
    public Integer getTvdbId() {
        return tvdbId;
    }
    
    @JSONSetter(name="tvdbid")
    public void setTvdbId(Integer tvdbId) {
        this.tvdbId = tvdbId;
    }
    
    public Integer getWeekday() {
        return weekday;
    }
    
    public void setWeekday(Integer weekday) {
        this.weekday = weekday;
    }
}
