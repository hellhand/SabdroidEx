package com.sabdroidex.data.sabnzbd;

import com.sabdroidex.utils.json.JSONElement;
import com.sabdroidex.utils.json.JSONSetter;
import com.sabdroidex.utils.json.impl.JSONType;

@JSONElement
public class SabnzbdConfig {
    
    private MiscConfig misc;

    public MiscConfig getMisc() {
        return misc;
    }

    @JSONSetter(name="misc", type=JSONType.JSON_OBJECT)
    public void setMisc(MiscConfig misc) {
        this.misc = misc;
    }
}
