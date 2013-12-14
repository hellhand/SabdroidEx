package com.sabdroidex.test;

import java.io.IOException;
import java.io.InputStream;

import org.json.JSONException;
import org.json.JSONObject;

import android.test.AndroidTestCase;

import com.sabdroidex.data.couchpotato.MovieList;
import com.sabdroidex.data.sabnzbd.SabnzbdConfig;
import com.sabdroidex.data.sickbeard.FuturePeriod;
import com.sabdroidex.data.sickbeard.Season;
import com.sabdroidex.data.sickbeard.Show;
import com.sabdroidex.data.sickbeard.Shows;
import com.sabdroidex.data.sickbeard.ShowSearch;
import com.sabdroidex.utils.json.SimpleJSONMarshaller;


public class SimpleJSONMarshallerTest extends AndroidTestCase  {
    
    public void testMarshaller_Show() throws JSONException, IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        
        StringBuffer stringBuffer = new StringBuffer();
        
        InputStream stream = getClass().getResourceAsStream("show.json");
        int c;
        while ((c = stream.read()) != -1) {
            stringBuffer.append((char) c);
        }
        
        JSONObject jsonObject = new JSONObject(stringBuffer.toString());
        jsonObject = jsonObject.getJSONObject("data");
        
        SimpleJSONMarshaller simpleJSONMarshaller = new SimpleJSONMarshaller(Show.class);
        Show show = (Show) simpleJSONMarshaller.unmarshal(jsonObject);
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
        
        SimpleJSONMarshaller simpleJSONMarshaller = new SimpleJSONMarshaller(Shows.class);
        Shows shows = (Shows) simpleJSONMarshaller.unmarshal(jsonObject);
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
        
        SimpleJSONMarshaller simpleJSONMarshaller = new SimpleJSONMarshaller(SabnzbdConfig.class);
        SabnzbdConfig config = (SabnzbdConfig) simpleJSONMarshaller.unmarshal(jsonObject);
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
        
        SimpleJSONMarshaller simpleJSONMarshaller = new SimpleJSONMarshaller(ShowSearch.class);
        ShowSearch showSearch = (ShowSearch) simpleJSONMarshaller.unmarshal(jsonObject);
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
        
        SimpleJSONMarshaller simpleJSONMarshaller = new SimpleJSONMarshaller(Season.class);
        Season season = (Season) simpleJSONMarshaller.unmarshal(jsonObject);
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
        
        SimpleJSONMarshaller simpleJSONMarshaller = new SimpleJSONMarshaller(FuturePeriod.class);
        FuturePeriod futurePeriod = (FuturePeriod) simpleJSONMarshaller.unmarshal(jsonObject);
        assertNotNull(futurePeriod);
        assertTrue(futurePeriod.getMissed().size() > 0);
        assertTrue(futurePeriod.getToday().size() > 0);
    }
    
    public void testMarshaller_MovieList() throws IOException, JSONException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        StringBuffer stringBuffer = new StringBuffer();

        InputStream stream = getClass().getResourceAsStream("movielist.json");
        int c;
        while ((c = stream.read()) != -1) {
            stringBuffer.append((char) c);
        }

        JSONObject jsonObject = new JSONObject(stringBuffer.toString());
        SimpleJSONMarshaller simpleJSONMarshaller = new SimpleJSONMarshaller(MovieList.class);
        MovieList movieList = (MovieList) simpleJSONMarshaller.unmarshal(jsonObject);
        assertNotNull(movieList);
        assertTrue(movieList.getMovieElements().size() > 0);
    }

    public void testMarshaller_MovieList2() throws IOException, JSONException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        StringBuffer stringBuffer = new StringBuffer();

        InputStream stream = getClass().getResourceAsStream("movielist2.json");
        int c;
        while ((c = stream.read()) != -1) {
            stringBuffer.append((char) c);
        }

        JSONObject jsonObject = new JSONObject(stringBuffer.toString());
        SimpleJSONMarshaller simpleJSONMarshaller = new SimpleJSONMarshaller(MovieList.class);
        MovieList movieList = (MovieList) simpleJSONMarshaller.unmarshal(jsonObject);
        assertNotNull(movieList);
        assertTrue(movieList.getMovieElements().size() > 0);
        assertTrue(movieList.getMovieElements().get(0).getReleases() != null);
        assertTrue(movieList.getMovieElements().get(0).getReleases().size() > 0);
    }
}
