package com.sabdroidex.controllers.sickbeard;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;

import com.sabdroidex.controllers.SABController;
import com.sabdroidex.data.Show;
import com.sabdroidex.data.ShowList;
import com.sabdroidex.data.ShowSearch;
import com.sabdroidex.utils.Preferences;
import com.sabdroidex.utils.json.SimpleJsonMarshaller;
import com.utils.HttpUtil;

public final class SickBeardController extends SABController {
    
    private static final String TAG = "SickBeardController";
    
    private static boolean executingRefreshShows = false;
    private static boolean executingRefreshComing = false;
    private static boolean executingCommand = false;
    
    private static final String URL_TEMPLATE = "[SICKBEARD_URL]/[SICKBEARD_URL_EXTENTION]api/[SICKBEARD_API_KEY]?cmd=[COMMAND]";
    private static final String URL_TVDB = "http://thetvdb.com/banners/posters/[TVDBID]";
    private static final String URL_TVDB_SEASONS = "http://thetvdb.com/banners/seasons/[TVDBID]";
    
    public static enum MESSAGE {
        SHOWS, SHOW, FUTURE, SHOW_GETBANNER, SHOW_GETPOSTER, SHOW_ADDNEW, SB_SEARCHTVDB, UPDATE, SHOW_SEASONLIST, SHOW_SEASONS
    }
    
    /**
     * Adds a show to SickBeard. For the location the configuration setting
     * (default) is used -- if valid. The result sent to the {@link Handler}
     * will be a {@link String} containing the status.
     * 
     * @param messageHandler
     *            The message handler to be notified
     * @param value
     *            The value which will be used to perform the action
     */
    public static void addShow(final Handler messageHandler, final String value) {
        // Already running or settings not ready
        if (executingCommand || !Preferences.isSet(Preferences.SABNZBD_URL))
            return;
        
        Thread thread = new Thread() {
            
            @Override
            public void run() {
                try {
                    Object results[] = new Object[2];
                    String addData = makeApiCall(MESSAGE.SHOW_ADDNEW.toString().toLowerCase(), "tvdbid=" + value);
                    
                    /**
                     * Getting the values from the JSON Object
                     */
                    JSONObject jsonObject = new JSONObject(addData);
                    
                    results[0] = jsonObject;
                    results[1] = jsonObject.getString("result");
                    
                    Message message = new Message();
                    message.setTarget(messageHandler);
                    message.what = MESSAGE.SHOW_ADDNEW.ordinal();
                    message.obj = results[1];
                    message.sendToTarget();
                    
                    Thread.sleep(250);
                    SickBeardController.refreshShows(messageHandler);
                }
                catch (Throwable e) {
                    Log.w(TAG, e.getLocalizedMessage());
                }
                finally {
                    sendUpdateMessageStatus(messageHandler, "");
                }
            }
        };
        
        sendUpdateMessageStatus(messageHandler, MESSAGE.SHOW_ADDNEW.toString());
        
        thread.start();
    }
    
    /**
     * Search for a show to add to SickBeard.
     * 
     * @param messageHandler
     *            The message handler to be notified
     * @param value
     *            The value which will be used to perform the action
     */
    public static void searchShow(final Handler messageHandler, final String value) {
        
        /**
         * If already running or Sickbeard is not configured
         */
        if (executingCommand || !Preferences.isSet(Preferences.SABNZBD_URL))
            return;
        
        Thread thread = new Thread() {
            
            @Override
            public void run() {
                try {
                    
                    String searchData = makeApiCall(MESSAGE.SB_SEARCHTVDB.toString().toLowerCase(), "name="
                            + URLEncoder.encode(value, "UTF-8"), "lang=en");
                    JSONObject jsonObject = new JSONObject(searchData);
                    
                    if (!jsonObject.isNull("message") && !"".equals(jsonObject.getString("message"))) {
                        sendUpdateMessageStatus(messageHandler, "SickBeard : " + jsonObject.getString("message"));
                    }
                    else {
                        jsonObject = jsonObject.getJSONObject("data");
                        SimpleJsonMarshaller simpleJsonMarshaller = new SimpleJsonMarshaller(ShowSearch.class);
                        ShowSearch showSearch = (ShowSearch) simpleJsonMarshaller.unmarshal(jsonObject);
                        
                        Message message = new Message();
                        message.setTarget(messageHandler);
                        message.what = MESSAGE.SB_SEARCHTVDB.ordinal();
                        message.obj = showSearch;
                        message.sendToTarget();
                    }
                }
                catch (Throwable e) {
                    Log.w(TAG, e.getLocalizedMessage());
                }
                finally {
                    sendUpdateMessageStatus(messageHandler, "");
                }
            }
        };
        
        sendUpdateMessageStatus(messageHandler, MESSAGE.SB_SEARCHTVDB.toString());
        
        thread.start();
    }
    
