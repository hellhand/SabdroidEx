package com.sabdroidex.data.sickbeard;

import com.sabdroidex.utils.json.JSONElement;
import com.sabdroidex.utils.json.JSONSetter;

@JSONElement
public class ShowSearchResult {
    
    private String firstAired;
    private String name;
    private Integer tvdbid;
    
    public String getFirstAired() {
        return firstAired;
    }
    
    @JSONSetter(name="first_aired")
    public void setFirstAired(String firstAired) {
        this.firstAired = firstAired;
    }
    
    public String getName() {
        return name;
    }
    
    @JSONSetter(name="name")
    public void setName(String name) {
        this.name = name;
    }
    
    public Integer getTvdbid() {
        return tvdbid;
    }
    
    @JSONSetter(name="tvdbid")
    public void setTvdbid(Integer tvdbid) {
        this.tvdbid = tvdbid;
    }
}
