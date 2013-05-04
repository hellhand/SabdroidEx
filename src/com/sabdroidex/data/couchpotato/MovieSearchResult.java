package com.sabdroidex.data.couchpotato;

import com.sabdroidex.utils.json.JSONElement;
import com.sabdroidex.utils.json.JSONSetter;

@JSONElement
public class MovieSearchResult {
    
    private String originalTitle;
    private String imdb;
    
    public String getOriginalTitle() {
        return originalTitle;
    }
    
    @JSONSetter(name="original_title")
    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }
    
    public String getImdb() {
        return imdb;
    }
    
    @JSONSetter(name="imdb")
    public void setImdb(String imdb) {
        this.imdb = imdb;
    }

}
