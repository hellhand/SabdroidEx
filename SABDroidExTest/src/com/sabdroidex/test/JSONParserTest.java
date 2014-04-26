package com.sabdroidex.test;

import android.test.AndroidTestCase;

import com.sabdroidex.data.couchpotato.MovieList;
import com.sabdroidex.data.couchpotato.MovieReleases;
import com.sabdroidex.data.sabnzbd.MiscConfig;
import com.sabdroidex.data.sabnzbd.SabnzbdConfig;
import com.sabdroidex.data.sickbeard.Season;
import com.sabdroidex.data.sickbeard.Show;
import com.sabdroidex.data.sickbeard.ShowSearch;
import com.sabdroidex.data.sickbeard.Shows;
import com.sabdroidex.utils.json.impl.JSONParser;
import com.sabdroidex.utils.json.impl.JSONPojoMapper;
import com.sabdroidex.utils.json.impl.SimpleJSONMarshaller;

import junit.framework.Assert;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Marc on 14/12/13.
 */
public class JSONParserTest extends AndroidTestCase {

    public void testParseConfig() throws IOException, IllegalAccessException, InstantiationException, ParseException {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        InputStream stream = getClass().getResourceAsStream("config.json");
        int c;
        while ((c = stream.read()) != -1) {
            byteArrayOutputStream.write((char) c);
        }

        InputStream inputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());

        JSONParser jsonParser = new JSONParser();
        jsonParser.setBadFormat(true);
        Map<String, Object> result = (Map<String, Object>) jsonParser.parse(inputStream, new AtomicInteger(0), null);
        Assert.assertNotNull(result);

        JSONPojoMapper simpleJSONMarshaller = new JSONPojoMapper(SabnzbdConfig.class);
        SabnzbdConfig sabnzbdConfig = (SabnzbdConfig) simpleJSONMarshaller.unMarshal(result);

