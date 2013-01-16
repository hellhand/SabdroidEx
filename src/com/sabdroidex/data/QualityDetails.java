package com.sabdroidex.data;

import java.util.List;

import com.sabdroidex.utils.json.JSONSetter;
import com.sabdroidex.utils.json.JSONType;


public class QualityDetails {
    
    private List<String> archive;
    private List<String> initial;
    
    public List<String> getArchive() {
        return archive;
    }
    
    @JSONSetter(name = "archive", type=JSONType.LIST)
    public void setArchive(List<String> archive) {
        this.archive = archive;
    }
    
    public List<String> getInitial() {
        return initial;
    }
    
    @JSONSetter(name = "initial", type=JSONType.LIST)
    public void setInitial(List<String> initial) {
        this.initial = initial;
    }
}