    /**
     * Refresh the shows that are in SickBeard. The result sent to the
     * {@link Handler} will be an array of all the shows.
     * 
     * @param messageHandler
     *            The message handler to be notified
     */
    public static void refreshShows(final Handler messageHandler) {
        
        /**
         * If already running or Sickbeard is not configured
         */
        if (executingRefreshShows || !Preferences.isSet(Preferences.SICKBEARD_URL))
            return;
        
        Thread thread = new Thread() {
            
            @Override
            public void run() {
                
                String statusMessage = "";
                
                try {
                    String queueData = makeApiCall(MESSAGE.SHOWS.toString().toLowerCase());
                    JSONObject jsonObject = new JSONObject(queueData);
                    ShowList showList = null;
                    
                    if (!jsonObject.isNull("message") && !"".equals(jsonObject.getString("message"))) {
                        sendUpdateMessageStatus(messageHandler, "SickBeard : " + jsonObject.getString("message"));
                    }
                    
                    else {
                        jsonObject = jsonObject.getJSONObject("data");
                        SimpleJsonMarshaller jsonMarshaller = new SimpleJsonMarshaller(ShowList.class);
                        showList = (ShowList) jsonMarshaller.unmarshal(jsonObject);
                        Collections.sort(showList.getShowElements());
                    }
                    
                    Message message = new Message();
                    message.setTarget(messageHandler);
                    message.what = MESSAGE.SHOWS.ordinal();
                    message.obj = showList;
                    message.sendToTarget();
                }
                catch (IOException e) {
                    Log.w(TAG, e.getLocalizedMessage());
                    statusMessage = e.getLocalizedMessage();
                }
                catch (Throwable e) {
                    Log.w(TAG, e.getLocalizedMessage());
                }
                finally {
                    executingRefreshShows = false;
                    sendUpdateMessageStatus(messageHandler, statusMessage);
                }
            }
        };
        
        executingRefreshShows = true;
        
        thread.start();
    }
    
    //TODO correct this once and for all !!!
    /**
     * Makes a call to retrieve all the episodes that will be available soon or
     * those that have been missed.
     * 
     * @param messageHandler
     *            The message handler to be notified
     */
    public static void refreshFuture(final Handler messageHandler) {
        
        /**
         * If already running or Sickbeard is not configured
         */
        if (executingRefreshComing || !Preferences.isSet(Preferences.SICKBEARD_URL))
            return;
        
        Thread thread = new Thread() {
            
            @Override
            public void run() {
                
                try {
                    Object results[] = new Object[2];
                    String queueData = makeApiCall(MESSAGE.FUTURE.toString().toLowerCase(), "sort=date");
                    
                    ArrayList<Object[]> rows = new ArrayList<Object[]>();
                    
                    /**
                     * Getting the values from the JSON Object
                     */
                    JSONObject jsonObject = new JSONObject(queueData);
                    if (!jsonObject.isNull("message") && !"".equals(jsonObject.getString("message"))) {
                        sendUpdateMessageStatus(messageHandler, "SickBeard : " + jsonObject.getString("message"));
                    }
                    else {
                        jsonObject = jsonObject.getJSONObject("data");
                        results[0] = jsonObject;
                        
                        Iterator<?> iterator = jsonObject.keys();
                        while (iterator.hasNext()) {
                            String when = (String) iterator.next();
                            JSONArray group = jsonObject.getJSONArray(when);
                            rows.add(new Object[] { when });
                            for (int i = 0; i < group.length(); i++) {
                                /**
                                 * The seventh item will be the banner The
                                 * eighth item will be the poster
                                 */
                                Object[] rowValues = new Object[10];
                                JSONObject current = group.getJSONObject(i);
                                rowValues[0] = when;
                                rowValues[1] = current.getInt("tvdbid");
                                rowValues[2] = current.getString("show_name");
                                
                                rowValues[3] = current.getInt("season");
                                rowValues[4] = current.getInt("episode");
                                rowValues[5] = current.getString("ep_name");
                                rowValues[6] = current.getString("airdate");
                                
                                rowValues[7] = current.getString("airs");
                                rowValues[8] = current.getString("network");
                                rowValues[9] = current.getString("quality");
                                rows.add(rowValues);
                            }
                        }
                        results[1] = rows;
                        
                        Message message = new Message();
                        message.setTarget(messageHandler);
                        message.what = MESSAGE.FUTURE.ordinal();
                        message.obj = results;
                        message.sendToTarget();
                    }
                }
                catch (IOException e) {
                    Log.w(TAG, e.getLocalizedMessage());
                }
                catch (Throwable e) {
                    Log.w(TAG, e.getLocalizedMessage());
                }
                finally {
                    executingRefreshComing = false;
                }
            }
        };
        
        executingRefreshComing = true;
        
        thread.start();
    }
    
