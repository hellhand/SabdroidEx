package com.sabdroidex.data.sickbeard;

import com.sabdroidex.utils.json.JSONElement;
import com.sabdroidex.utils.json.JSONSetter;
import com.sabdroidex.utils.json.JSONType;

import java.io.Serializable;

@JSONElement
public class Future implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 8363254087580757089L;
    private FuturePeriod futurePeriod;
    
    public FuturePeriod getFuturePeriod() {
        return futurePeriod;
    }
    
    @JSONSetter(name="data", type=JSONType.JSON_OBJECT, objectClazz=FuturePeriod.class)
    public void setFutureEpisodes(FuturePeriod futurePeriod) {
        this.futurePeriod = futurePeriod;
    }
}
