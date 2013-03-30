package com.sabdroidex.controllers.couchpotato;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;

import com.sabdroidex.utils.Preferences;
import com.utils.HttpUtil;

public final class CouchPotatoController {
    
    private static final String TAG = "CouchPotatoController";
    
    public static boolean paused = false;
    private static boolean executingCommand = false;
    private static String api_key = "";
    
    private static final String URL_TEMPLATE = "[COUCHPOTATO_URL]/[COUCHPOTATO_URL_EXTENTION]/api/[API]/[COMMAND]?";
    private static final String API_TEMPLATE = "[COUCHPOTATO_URL]/[COUCHPOTATO_URL_EXTENTION]/getkey/?p=[PASSWORD]&u=[USERNAME]";
    
    public static enum MESSAGE {
        MOVIE_ADD, MOVIE_GET, MOVIE_LIST, MOVIE_SEARCH, PROFILE_LIST, UPDATE;
    }
    
    /**
     * This function handle the API calls to Sabnzbd to define the URL and
     * parameters
     * 
     * @param command
     *            The type of command that will be sent to Sabnzbd
     * @return The result of the API call
     * @throws RuntimeException
     *             Thrown if there is any unexpected problem during the
     *             communication with the server
     */
    public static String makeApiCall(String command) throws Exception {
        return makeApiCall(command, "");
    }
    
    /**
     * This function handle the API calls to Couchpotato to define the URL and
     * parameters
     * 
     * @param command
     *            The type of command that will be sent to Sabnzbd
     * @param extraParams
     *            Any parameter that will have to be part of the URL
     * @return The result of the API call
     * @throws RuntimeException
     *             Thrown if there is any unexpected problem during the
     *             communication with the server
     */
    public static String makeApiCall(String command, String... extraParams) throws Exception {
        
        /**
         * Check if the API key is already retrieved, otherwise retrieve it
         */
        if (api_key.isEmpty()) {
            api_key = getApiKey();
        }
        
        /**
         * Correcting the command names to be understood by CouchPotato
         */
        command = command.replace('_', '.');
        Map<String, String> parameterMap = getAdditionalParameters();
        String url = getFormattedUrl();
        url = url.replace("[COMMAND]", command);
        url = url.replace("[API]", api_key);
        int i = 0;
        
        for (String xTraParam : extraParams) {
            if (xTraParam != null && !xTraParam.trim().equals("")) {
                if (i == 0) {
                    url = url + xTraParam;
                    i++;
                    continue;
                }
                url = url + "&" + xTraParam;
            }
        }
        
        url = url.replace(" ", "%20");
        Log.d(TAG, url);
        
        String result = new String(HttpUtil.getInstance().getDataAsCharArray(url, parameterMap));
        return result;
    }
    
    /**
     * This function gets the URL used to connect to the CouchPotato server
     * 
     * @return url A {@link String} containing the URL of the CouchPotato server
     */
    private static String getFormattedUrl() {
        String url = URL_TEMPLATE;
        
        /**
         * Checking if there is a port to concatenate to the URL
         */
        if ("".equals(Preferences.get(Preferences.COUCHPOTATO_PORT))) {
            url = url.replace("[COUCHPOTATO_URL]", Preferences.get(Preferences.COUCHPOTATO_URL));
        }
        else {
            url = url.replace("[COUCHPOTATO_URL]",
                    Preferences.get(Preferences.COUCHPOTATO_URL) + ":" + Preferences.get(Preferences.COUCHPOTATO_PORT));
        }
        
        /**
         * Checking the url extention
         */
        if ("".equals(Preferences.get(Preferences.COUCHPOTATO_URL_EXTENTION))) {
            url = url.replace("[COUCHPOTATO_URL_EXTENTION]", Preferences.get(Preferences.COUCHPOTATO_URL_EXTENTION));
        }
        else {
            url = url.replace("[COUCHPOTATO_URL_EXTENTION]", Preferences.get(Preferences.COUCHPOTATO_URL_EXTENTION));
        }
        
        if (!url.toUpperCase().startsWith("HTTP://") && !url.toUpperCase().startsWith("HTTPS://")) {
            if (Preferences.isEnabled(Preferences.COUCHPOTATO_SSL)) {
                url = "https://" + url;
            }
            else {
                url = "http://" + url;
            }
        }
        
        return url;
    }
    