    /**
     * Makes a call to retrieve all seasons of a given show.
     * 
     * @param messageHandler
     *            the handler that will be notified upon completion or error.
     * @param value
     *            the showID
     */
    public static void getShow(final Handler messageHandler, final String value) {
        
        /**
         * If Sickbeard is not configured
         */
        if (!Preferences.isSet(Preferences.SICKBEARD_URL))
            return;
        
        Thread thread = new Thread() {
            
            @Override
            public void run() {
                
                try {
                    String data = makeApiCall(MESSAGE.SHOW.toString().toLowerCase(), "tvdbid=" + value);
                    JSONObject jsonObject = new JSONObject(data);
                    Show show = null;
                    
                    if (!jsonObject.isNull("message") && !"".equals(jsonObject.getString("message"))) {
                        sendUpdateMessageStatus(messageHandler, "SickBeard : " + jsonObject.getString("message"));
                    }
                    
                    else {
                        jsonObject = jsonObject.getJSONObject("data");
                        
                        SimpleJsonMarshaller jsonMarshaller = new SimpleJsonMarshaller(Show.class);
                        show = (Show) jsonMarshaller.unmarshal(jsonObject);
                    }
                    
                    Message message = new Message();
                    message.setTarget(messageHandler);
                    message.what = MESSAGE.SHOW.ordinal();
                    message.obj = show;
                    message.sendToTarget();
                }
                catch (IOException e) {
                    Log.w(TAG, e.getLocalizedMessage());
                }
                catch (Throwable e) {
                    Log.w(TAG, e.getLocalizedMessage());
                }
            }
        };
        
        thread.start();
    }
    
    /**
     * Makes a call to retrieve all episodes of a given season for a specific
     * show.
     * 
     * @param messageHandler
     *            the handler that will be notified upon completion or error.
     * @param messageHandler
     * @param string
     * @param string2
     */
    public static void getSeason(final Handler messageHandler, final String showId, final String seasonId) {

        /**
         * If Sickbeard is not configured
         */
        if (!Preferences.isSet(Preferences.SICKBEARD_URL))
            return;
        
        Thread thread = new Thread() {
            
            @Override
            public void run() {
                
                try {
                    String data = makeApiCall(MESSAGE.SHOW.toString().toLowerCase(), "tvdbid=" + showId, "season=" + seasonId);
                    JSONObject jsonObject = new JSONObject(data);
                    Show show = null;
                    
                    if (!jsonObject.isNull("message") && !"".equals(jsonObject.getString("message"))) {
                        sendUpdateMessageStatus(messageHandler, "SickBeard : " + jsonObject.getString("message"));
                    }
                    
                    else {
                        jsonObject = jsonObject.getJSONObject("data");
                        
                        SimpleJsonMarshaller jsonMarshaller = new SimpleJsonMarshaller(Show.class);
                        show = (Show) jsonMarshaller.unmarshal(jsonObject);
                    }
                    
                    Message message = new Message();
                    message.setTarget(messageHandler);
                    message.what = MESSAGE.SHOW.ordinal();
                    message.obj = show;
                    message.sendToTarget();
                }
                catch (IOException e) {
                    Log.w(TAG, e.getLocalizedMessage());
                }
                catch (Throwable e) {
                    Log.w(TAG, e.getLocalizedMessage());
                }
            }
        };
        
        thread.start();
    }
    
    /**
     * This functions handle the API calls to SickBeard to define the URL and
     * parameters
     * 
     * @param command
     *            The type of command that will be sent to SickBeard
     * @return The result of the API call
     * @throws Exception
     *             Thrown if there is any unexpected problem during the
     *             communication with the server
     */
    public static String makeApiCall(String command) throws Exception {
        return makeApiCall(command, "");
    }
    
    /**
     * This functions handle the API calls to SickBeard to define the URL and
     * parameters
     * 
     * @param command
     *            The type of command that will be sent to SickBeard
     * @param extraParams
     *            Any parameter that will have to be part of the URL
     * @return The result of the API call
     * @throws Exception
     *             Thrown if there is any unexpected problem during the
     *             communication with the server
     */
    public static String makeApiCall(String command, String... extraParams) throws Exception {
        
        /**
         * Correcting the command names to be understood by SickBeard
         */
        command = command.replace('_', '.');
        Map<String, String> parameterMap = getAdditionalParameters();
        String url = getFormattedUrl();
        url = url.replace("[COMMAND]", command);
        
        for (String xTraParam : extraParams) {
            if (xTraParam != null && !xTraParam.trim().equals("")) {
                url = url + "&" + xTraParam;
            }
        }
        
        String result = new String(HttpUtil.getInstance().getDataAsCharArray(url, parameterMap));
        return result;
    }
    
