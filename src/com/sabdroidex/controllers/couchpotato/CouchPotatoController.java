/*
 * Copyright (C) 2011-2013  Roy Kokkelkoren
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.*
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.sabdroidex.controllers.couchpotato;

import java.io.IOException;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.sabdroidex.data.couchpotato.MovieList;
import com.sabdroidex.data.couchpotato.MovieSearch;
import com.sabdroidex.utils.Preferences;
import com.sabdroidex.utils.json.SimpleJsonMarshaller;
import com.utils.ApacheCredentialProvider;
import com.utils.HttpUtil;

public final class CouchPotatoController {

    private static final String TAG = "CouchPotatoController";

    private static boolean executingRefreshMovies = false;
    private static boolean executingCommand = false;

    private static HashMap<Integer, String> profiles;
    private static HashMap<Integer, String> status;
    public static boolean paused = false;
    private static String api_key = "";

    private static final String URL_TEMPLATE = "[COUCHPOTATO_URL]/[COUCHPOTATO_URL_EXTENTION]api/[API]/[COMMAND]/";
    private static final String API_TEMPLATE = "[COUCHPOTATO_URL]/[COUCHPOTATO_URL_EXTENTION]getkey/?p=[PASSWORD]&u=[USERNAME]";

    public static enum MESSAGE {
        MOVIE_ADD, MOVIE_DELETE, MOVIE_EDIT, MOVIE_REFRESH, MOVIE_GET, MOVIE_LIST, MOVIE_SEARCH, PROFILE_LIST, UPDATE, STATUS_LIST, APP_RESTART, APP_SHUTDOWN, RELEASE_DELETE, RELEASE_IGNORE, RELEASE_DOWNLOAD;
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
            getApiKey();
            getStatusList();
            getProfiles();
        }

        /**
         * Correcting the command names to be understood by CouchPotato
         */
        command = command.replace('_', '.');
        String url = getFormattedUrl();
        url = url.replace("[COMMAND]", command);
        url = url.replace("[API]", api_key);

        for (final String xTraParam : extraParams) {
            if (xTraParam != null && !xTraParam.trim().equals("")) {
                url = url.endsWith("/") ? url + "?" + xTraParam : url + "&" + xTraParam;
            }
        }

        url = url.replace(" ", "%20");
        Log.d(TAG, url);

        final String result = new String(HttpUtil.getInstance().getDataAsCharArray(url, ApacheCredentialProvider.getCredentialsProvider()));
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
            url = url.replace("[COUCHPOTATO_URL]", Preferences.get(Preferences.COUCHPOTATO_URL) + ":" + Preferences.get(Preferences.COUCHPOTATO_PORT));
        }

        /**
         * Checking the url extention
         */
        if ("".equals(Preferences.get(Preferences.COUCHPOTATO_URL_EXTENTION))) {
            url = url.replace("[COUCHPOTATO_URL_EXTENTION]", Preferences.get(Preferences.COUCHPOTATO_URL_EXTENTION));
        }
        else {
            url = url.replace("[COUCHPOTATO_URL_EXTENTION]", Preferences.get(Preferences.COUCHPOTATO_URL_EXTENTION) + "/");
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
            url = url.replace("[COUCHPOTATO_URL]", Preferences.get(Preferences.COUCHPOTATO_URL) + ":" + Preferences.get(Preferences.COUCHPOTATO_PORT));
        }

        /**
         * Checking the url extention
         */
        if ("".equals(Preferences.get(Preferences.COUCHPOTATO_URL_EXTENTION))) {
            url = url.replace("[COUCHPOTATO_URL_EXTENTION]", Preferences.get(Preferences.COUCHPOTATO_URL_EXTENTION));
        }
        else {
            url = url.replace("[COUCHPOTATO_URL_EXTENTION]", Preferences.get(Preferences.COUCHPOTATO_URL_EXTENTION) + "/");
        }

        /**
         * Check if user credentials are being used
         */
        if (!"".equals(Preferences.get(Preferences.COUCHPOTATO_USERNAME, ""))) {
            url = url.replace("[USERNAME]", "md5(" + Preferences.COUCHPOTATO_USERNAME + ")");
        }
        else {
            url = url.replace("[USERNAME]", "");
        }
        if (!"".equals(Preferences.get(Preferences.COUCHPOTATO_PASSWORD, ""))) {
            url = url.replace("[PASSWORD]", "md5(" + Preferences.COUCHPOTATO_PASSWORD + ")");
        }
        else {
            url = url.replace("[PASSWORD]", "");
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
    private static void getApiKey() {
        String url = getApiUrl();
        try {

            final String result = new String(HttpUtil.getInstance().getDataAsCharArray(url, ApacheCredentialProvider.getCredentialsProvider()));

            JSONObject jsonObject = new JSONObject(result);
            if (jsonObject.getBoolean("success")) {
                api_key = jsonObject.getString("api_key");
            }
        }
        catch (RuntimeException e) {
            Log.w(TAG, " " + e.getLocalizedMessage());
        }
        catch (Throwable e) {
            Log.w(TAG, " " + e.getLocalizedMessage());
        }
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
    public static void addMovie(final Handler messageHandler, final String profile, final String idIMDb, final String movieTitle) {
        if (executingCommand || !Preferences.isSet(Preferences.COUCHPOTATO_URL)) {
            return;
        }

        Thread thread = new Thread() {

            @Override
            public void run() {
                try {
                    String result = makeApiCall(MESSAGE.MOVIE_ADD.toString().toLowerCase(), "profile_id=" + profile, "identifier=" + idIMDb, "title="
                            + movieTitle);

                    JSONObject jsonObject = new JSONObject(result);
                    result = (String) jsonObject.get("added");

                    Message message = new Message();
                    message.setTarget(messageHandler);
                    message.what = MESSAGE.MOVIE_ADD.hashCode();
                    message.obj = result;
                    message.sendToTarget();

                    Thread.sleep(500);
                    CouchPotatoController.refreshMovies(messageHandler, "active,done");

                }
                catch (IOException e) {
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
     * Retrieve possible download profiles from CouchPotato
     */
    public synchronized static void getStatusList() {
        if ("".equals(Preferences.get(Preferences.COUCHPOTATO_URL))) {
            return;
        }

        Thread thread = new Thread() {

            @Override
            public void run() {
                try {
                    String result = makeApiCall(MESSAGE.STATUS_LIST.toString().toLowerCase());
                    JSONObject jsonObject = new JSONObject(result);

                    if (jsonObject.getBoolean("success")) {
                        JSONArray profileList = jsonObject.getJSONArray("list");
                        status = new HashMap<Integer, String>();
                        for (int i = 0; i < profileList.length(); i++) {
                            status.put(profileList.getJSONObject(i).getInt("id"), profileList.getJSONObject(i).getString("label"));
                        }
                    }
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
     * Retrieve possible download profiles from CouchPotato
     * 
     * @param messageHandler
     *            Handler
     */
    public synchronized static void getStatusList(final Handler messageHandler) {
        if (executingCommand || "".equals(Preferences.get(Preferences.COUCHPOTATO_URL))) {
            return;
        }

        Thread thread = new Thread() {

            @Override
            public void run() {
                try {
                    String result = makeApiCall(MESSAGE.STATUS_LIST.toString().toLowerCase());

                    JSONObject jsonObject = new JSONObject(result);

                    if (jsonObject.getBoolean("success")) {
                        JSONArray profileList = jsonObject.getJSONArray("list");
                        status = new HashMap<Integer, String>();
                        for (int i = 0; i < profileList.length(); i++) {
                            status.put(profileList.getJSONObject(i).getInt("id"), profileList.getJSONObject(i).getString("label"));
                        }

                        Message message = new Message();
                        message.setTarget(messageHandler);
                        message.what = MESSAGE.PROFILE_LIST.hashCode();
                        message.obj = status;
                        message.sendToTarget();
                    }
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
     * Retrieve possible download profiles from CouchPotato
     */
    public synchronized static void getProfiles() {
        if ("".equals(Preferences.get(Preferences.COUCHPOTATO_URL))) {
            return;
        }

        Thread thread = new Thread() {

            @Override
            public void run() {
                try {
                    String result = makeApiCall(MESSAGE.PROFILE_LIST.toString().toLowerCase());
                    JSONObject jsonObject = new JSONObject(result);

                    if (jsonObject.getBoolean("success")) {
                        profiles = new HashMap<Integer, String>();
                        JSONArray profileList = jsonObject.getJSONArray("list");
                        for (int i = 0; i < profileList.length(); i++) {
                            profiles.put(profileList.getJSONObject(i).getInt("id"), profileList.getJSONObject(i).getString("label"));
                        }
                    }
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
     * Retrieve possible download profiles from CouchPotat
     * 
     * @param messageHandler
     *            Handler
     */
    public synchronized static void getProfiles(final Handler messageHandler) {
        if (executingCommand || !Preferences.isSet(Preferences.COUCHPOTATO_URL)) {
            return;
        }

        Thread thread = new Thread() {

            @Override
            public void run() {
                try {
                    String result = makeApiCall(MESSAGE.PROFILE_LIST.toString().toLowerCase());

                    JSONObject jsonObject = new JSONObject(result);

                    if (jsonObject.getBoolean("success") && profiles == null) {
                        profiles = new HashMap<Integer, String>();
                        JSONArray profileList = jsonObject.getJSONArray("list");
                        for (int i = 0; i < profileList.length(); i++) {
                            profiles.put(profileList.getJSONObject(i).getInt("id"), profileList.getJSONObject(i).getString("label"));
                        }
                    }

                    Message message = new Message();
                    message.setTarget(messageHandler);
                    message.what = MESSAGE.PROFILE_LIST.hashCode();
                    message.obj = profiles;
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
        sendUpdateMessageStatus(messageHandler, MESSAGE.MOVIE_DELETE.toString());
        thread.start();
    }

    /**
     * This function refreshes the elements from movies.
     * 
     * @param messageHandler
     *            The message handler that will receive the result
     * @param status
     *            The status we want to fetch
     */
    public static void refreshMovies(final Handler messageHandler, final String status) {
        if (executingRefreshMovies || !Preferences.isSet(Preferences.COUCHPOTATO_URL)) {
            return;
        }

        Thread thread = new Thread() {

            @Override
            public void run() {
                try {

                    String result = makeApiCall(MESSAGE.MOVIE_LIST.toString().toLowerCase(), "status=" + status);
                    JSONObject jsonObject = new JSONObject(result);
                    MovieList movieList = null;

                    if (!jsonObject.isNull("message") && !"".equals(jsonObject.getString("message"))) {
                        sendUpdateMessageStatus(messageHandler, "SickBeard : " + jsonObject.getString("message"));
                    }
                    else {
                        SimpleJsonMarshaller jsonMarshaller = new SimpleJsonMarshaller(MovieList.class);
                        movieList = (MovieList) jsonMarshaller.unmarshal(jsonObject);
                    }

                    Message message = new Message();
                    message.setTarget(messageHandler);
                    message.what = MESSAGE.MOVIE_LIST.hashCode();
                    message.obj = movieList;
                    message.sendToTarget();
                }
                catch (IOException e) {
                    Log.w(TAG, " " + e.getMessage());
                }
                catch (Throwable e) {
                    Log.w(TAG, " " + e.getMessage());
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
     * Delete a move Based on ID
     * 
     * @param messageHandler
     *            Handler
     * @param ids
     *            ID's of movies to delete
     */
    public static void deleteMovie(final Handler messageHandler, final int... ids) {
        if (executingCommand || !Preferences.isSet(Preferences.COUCHPOTATO_URL)) {
            return;
        }

        Thread thread = new Thread() {

            @Override
            public void run() {
                String movieIds = "";

                for (int i = 0; i < ids.length; i++) {
                    if (i == 0)
                        movieIds += Integer.toString(ids[i]);
                    else
                        movieIds += "," + Integer.toString(ids[i]);
                }

                try {
                    String result = makeApiCall(MESSAGE.MOVIE_DELETE.toString().toLowerCase(), "id=" + movieIds);
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getBoolean("success") && ids.length != 1) {
                        // TODO: Resource bundle
                        sendUpdateMessageStatus(messageHandler, "Deleted movies");
                    }
                    else if (jsonObject.getBoolean("success") && ids.length == 1) {
                        // TODO: Resource bundle
                        sendUpdateMessageStatus(messageHandler, "Deleted movie");
                    }
                    else {
                        // TODO: Resource bundle
                        sendUpdateMessageStatus(messageHandler, "Failed");
                    }

                    Thread.sleep(100);
                    CouchPotatoController.refreshMovies(messageHandler, "");

                }
                catch (IOException e) {
                    Log.w(TAG, " " + e.getLocalizedMessage());
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
        sendUpdateMessageStatus(messageHandler, MESSAGE.MOVIE_DELETE.toString());
        thread.start();
    }

    /**
     * Edit a move Based on ID and Profile_ID
     * 
     * @param messageHandler
     *            Handler
     * @param ids
     *            ID's of movies to edit
     * @param profile_id
     *            Profile ID of profile to edit the movies in
     */
    public static void editMovie(final Handler messageHandler, final int profile_id, final int... ids) {
        if (executingCommand || !Preferences.isSet(Preferences.COUCHPOTATO_URL)) {
            return;
        }

        Thread thread = new Thread() {

            @Override
            public void run() {
                String movieIds = "";

                for (int i = 0; i < ids.length; i++) {
                    if (i == 0)
                        movieIds += Integer.toString(ids[i]);
                    else
                        movieIds += "," + Integer.toString(ids[i]);
                }

                try {
                    String result = makeApiCall(MESSAGE.MOVIE_EDIT.toString().toLowerCase(), "profile_id=" + profile_id, "id=" + movieIds);

                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getBoolean("success")) {
                        // TODO: Resource bundle
                        sendUpdateMessageStatus(messageHandler, "Edited");
                    }
                    else {
                        // TODO: Resource bundle
                        sendUpdateMessageStatus(messageHandler, "Failed");
                    }
                    Thread.sleep(100);
                    CouchPotatoController.refreshMovies(messageHandler, "active,done");

                }
                catch (IOException e) {
                    Log.w(TAG, " " + e.getLocalizedMessage());
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
        sendUpdateMessageStatus(messageHandler, MESSAGE.MOVIE_EDIT.toString());
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
                    JSONObject jsonObject = new JSONObject(result);
                    MovieSearch movieList = null;

                    if (!jsonObject.isNull("message") && !"".equals(jsonObject.getString("message"))) {
                        // TODO: Resource bundle
                        sendUpdateMessageStatus(messageHandler, "SickBeard : " + jsonObject.getString("message"));
                    }
                    else {
                        SimpleJsonMarshaller jsonMarshaller = new SimpleJsonMarshaller(MovieSearch.class);
                        movieList = (MovieSearch) jsonMarshaller.unmarshal(jsonObject);
                    }

                    Message message = new Message();
                    message.setTarget(messageHandler);
                    message.what = MESSAGE.MOVIE_SEARCH.hashCode();
                    message.obj = movieList;
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
     * Ignore a release
     * 
     * @param messageHandler
     *            Handler
     * @param releaseId
     *            ID of release to ignore
     */
    public static void ignoreRelease(final Handler messageHandler, final int releaseId) {
        if (executingCommand || !Preferences.isSet(Preferences.COUCHPOTATO_URL)) {
            return;
        }

        Thread thread = new Thread() {

            @Override
            public void run() {

                try {
                    String result = makeApiCall(MESSAGE.RELEASE_IGNORE.toString().toLowerCase(), "id=" + releaseId);
                    JSONObject jsonObject = new JSONObject(result);
                    Log.d(TAG, jsonObject.toString());

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
     * Download a release of a movie
     * 
     * @param messageHandler
     *            Handler
     * @param releaseId
     *            ID of release to download
     */
    public static void downloadRelease(final Handler messageHandler, final int releaseId) {
        if (executingCommand || !Preferences.isSet(Preferences.COUCHPOTATO_URL)) {
            return;
        }

        Thread thread = new Thread() {

            @Override
            public void run() {

                try {
                    String result = makeApiCall(MESSAGE.RELEASE_DOWNLOAD.toString().toLowerCase(), "id=" + releaseId);
                    JSONObject jsonObject = new JSONObject(result);
                    Log.d(TAG, jsonObject.toString());

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
     * Get Profile based on ID key
     * 
     * @param key
     *            Identifier of Profile
     * @param messageHandler
     *            MessageHandler
     * @return Profile
     */
    public synchronized static String getProfile(Integer key, final Handler messageHandler) {
        String result = "";
        if (profiles == null)
            getProfiles(messageHandler);
        else
            result = profiles.get(key);

        if (result == null)
            result = "Unknown";
        return !result.isEmpty() ? result : "Unknown";
    }

    /**
     * Get Status based on ID key
     * 
     * @param key
     *            Identifier of status
     * @param messageHandler
     *            MessageHandler
     * @return Status
     */
    public synchronized static String getStatus(Integer key, final Handler messageHandler) {
        String result = "";
        if (status == null)
            getStatusList(messageHandler);
        result = status.get(key);

        if (result == null)
            result = "Unknown";
        return !result.isEmpty() ? result : "Unknown";
    }

    /**
     * Get Profile based on ID key
     * 
     * @param key
     *            Identifier of Profile
     * @param messageHandler
     *            MessageHandler
     * @return Profile
     */
    public synchronized static String getProfile(Integer key) {
        String result = "";
        if (profiles == null)
            getProfiles();
        else
            result = profiles.get(key);

        if (result == null)
            result = "Unknown";
        return !result.isEmpty() ? result : "Unknown";
    }

    /**
     * Get Status based on ID key
     * 
     * @param key
     *            Identifier of status
     * @param messageHandler
     *            MessageHandler
     * @return Status
     */
    public synchronized static String getStatus(Integer key) {
        String result = "";
        if (status == null)
            getStatusList();
        else
            result = status.get(key);
        ;
        if (result == null)
            result = "Unknown";
        return !result.isEmpty() ? result : "Unknown";
    }

    /**
     * Receive keyset from Profile List
     * 
     * @return Integer keyset from Profiles
     */
    public static HashMap<Integer, String> getAllProfiles() {
        return profiles;
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
        message.what = MESSAGE.UPDATE.hashCode();
        message.obj = text;
        message.sendToTarget();
    }
}
