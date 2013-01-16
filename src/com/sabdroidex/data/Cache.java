package com.sabdroidex.data;

import com.sabdroidex.utils.json.JSONSetter;


public class Cache {
    
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