    /**
     * Retrieve API_URL of couchpotato by using username & password.
     * 
     * @return API_URL
     */
    private static String getApiUrl() {
        String url = API_TEMPLATE;
        
        /**
         * Checking if there is a port to concatenate to the URL
         */
        if ("".equals(Preferences.get(Preferences.COUCHPOTATO_PORT))) {
            url = url.replace("[COUCHPOTATO_URL]", Preferences.get(Preferences.COUCHPOTATO_URL));
        }
        else {
            url = url.replace("[COUCHPOTATO_URL]",
                    Preferences.get(Preferences.COUCHPOTATO_URL) + ":" + Preferences.get(Preferences.COUCHPOTATO_PORT));
        }
        
        /**
         * Checking the url extention
         */
        if ("".equals(Preferences.get(Preferences.COUCHPOTATO_URL_EXTENTION))) {
            url = url.replace("[COUCHPOTATO_URL_EXTENTION]", Preferences.get(Preferences.COUCHPOTATO_URL_EXTENTION));
        }
        else {
            /**
             * TODO: Fix last /
             */
            url = url.replace("[COUCHPOTATO_URL_EXTENTION]", Preferences.get(Preferences.COUCHPOTATO_URL_EXTENTION));
        }
        
        /**
         * Check if user credentials are being used
         */
        if (!"".equals(Preferences.COUCHPOTATO_USERNAME)) {
            url = url.replace("[USERNAME]", "md5(" + Preferences.COUCHPOTATO_USERNAME + ")");
        }
        else {
            url = url.replace("[USERNAME]", "");
        }
        if (!"".equals(Preferences.COUCHPOTATO_PASSWORD)) {
            url = url.replace("[PASSWORD]", "md5(" + Preferences.COUCHPOTATO_PASSWORD + ")");
        }
        else {
            url = url.replace("[PASSWORD]", "");
        }
        
        if (!url.toUpperCase().startsWith("HTTP://") && !url.toUpperCase().startsWith("HTTPS://")) {
            if (Preferences.isEnabled(Preferences.COUCHPOTATO_SSL)) {
                url = "https://" + url;
            }
            else {
                url = "http://" + url;
            }
        }
        Log.d(TAG, url);
        return url;
    }
    
    /**
     * Retrieving API key from CouchPotato
     * 
     * @return API_KEY
     */
    private static String getApiKey() {
        String url = getApiUrl();
        Map<String, String> Parameters = getAdditionalParameters();
        try {
            
            String result = new String(HttpUtil.getInstance().getDataAsCharArray(url, Parameters));
            
            JSONObject jsonObject = new JSONObject(result);
            if (jsonObject.getBoolean("success")) {
                return jsonObject.getString("api_key");
            }
        }
        catch (RuntimeException e) {
            Log.w(TAG, " " + e.getLocalizedMessage());
        }
        catch (Throwable e) {
            Log.w(TAG, " " + e.getLocalizedMessage());
        }
        return "";
    }
    
    /**
     * Add movie to couchpotato
     * 
     * @param messageHandler
     *            Handler
     * @param profile
     *            CouchPotato profile
     * @param idIMDb
     *            IMDB-ID
     * @param movieTitle
     *            Title of movie to add
     */
    public static void addMovie(final Handler messageHandler, final String profile, final String idIMDb,
            final String movieTitle) {
        if (executingCommand || !Preferences.isSet(Preferences.COUCHPOTATO_URL)) {
            return;
        }
        
        Thread thread = new Thread() {
            
            @Override
            public void run() {
                try {
                    String result = makeApiCall(MESSAGE.MOVIE_ADD.toString().toLowerCase(), "profile_id=" + profile,
                            "identifier=" + idIMDb, "title=" + movieTitle);
                    
                    JSONObject jsonObject = new JSONObject(result);
                    
                    if (jsonObject.isNull("added")) {
                        sendUpdateMessageStatus(messageHandler, "");
                    }
                    else {
                        String title = jsonObject.getJSONObject("movie").getJSONObject("library")
                                .getJSONArray("titles").getJSONObject(0).getString("title");
                        sendUpdateMessageStatus(messageHandler, title);
                    }
                    
                }
                catch (RuntimeException e) {
                    Log.w(TAG, " " + e.getLocalizedMessage());
                    sendUpdateMessageStatus(messageHandler, "Error");
                }
                catch (Throwable e) {
                    Log.w(TAG, " " + e.getLocalizedMessage());
                }
                finally {
                    executingCommand = false;
                    sendUpdateMessageStatus(messageHandler, "");
                }
            }
        };
        executingCommand = true;
        thread.start();
    }
    
