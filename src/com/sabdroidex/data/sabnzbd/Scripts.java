package com.sabdroidex.data.sabnzbd;

import com.sabdroidex.utils.json.JSONElement;
import com.sabdroidex.utils.json.JSONSetter;
import com.sabdroidex.utils.json.impl.JSONType;

import java.util.List;

/**
 * Created by Marc on 27/12/13.
 */
@JSONElement
public class Scripts {

    private List<String> scripts;

    public List<String> getScripts() {
        return scripts;
    }

    @JSONSetter(name = "scripts", type = JSONType.LIST)
    public void setScripts(List<String> scripts) {
        this.scripts = scripts;
    }
}
