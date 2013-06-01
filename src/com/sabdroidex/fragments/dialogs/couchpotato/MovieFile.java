package com.sabdroidex.fragments.dialogs.couchpotato;

import java.io.Serializable;

import com.sabdroidex.utils.json.JSONElement;
import com.sabdroidex.utils.json.JSONSetter;

@JSONElement
public class MovieFile implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -4271459719791113137L;

    private Boolean available;
    private String path;
    private Integer part;
    private Integer id;
    private Integer typeId;

    public Boolean getAvailable() {
        return available;
    }
    
    @JSONSetter(name = "available")
    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public String getPath() {
        return path;
    }

    @JSONSetter(name = "path")
    public void setPath(String path) {
        this.path = path;
    }

    public Integer getPart() {
        return part;
    }

    @JSONSetter(name = "part")
    public void setPart(Integer part) {
        this.part = part;
    }

    public Integer getId() {
        return id;
    }

    @JSONSetter(name = "id")
    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTypeId() {
        return typeId;
    }

    @JSONSetter(name = "type_id")
    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }
}
