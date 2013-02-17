package com.sabdroidex.data;

import java.io.Serializable;
import java.util.List;

import com.sabdroidex.utils.json.JSONElement;
import com.sabdroidex.utils.json.JSONSetter;
import com.sabdroidex.utils.json.JSONType;

@JSONElement
public class DownloadLog implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 5152837848012613321L;
    
    private String name;
    private List<String> actions;
    
    public String getName() {
        return name;
    }
    
    @JSONSetter(name="name")
    public void setName(String name) {
        this.name = name;
    }
    
    public List<String> getActions() {
        return actions;
    }
    
    @JSONSetter(name="actions", type=JSONType.LIST)
    public void setActions(List<String> actions) {
        this.actions = actions;
    }
}
