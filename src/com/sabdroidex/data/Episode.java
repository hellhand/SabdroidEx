package com.sabdroidex.data;

import java.io.Serializable;

import com.sabdroidex.utils.json.JSONSetter;

public class Episode extends UnknowMappingElement implements Serializable, Comparable<Episode> {
    
    /**
     * 
     */
    private static final long serialVersionUID = -898360930222062589L;
    private Integer episode;
    private String airDate;
    private String name;
    private String quality;
    private String status;
    
    public Integer getEpisode() {
        return episode;
    }
    
    public void setEpisode(Integer episode) {
        this.episode = episode;
    }
    
    public String getAirDate() {
        return airDate;
    }
    
    @JSONSetter(name="airdate")
    public void setAirDate(String airDate) {
        this.airDate = airDate;
    }
    
    public String getName() {
        return name;
    }
    
    @JSONSetter(name="name")
    public void setName(String name) {
        this.name = name;
    }
    
    public String getQuality() {
        return quality;
    }
    
    @JSONSetter(name="quality")
    public void setQuality(String quality) {
        this.quality = quality;
    }
    
    public String getStatus() {
        return status;
    }
    
    @JSONSetter(name="status")
    public void setStatus(String status) {
        this.status = status;
    }
    
    @Override
    public int compareTo(Episode another) {
        return getId().compareTo(another.getId());
    }
}