        assertNotNull(sabnzbdConfig);
        assertNotNull(sabnzbdConfig.getMisc());
    }

    public void testParserFuture() throws IOException, ParseException {

        long l = System.currentTimeMillis();

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        InputStream stream = getClass().getResourceAsStream("future.json");
        int c;
        while ((c = stream.read()) != -1) {
            byteArrayOutputStream.write((char) c);
        }

        InputStream inputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());

        JSONParser jsonParser = new JSONParser();
        Map<String, Object> result = (Map<String, Object>) jsonParser.parse(inputStream, new AtomicInteger(0), null);
        Assert.assertNotNull(result);

        l = System.currentTimeMillis() - l;
        System.out.println(l + " ms");
    }

    public void testParserMovieList() throws IOException, IllegalAccessException, InstantiationException, ParseException {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        InputStream stream = getClass().getResourceAsStream("movielist.json");
        int c;
        while ((c = stream.read()) != -1) {
            byteArrayOutputStream.write((char) c);
        }

        InputStream inputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());

        JSONParser jsonParser = new JSONParser();
        Map<String, Object> result = (Map<String, Object>) jsonParser.parse(inputStream, new AtomicInteger(0), null);
        Assert.assertNotNull(result);

        JSONPojoMapper simpleJSONMarshaller = new JSONPojoMapper(MovieList.class);
        MovieList movieList = (MovieList) simpleJSONMarshaller.unMarshal(result);
        assertNotNull(movieList);
    }

    public void testParserMovieList2() throws IOException, IllegalAccessException, InstantiationException, ParseException {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        InputStream stream = getClass().getResourceAsStream("movielist2.json");
        int c;
        while ((c = stream.read()) != -1) {
            byteArrayOutputStream.write((char) c);
        }

        InputStream inputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());

        JSONParser jsonParser = new JSONParser();
        Map<String, Object> result = (Map<String, Object>) jsonParser.parse(inputStream, new AtomicInteger(0), null);
        Assert.assertNotNull(result);

        JSONPojoMapper simpleJSONMarshaller = new JSONPojoMapper(MovieList.class);
        MovieList movieList = (MovieList) simpleJSONMarshaller.unMarshal(result);
        assertNotNull(movieList);
    }

    public void testParserMovieReleases() throws IOException, IllegalAccessException, InstantiationException, ParseException {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        InputStream stream = getClass().getResourceAsStream("moviereleases.json");
        int c;
        while ((c = stream.read()) != -1) {
            byteArrayOutputStream.write((char) c);
        }

        InputStream inputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());

        JSONParser jsonParser = new JSONParser();
        Map<String, Object> result = (Map<String, Object>) jsonParser.parse(inputStream, new AtomicInteger(0), null);
        Assert.assertNotNull(result);

        JSONPojoMapper simpleJSONMarshaller = new JSONPojoMapper(MovieReleases.class);
        MovieReleases movieReleases = (MovieReleases) simpleJSONMarshaller.unMarshal(result);
        assertNotNull(movieReleases);
    }

    public void testParserShow() throws IOException, IllegalAccessException, InstantiationException, ParseException {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        InputStream stream = getClass().getResourceAsStream("show.json");
        int c;
        while ((c = stream.read()) != -1) {
            byteArrayOutputStream.write((char) c);
        }

        InputStream inputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());

        JSONParser jsonParser = new JSONParser();
        Map<String, Object> result = (Map<String, Object>) jsonParser.parse(inputStream, new AtomicInteger(0), null);
        Assert.assertNotNull(result);

        result = (Map<String, Object>) result.get("data");

        JSONPojoMapper simpleJSONMarshaller = new JSONPojoMapper(com.sabdroidex.data.sickbeard.Show.class);
        Show show = (Show) simpleJSONMarshaller.unMarshal(result);
        assertNotNull(show);
        System.out.println(show.getShowName());
    }

    public void testParserShowList() throws IOException, IllegalAccessException, InstantiationException, ParseException {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        InputStream stream = getClass().getResourceAsStream("showlist.json");
        int c;
        while ((c = stream.read()) != -1) {
            byteArrayOutputStream.write((char) c);
        }

        InputStream inputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());

        JSONParser jsonParser = new JSONParser();
        Map<String, Object> result = (Map<String, Object>) jsonParser.parse(inputStream, new AtomicInteger(0), null);
        Assert.assertNotNull(result);

        result = (Map<String, Object>) result.get("data");

        JSONPojoMapper simpleJSONMarshaller = new JSONPojoMapper(Shows.class);
        Shows shows = (Shows) simpleJSONMarshaller.unMarshal(result);

        assertNotNull(shows);
        assertTrue(shows.getShowElements().size() > 0);
        for (Show show : shows.getShowElements()) {
            System.out.println(show.getShowName());
        }
    }

    public void testParserShowSearch() throws IOException, IllegalAccessException, InstantiationException, ParseException {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        InputStream stream = getClass().getResourceAsStream("showsearch.json");
        int c;
        while ((c = stream.read()) != -1) {
            byteArrayOutputStream.write((char) c);
        }

        InputStream inputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());

        JSONParser jsonParser = new JSONParser();
        Map<String, Object> result = (Map<String, Object>) jsonParser.parse(inputStream, new AtomicInteger(0), null);
        Assert.assertNotNull(result);

        JSONPojoMapper simpleJSONMarshaller = new JSONPojoMapper(ShowSearch.class);
        ShowSearch showSearch = (ShowSearch) simpleJSONMarshaller.unMarshal(result);
        assertNotNull(showSearch);
    }

    public void testParserShowSeason() throws IOException, IllegalAccessException, InstantiationException, ParseException {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        InputStream stream = getClass().getResourceAsStream("showseason.json");
        int c;
        while ((c = stream.read()) != -1) {
            byteArrayOutputStream.write((char) c);
        }

        InputStream inputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());

        JSONParser jsonParser = new JSONParser();
        Map<String, Object> result = (Map<String, Object>) jsonParser.parse(inputStream, new AtomicInteger(0), null);
        Assert.assertNotNull(result);

        JSONPojoMapper simpleJSONMarshaller = new JSONPojoMapper(Season.class);
        Season season = (Season) simpleJSONMarshaller.unMarshal(result);
        assertNotNull(season);
    }
}
