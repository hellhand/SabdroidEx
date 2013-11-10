package com.sabdroidex.data.sickbeard;

import com.sabdroidex.data.UnknowMappingElement;
import com.sabdroidex.utils.json.JSONSetter;
import com.sabdroidex.utils.json.JSONType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Show extends UnknowMappingElement implements Serializable, Comparable<Show> {
    
    /**
     * 
     */
    private static final long serialVersionUID = 4393785794653519806L;
    private Integer airByDate;
    private String airs;
    private Cache cache;
    private Integer flattenFolders;
    private List<String> genre;
    private String language;
    private String location;
    private String network;
    private String nextEpAirdate;
    private Integer paused;
    private String quality;
    private QualityDetails qualityDetails;
    private List<Integer> seasonList;
    private String showName;
    private String status;
    private Integer tvdvId;
    private Integer tvrageId;
    private String tvrageName;
    
    public int getAirByDate() {
        return airByDate;
    }
    
    @JSONSetter(name = "air_by_date")
    public void setAirByDate(Integer airByDate) {
        this.airByDate = airByDate;
    }
    
    public String getAirs() {
        return airs;
    }
    
    @JSONSetter(name = "airs")
    public void setAirs(String airs) {
        this.airs = airs;
    }
    
    public Cache getCache() {
        return cache;
    }
    
    @JSONSetter(name = "cache", type=JSONType.JSON_OBJECT)
    public void setCache(Cache cache) {
        this.cache = cache;
    }
    
    public int getFlattenFolders() {
        return flattenFolders;
    }
    
    @JSONSetter(name = "flatten_folders")
    public void setFlattenFolders(Integer flattenFolders) {
        this.flattenFolders = flattenFolders;
    }
    
    public List<String> getGenre() {
        return genre;
    }
    
    @JSONSetter(name = "genre", type=JSONType.LIST)
    public void setGenre(List<String> genre) {
        this.genre = genre;
    }
    
    public String getLanguage() {
        return language;
    }
    
    @JSONSetter(name = "language")
    public void setLanguage(String language) {
        this.language = language;
    }
    
    public String getLocation() {
        return location;
    }
    
    @JSONSetter(name = "location")
    public void setLocation(String location) {
        this.location = location;
    }
    
    public String getNetwork() {
        return network;
    }
    
    @JSONSetter(name = "network")
    public void setNetwork(String network) {
        this.network = network;
    }
    
    public String getNextEpAirdate() {
        return nextEpAirdate;
    }
    
    @JSONSetter(name = "next_ep_airdate")
    public void setNextEpAirdate(String nextEpAirdate) {
        this.nextEpAirdate = nextEpAirdate;
    }
    
    public Integer getPaused() {
        return paused;
    }
    
    @JSONSetter(name = "paused")
    public void setPaused(Integer paused) {
        this.paused = paused;
    }
    
    public String getQuality() {
        return quality;
    }
    
    @JSONSetter(name = "quality")
    public void setQuality(String quality) {
        this.quality = quality;
    }
    
    public QualityDetails getQualityDetails() {
        return qualityDetails;
    }
    
    @JSONSetter(name = "quality_details", type=JSONType.JSON_OBJECT)
    public void setQualityDetails(QualityDetails qualityDetails) {
        this.qualityDetails = qualityDetails;
    }
    
    public List<Integer> getSeasonList() {
        if (seasonList == null) {
            seasonList = new ArrayList<Integer>();
        }
        return seasonList;
    }
    
    @JSONSetter(name = "season_list", type=JSONType.LIST)
    public void setSeasonList(List<Integer> seasonList) {
        this.seasonList = seasonList;
    }
    
    public String getShowName() {
        return showName;
    }
    
    @JSONSetter(name = "show_name")
    public void setShowName(String showName) {
        this.showName = showName;
    }
    
    public String getStatus() {
        return status;
    }
    
    @JSONSetter(name = "status")
    public void setStatus(String status) {
        this.status = status;
    }
    
    public Integer getTvdbId() {
        return tvdvId;
    }
    
    @JSONSetter(name = "tvdb_id")
    public void setTvdbId(Integer tvdbId) {
        this.tvdvId = tvdbId;
    }
    
    public Integer getTvrageId() {
        return tvrageId;
    }
    
    @JSONSetter(name = "tvrage_id")
    public void setTvrageId(Integer tvrageId) {
        this.tvrageId = tvrageId;
    }
    
    public String getTvrageName() {
        return tvrageName;
    }
    
    @JSONSetter(name = "tvrage_name")
    public void setTvrageName(String tvrageName) {
        this.tvrageName = tvrageName;
    }
    
    @Override
    public void setId(String id) {
        setTvdbId(Integer.valueOf(id));
        super.setId(id);
    }

    @Override
    public int compareTo(Show another) {
        return getShowName().compareTo(another.getShowName());
    }
}