    /**
     * Retrieve possible download profiles from CouchPotat
     * 
     * @param messageHandler
     *            Handler
     */
    public static void getProfiles(final Handler messageHandler) {
        if (executingCommand || !Preferences.isSet(Preferences.COUCHPOTATO_URL)) {
            return;
        }
        
        Thread thread = new Thread() {
            
            @Override
            public void run() {
                try {
                    String result = makeApiCall(MESSAGE.PROFILE_LIST.toString().toLowerCase());
                    
                    final Object results[] = new Object[2];
                    ArrayList<String> label = new ArrayList<String>();
                    ArrayList<String> id = new ArrayList<String>();
                    JSONObject jsonObject = new JSONObject(result);
                    
                    if (jsonObject.getBoolean("success")) {
                        JSONArray profileList = jsonObject.getJSONArray("list");
                        for (int i = 0; i < profileList.length(); i++) {
                            label.add(i, profileList.getJSONObject(i).getString("label"));
                            id.add(i, Integer.toString(profileList.getJSONObject(i).getInt("id")));
                        }
                        results[0] = label;
                        results[1] = id;
                        
                        Message message = new Message();
                        message.setTarget(messageHandler);
                        message.what = MESSAGE.PROFILE_LIST.ordinal();
                        message.obj = results;
                        message.sendToTarget();
                    }
                }
                catch (RuntimeException e) {
                    Log.w(TAG, " " + e.getLocalizedMessage());
                }
                catch (Throwable e) {
                    Log.w(TAG, " " + e.getLocalizedMessage());
                }
                finally {
                    executingCommand = false;
                }
            }
        };
        executingCommand = true;
        thread.start();
    }
    
    /**
     * Search for a movie based on title
     * 
     * @param messageHandler
     *            Handler
     * @param searchTitle
     *            Movie title to search for
     */
    public static void searchMovie(final Handler messageHandler, final String searchTitle) {
        if (executingCommand || !Preferences.isSet(Preferences.COUCHPOTATO_URL)) {
            return;
        }
        
        Thread thread = new Thread() {
            
            @Override
            public void run() {
                
                try {
                    
                    String result = makeApiCall(MESSAGE.MOVIE_SEARCH.toString().toLowerCase(), "q=" + searchTitle);
                    Object results[] = new Object[1];
                    ArrayList<Object[]> rows = new ArrayList<Object[]>();
                    
                    JSONObject jsonObject = new JSONObject(result);
                    
                    JSONArray movies = jsonObject.getJSONArray("movies");
                    for (int i = 0; i < movies.length(); i++) {
                        Object[] rowValues = new Object[2];
                        rowValues[0] = movies.getJSONObject(i).getString("original_title");
                        rowValues[1] = movies.getJSONObject(i).getString("imdb");
                        rows.add(rowValues);
                    }
                    
                    results[0] = rows;
                    
                    Message message = new Message();
                    message.setTarget(messageHandler);
                    message.what = MESSAGE.MOVIE_SEARCH.ordinal();
                    message.obj = results;
                    message.sendToTarget();
                    
                }
                catch (IOException e) {
                    Log.w(TAG, " " + e.getLocalizedMessage());
                }
                catch (Throwable e) {
                    Log.w(TAG, " " + e.getLocalizedMessage());
                }
                finally {
                    executingCommand = false;
                }
            }
        };
        executingCommand = true;
        thread.start();
    }
    
    /**
     * Get Additional Parameters
     * 
     * @return Parameters
     */
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
    
    /**
     * Sends a message to the calling {@link Activity} to update it's status bar
     * 
     * @param messageHandler
     *            The message handler to be notified
     * @param text
     *            The text to write in the message
     */
    private static void sendUpdateMessageStatus(Handler messageHandler, String text) {
        
        Message message = new Message();
        message.setTarget(messageHandler);
        message.what = MESSAGE.UPDATE.ordinal();
        message.obj = text;
        message.sendToTarget();
    }
}
