package com.sabdroidex.data.sickbeard;

import java.util.List;

import com.sabdroidex.utils.json.JSONElement;
import com.sabdroidex.utils.json.JSONSetter;
import com.sabdroidex.utils.json.JSONType;

@JSONElement
public class ShowSearch {
    
    private Integer langId;
    private List<ShowSearchResult> results;
    
    public Integer getLangId() {
        return langId;
    }
    
    @JSONSetter(name="langid")
    public void setLangId(Integer langId) {
        this.langId = langId;
    }
    
    public List<ShowSearchResult> getResults() {
        return results;
    }
    
    @JSONSetter(name="results", type=JSONType.LIST, objectClazz=ShowSearchResult.class)
    public void setResults(List<ShowSearchResult> results) {
        this.results = results;
    }
    
}
