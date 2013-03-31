package com.sabdroidex.test;

import java.io.IOException;
import java.io.InputStream;

import org.json.JSONException;
import org.json.JSONObject;

import android.test.AndroidTestCase;

import com.sabdroidex.data.Show;
import com.sabdroidex.data.ShowList;
import com.sabdroidex.utils.json.SimpleJsonMarshaller;


public class SimpleJsonMarshallerTest extends AndroidTestCase  {
    
    public void testMarshaller() throws JSONException, IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        
        StringBuffer stringBuffer = new StringBuffer();
        
        InputStream stream = getClass().getResourceAsStream("show.json");
        int c;
        while ((c = stream.read()) != -1) {
            stringBuffer.append((char) c);
        }
        
        JSONObject jsonObject = new JSONObject(stringBuffer.toString());
        jsonObject = jsonObject.getJSONObject("data");
        
        SimpleJsonMarshaller simpleJsonMarshaller = new SimpleJsonMarshaller(Show.class);
        Show show = (Show) simpleJsonMarshaller.unmarshal(jsonObject);
        assertNotNull(show);
        System.out.println(show.getShowName());
    }
    
    public void testMarshallerExtended() throws JSONException, IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        StringBuffer stringBuffer = new StringBuffer();
        
        InputStream stream = getClass().getResourceAsStream("showlist.json");
        int c;
        while ((c = stream.read()) != -1) {
            stringBuffer.append((char) c);
        }
        
        JSONObject jsonObject = new JSONObject(stringBuffer.toString());
        jsonObject = jsonObject.getJSONObject("data");
        
        SimpleJsonMarshaller simpleJsonMarshaller = new SimpleJsonMarshaller(ShowList.class);
        ShowList shows = (ShowList) simpleJsonMarshaller.unmarshal(jsonObject);
        assertNotNull(shows);
        assertTrue(shows.getShowElements().size() > 0);
        for (Show show : shows.getShowElements()) {
            System.out.println(show.getShowName());
        }
    }
}