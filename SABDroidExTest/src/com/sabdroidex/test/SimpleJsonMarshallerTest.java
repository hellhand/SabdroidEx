package com.sabdroidex.test;

import java.io.IOException;
import java.io.InputStream;

import org.json.JSONException;
import org.json.JSONObject;

import android.test.AndroidTestCase;

import com.sabdroidex.data.FuturePeriod;
import com.sabdroidex.data.SabnzbdConfig;
import com.sabdroidex.data.Season;
import com.sabdroidex.data.Show;
import com.sabdroidex.data.ShowList;
import com.sabdroidex.data.ShowSearch;
import com.sabdroidex.utils.json.SimpleJsonMarshaller;


public class SimpleJsonMarshallerTest extends AndroidTestCase  {
    
    public void testMarshaller_Show() throws JSONException, IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        
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
    
    public void testMarshaller_ShowList() throws JSONException, IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
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
    
    public void testMarshaller_SabnzbdConfig() throws JSONException, IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        StringBuffer stringBuffer = new StringBuffer();
        
        InputStream stream = getClass().getResourceAsStream("config.json");
        int c;
        while ((c = stream.read()) != -1) {
            stringBuffer.append((char) c);
        }
        
        JSONObject jsonObject = new JSONObject(stringBuffer.toString());
        jsonObject = jsonObject.getJSONObject("config");
        
        SimpleJsonMarshaller simpleJsonMarshaller = new SimpleJsonMarshaller(SabnzbdConfig.class);
        SabnzbdConfig config = (SabnzbdConfig) simpleJsonMarshaller.unmarshal(jsonObject);
        assertNotNull(config);
        assertNotNull(config.getMisc());
    }
    
    public void testMarshaller_ShowSearch() throws IOException, JSONException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        StringBuffer stringBuffer = new StringBuffer();
        
        InputStream stream = getClass().getResourceAsStream("showsearch.json");
        int c;
        while ((c = stream.read()) != -1) {
            stringBuffer.append((char) c);
        }
        
        JSONObject jsonObject = new JSONObject(stringBuffer.toString());
        jsonObject = jsonObject.getJSONObject("data");
        
        SimpleJsonMarshaller simpleJsonMarshaller = new SimpleJsonMarshaller(ShowSearch.class);
        ShowSearch showSearch = (ShowSearch) simpleJsonMarshaller.unmarshal(jsonObject);
        assertNotNull(showSearch);
        assertTrue(showSearch.getResults().size() > 0);
    }
    
    public void testMarshaller_ShowSeason() throws IOException, JSONException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        StringBuffer stringBuffer = new StringBuffer();
        
        InputStream stream = getClass().getResourceAsStream("showseason.json");
        int c;
        while ((c = stream.read()) != -1) {
            stringBuffer.append((char) c);
        }
        
        JSONObject jsonObject = new JSONObject(stringBuffer.toString());
        jsonObject = jsonObject.getJSONObject("data");
        
        SimpleJsonMarshaller simpleJsonMarshaller = new SimpleJsonMarshaller(Season.class);
        Season season = (Season) simpleJsonMarshaller.unmarshal(jsonObject);
        assertNotNull(season);
        assertTrue(season.getEpisodes().size() > 0);
    }
    
    public void testMarshaller_Future() throws IOException, JSONException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        StringBuffer stringBuffer = new StringBuffer();
        
        InputStream stream = getClass().getResourceAsStream("future.json");
        int c;
        while ((c = stream.read()) != -1) {
            stringBuffer.append((char) c);
        }
        
        JSONObject jsonObject = new JSONObject(stringBuffer.toString());
        jsonObject = jsonObject.getJSONObject("data");
        
        SimpleJsonMarshaller simpleJsonMarshaller = new SimpleJsonMarshaller(FuturePeriod.class);
        FuturePeriod futurePeriod = (FuturePeriod) simpleJsonMarshaller.unmarshal(jsonObject);
        assertNotNull(futurePeriod);
        assertTrue(futurePeriod.getMissed().size() > 0);
        assertTrue(futurePeriod.getToday().size() > 0);
    }
}
