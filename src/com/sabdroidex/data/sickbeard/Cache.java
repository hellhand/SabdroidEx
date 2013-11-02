package com.sabdroidex.data.sickbeard;

import com.sabdroidex.utils.json.JSONElement;
import com.sabdroidex.utils.json.JSONSetter;

import java.io.Serializable;

@JSONElement
public class Cache implements Serializable {
    
    /**
     * 
     */
    private static final long serialVersionUID = 2664197781437907595L;
    private Integer banner;
    private Integer poster;
    
    public Integer getBanner() {
        return banner;
    }
    
    @JSONSetter(name = "banner")
    public void setBanner(Integer banner) {
        this.banner = banner;
    }
    
    public Integer getPoster() {
        return poster;
    }
    
    @JSONSetter(name = "poster")
    public void setPoster(Integer poster) {
        this.poster = poster;
    }
}