    /**
     * This function gets the URL used to connect to the Sabnzbd server
     * 
     * @return url A {@link String} containing the URL of the Sabnzbd server
     */
    private static String getFormattedUrl() {
        String url = URL_TEMPLATE;
        /**
         * Checking if there is a port to concatenate to the URL
         */
        if ("".equals(Preferences.get(Preferences.SICKBEARD_PORT))) {
            url = url.replace("[SICKBEARD_URL]", Preferences.get(Preferences.SICKBEARD_URL));
        }
        else {
            url = url.replace("[SICKBEARD_URL]",
                    Preferences.get(Preferences.SICKBEARD_URL) + ":" + Preferences.get(Preferences.SICKBEARD_PORT));
        }
        
        /**
         * Checking the url extention
         */
        if ("".equals(Preferences.get(Preferences.SICKBEARD_URL_EXTENTION))) {
            url = url.replace("[SICKBEARD_URL_EXTENTION]", Preferences.get(Preferences.SICKBEARD_URL_EXTENTION));
        }
        else {
            url = url.replace("[SICKBEARD_URL_EXTENTION]", Preferences.get(Preferences.SICKBEARD_URL_EXTENTION) + "/");
        }
        
        /**
         * Checking if there is an API Key from SickBeard to concatenate to the
         * URL
         */
        if ("".equals(Preferences.get(Preferences.SICKBEARD_API_KEY))) {
            url = url.replace("[SICKBEARD_API_KEY]", "");
        }
        else {
            url = url.replace("[SICKBEARD_API_KEY]", Preferences.get(Preferences.SICKBEARD_API_KEY) + "/");
        }
        
        if (!url.toUpperCase().startsWith("HTTP://") && !url.toUpperCase().startsWith("HTTPS://")) {
            if (Preferences.isEnabled(Preferences.SICKBEARD_SSL)) {
                url = "https://" + url;
            }
            else {
                url = "http://" + url;
            }
        }
        
        return url;
    }
    
    /**
     * This function returns the URL of the banner for a given show
     * 
     * @param command
     *            command The type of command that will be sent to SickBeard
     * @param tvdbid
     *            The TvDBid of the show to get the banner for
     * @return The URL of the banner
     */
    public static String getImageURL(String command, Integer tvdbid) {
        
        /**
         * Correcting the command names to be understood by SickBeard
         */
        command = command.replace('_', '.');
        String url = getFormattedUrl();
        
        url = url.replace("[COMMAND]", command);
        url = url + "&tvdbid=" + tvdbid;
        
        return url;
    }
    
    /**
     * This function returns the URL of the poster for a given show
     * 
     * @param command
     *            command The type of command that will be sent to SickBeard
     * @param tvdbid
     *            The TvDBid of the show to get the poster for
     * @return The URL of the poster
     */
    public static String getPosterURL(String command, Integer tvdbid) {
        String url = URL_TVDB;
        url = url.replace("[TVDBID]", tvdbid + "-1.jpg");
        
        return url;
    }
    
    /**
     * 
     * @return
     */
    @SuppressWarnings("unused")
    private static String getPreferencesParams() {
        String username = Preferences.get(Preferences.SABNZBD_USERNAME);
        String password = Preferences.get(Preferences.SABNZBD_PASSWORD);
        
        String credentials = "";
        if (username != null && !"".equals(username)) {
            credentials += "&ma_username=" + username;
        }
        if (password != null && !"".equals(password)) {
            credentials += "&ma_password=" + password;
        }
        return credentials;
    }
    
    private static Map<String, String> getAdditionalParameters() {
        HashMap<String, String> parameterMap = new HashMap<String, String>();
        
        if (Preferences.isEnabled(Preferences.APACHE)) {
            String apache_auth = Preferences.get(Preferences.APACHE_USERNAME) + ":"
                    + Preferences.get(Preferences.APACHE_PASSWORD);
            String encoding = new String(Base64.encode(apache_auth.getBytes(), Base64.NO_WRAP));
            parameterMap.put("Authorization", "Basic " + encoding);
        }
        
        return parameterMap;
    }
    
    public static String getSeasonPosterURL(String command, Integer tvdbid, Integer season) {
        String url = URL_TVDB_SEASONS;
        url = url.replace("[TVDBID]", tvdbid + "-" + season + "-2.jpg");
        
        return url;
    }
}
