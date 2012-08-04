package com.sabdroidex.sabnzbd;

import java.net.URLEncoder;
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

/**
 * @author Marc
 * 
 */
public final class SABnzbdController {

    public static enum MESSAGE {
        ADDFILE, ADDURL, HISTORY, PAUSE, QUEUE, REMOVE, RESUME, CONFIG, SET_CONFIG, GET_CONFIG, UPDATE;
    }

    private static final String TAG = "SABnzbdController";
    private static boolean executingCommand = false;
    private static boolean executingRefreshHistory = false;

    private static boolean executingRefreshQuery = false;

    public static boolean paused = false;

    private static final String URL_TEMPLATE = "[SABNZBD_URL]/[SABNZBD_URL_EXTENTION]api?mode=[COMMAND]&output=json";

    /**
     * This function sends a Nzb URL to Sabnzbd to add to the queue.
     * 
     * @param messageHandler
     *            The class that will handle the result message.
     * @param value
     *            The URL to sent to the Sabnzbd server.
     */
    public static void addByURL(final Handler messageHandler, final String value) {
        // Already running or settings not ready
        if (executingCommand || !Preferences.isSet(Preferences.SABNZBD_URL)) {
            return;
        }

        final Thread thread = new Thread() {

            @Override
            public void run() {
                try {
                    makeApiCall(MESSAGE.ADDURL.toString().toLowerCase(), "name=" + value);
                }
                catch (final Throwable e) {
                    Log.w(TAG, " " + e.getLocalizedMessage());
                }
                finally {
                    sendUpdateMessageStatus(messageHandler, "");
                }
            }
        };

        sendUpdateMessageStatus(messageHandler, MESSAGE.ADDURL.toString());
        thread.start();
    }

    /**
     * This method sends a Nzb file to Sabnzbd to add to the queue.
     * 
     * @param messageHandler
     *            The class that will handle the result message.
     * @param value
     *            The URL to sent to the Sabnzbd server.
     */
    public static void addFile(final Handler messageHandler, final String name, final char[] file) {
        // Already running or settings not ready
        if (executingCommand || !Preferences.isSet(Preferences.SABNZBD_URL)) {
            return;
        }

        final Thread thread = new Thread() {

            @Override
            public void run() {
                try {
                    makePostApiCall(MESSAGE.ADDFILE.toString().toLowerCase(), "application/x-nzb", URLEncoder.encode(name, "UTF-8"), file, "name=" + URLEncoder.encode(name, "UTF-8"));
                }
                catch (final Throwable e) {
                    Log.w(TAG, " " + e.getLocalizedMessage());
                }
                finally {
                    sendUpdateMessageStatus(messageHandler, "");
                }
            }
        };

        sendUpdateMessageStatus(messageHandler, MESSAGE.ADDFILE.toString());
        thread.start();
    }

    /**
     * Gets all the configurations on the Sabnzbd Server
     * 
     * @param messageHandler
     *            The class that will handle the result message.
     * @param item
     *            An array that contains the configuration section, the configuration name and the new value.
     */
    public static void getAllConfigs(final Handler messageHandler, final Object[] item) {

        // Already running or settings not ready
        if (executingCommand || !Preferences.isSet(Preferences.SABNZBD_URL)) {
            return;
        }

        final Thread thread = new Thread() {

            @Override
            public void run() {

                try {
                    final Object results[] = new Object[2];

                    final String result = makeApiCall(MESSAGE.GET_CONFIG.toString().toLowerCase());
                    final Object[] elements = new Object[7];

                    JSONObject jsonObject = new JSONObject(result);

                    if (!jsonObject.isNull("error")) {
                        sendUpdateMessageStatus(messageHandler, "SABnzbd : " + jsonObject.getString("error"));
                    }
                    else {
                        jsonObject = jsonObject.getJSONObject("config");
                    }

                    jsonObject = jsonObject.getJSONObject("misc");

                    results[0] = jsonObject;

                    elements[0] = jsonObject.get("bandwidth_limit");
                    elements[1] = jsonObject.get("cache_dir");
                    elements[2] = jsonObject.get("cache_limit");
                    elements[3] = jsonObject.get("dirscan_dir");
                    elements[4] = jsonObject.get("dirscan_speed");
                    elements[5] = jsonObject.get("download_dir");
                    elements[6] = jsonObject.get("complete_dir");

                    results[1] = elements;

                    final Message message = new Message();
                    message.setTarget(messageHandler);
                    message.what = MESSAGE.GET_CONFIG.ordinal();
                    message.obj = results;
                    message.sendToTarget();
                }
                catch (final Throwable e) {
                    Log.w(TAG, " " + e.getLocalizedMessage());
                }
                finally {
                    executingCommand = false;
                    sendUpdateMessageStatus(messageHandler, "");
                }
            }
        };

        executingCommand = true;
        sendUpdateMessageStatus(messageHandler, "");
        thread.start();
    }

    /**
     * Gets a specific configuration on the Sabnzbd Server
     * 
     * @param messageHandler
     *            The class that will handle the result message.
     * @param item
     *            An array that contains the configuration section, the configuration name and the new value.
     */
    public static void getConfig(final Handler messageHandler, final Object[] item) {

        // Already running or settings not ready
        if (executingCommand || !Preferences.isSet(Preferences.SABNZBD_URL)) {
            return;
        }

        final Thread thread = new Thread() {

            @Override
            public void run() {

                try {
                    final String results = makeApiCall(MESSAGE.GET_CONFIG.toString().toLowerCase(), "section=" + item[0], "keyword=" + item[1]);

                    final Message message = new Message();
                    message.setTarget(messageHandler);
                    message.what = MESSAGE.GET_CONFIG.ordinal();
                    message.obj = results;
                    message.sendToTarget();
                }
                catch (final Throwable e) {
                    Log.w(TAG, " " + e.getLocalizedMessage());
                }
                finally {
                    executingCommand = false;
                    sendUpdateMessageStatus(messageHandler, "");
                }
            }
        };

        executingCommand = true;
        sendUpdateMessageStatus(messageHandler, "");
        thread.start();
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
        if ("".equals(Preferences.get(Preferences.SABNZBD_PORT))) {
            url = url.replace("[SABNZBD_URL]", Preferences.get(Preferences.SABNZBD_URL));
        }
        else {
            url = url.replace("[SABNZBD_URL]", Preferences.get(Preferences.SABNZBD_URL) + ":" + Preferences.get(Preferences.SABNZBD_PORT));
        }

        /**
         * Checking the URL extension
         */
        if ("".equals(Preferences.get(Preferences.SICKBEARD_URL_EXTENTION))) {
            url = url.replace("[SABNZBD_URL_EXTENTION]", Preferences.get(Preferences.SICKBEARD_URL_EXTENTION));
        }
        else {
            url = url.replace("[SABNZBD_URL_EXTENTION]", Preferences.get(Preferences.SICKBEARD_URL_EXTENTION) + "/");
        }

        if (!url.toUpperCase().startsWith("HTTP://") && !url.toUpperCase().startsWith("HTTPS://")) {
            if (Preferences.isEnabled(Preferences.SABNZBD_SSL)) {
                url = "https://" + url;
            }
            else {
                url = "http://" + url;
            }
        }

        /**
         * Checking if there is an API Key from Sabnzbd to concatenate to the URL
         */
        final String apiKey = Preferences.get(Preferences.SABNZBD_API_KEY);
        if (!apiKey.trim().equals("")) {
            url = url + "&apikey=" + apiKey;
        }

        return url;
    }

    /**
     * This function gets the Sabnzbd user name and password in an usable URL format.
     * 
     * @return The credentials used to connect to Sabnzbd
     */
    private static String getPreferencesParams() {
        final String username = Preferences.get(Preferences.SABNZBD_USERNAME);
        final String password = Preferences.get(Preferences.SABNZBD_PASSWORD);

        String credentials = "";
        if (username != null && !"".equals(username)) {
            credentials += "&ma_username=" + username;
        }
        if (password != null && !"".equals(password)) {
            credentials += "&ma_password=" + password;
        }
        return credentials;
    }

    /**
     * This function handle the API calls to Sabnzbd to define the URL and parameters
     * 
     * @param command
     *            The type of command that will be sent to Sabnzbd
     * @return The result of the API call
     * @throws RuntimeException
     *             Thrown if there is any unexpected problem during the communication with the server
     */
    public static String makeApiCall(final String command) throws RuntimeException {
        return makeApiCall(command, "");
    }

    /**
     * This function handle the API calls to Sabnzbd to define the URL and parameters
     * 
     * @param command
     *            The type of command that will be sent to Sabnzbd
     * @param extraParams
     *            Any parameter that will have to be part of the URL
     * @return The result of the API call
     * @throws RuntimeException
     *             Thrown if there is any unexpected problem during the communication with the server
     */
    public static String makeApiCall(final String command, final String... extraParams) throws RuntimeException {

        String url = getFormattedUrl();
        Map<String, String> parameterMap = getAdditionalParameters();

        url = url.replace("[COMMAND]", command);
        url = url + getPreferencesParams();

        for (final String xTraParam : extraParams) {
            if (xTraParam != null && !xTraParam.trim().equals("")) {
                url = url + "&" + xTraParam;
            }
        }

        final String result = new String(HttpUtil.getInstance().getDataAsCharArray(url,parameterMap));
        return result;
    }

    private static Map<String, String> getAdditionalParameters() {
    	HashMap<String, String> parameterMap = new HashMap<String, String>();
    	
        if(Preferences.isEnabled(Preferences.APACHE)){
        	String apache_auth = Preferences.get(Preferences.APACHE_USERNAME)+":"+Preferences.get(Preferences.APACHE_PASSWORD);
        	String encoding = new String(Base64.encode(apache_auth.getBytes(), Base64.NO_WRAP));
        	parameterMap.put("Authorization", "Basic " + encoding);
        }
    	
    	return parameterMap;
	}

	/**
     * This function handle the API calls to Sabnzbd to define the URL and parameters
     * 
     * 
     * @param command
     *            The type of command that will be sent to Sabnzbd
     * @param contentType
     *            The type of the content that will be sent (application/x-nzb or else).
     * @param content
     *            The content that will be sent. The type should match what is sent in contentType.
     * @param extraParams
     *            Any parameter that will have to be part of the URL
     * @return The result of the API call
     * @throws RuntimeException
     *             Thrown if there is any unexpected problem during the communication with the server
     */
    public static String makePostApiCall(final String command, final String contentType, String contentName, final char[] content, final String... extraParams)
            throws RuntimeException {

        String url = getFormattedUrl();

        url = url.replace("[COMMAND]", command);
        url = url + getPreferencesParams();

        for (final String xTraParam : extraParams) {
            if (xTraParam != null && !xTraParam.trim().equals("")) {
                url = url + "&" + xTraParam;
            }
        }

        final String result = new String(HttpUtil.getInstance().postDataAsCharArray(url, contentType, contentName, content));
        return result;
    }

    /**
     * Pauses or resumes a queue item depending on the current status
     * 
     * @param messageHandler
     * @param item
     */
    public static void pauseResumeItem(final Handler messageHandler, final Object[] item) {

        // Already running or settings not ready
        if (executingCommand || !Preferences.isSet(Preferences.SABNZBD_URL)) {
            return;
        }

        final Thread thread = new Thread() {

            @Override
            public void run() {
                try {
                    if ("Paused".equals(item[3])) {
                        makeApiCall(MESSAGE.QUEUE.toString().toLowerCase(), "name=resume", "value=" + item[4]);
                    }
                    else {
                        makeApiCall(MESSAGE.QUEUE.toString().toLowerCase(), "name=pause", "value=" + item[4]);
                    }
                    Thread.sleep(100);
                    SABnzbdController.refreshQueue(messageHandler);
                }
                catch (final Throwable e) {
                    Log.w(TAG, " " + e.getLocalizedMessage());
                }
                finally {
                    executingCommand = false;
                    sendUpdateMessageStatus(messageHandler, "");
                }
            }
        };
        executingCommand = true;

        if (paused) {
            sendUpdateMessageStatus(messageHandler, MESSAGE.RESUME.toString());
        }
        else {
            sendUpdateMessageStatus(messageHandler, MESSAGE.PAUSE.toString());
        }

        thread.start();
    }

    /**
     * Pauses or resumes the queue depending on the current status
     * 
     * @param messageHandler
     */
    public static void pauseResumeQueue(final Handler messageHandler) {
        // Already running or settings not ready
        if (executingCommand || !Preferences.isSet(Preferences.SABNZBD_URL)) {
            return;
        }

        final Thread thread = new Thread() {

            @Override
            public void run() {
                try {
                    if (paused) {
                        makeApiCall(MESSAGE.RESUME.toString().toLowerCase());
                    }
                    else {
                        makeApiCall(MESSAGE.PAUSE.toString().toLowerCase());
                    }
                    Thread.sleep(100);
                    SABnzbdController.refreshQueue(messageHandler);
                }
                catch (final Throwable e) {
                    Log.w(TAG, e.getLocalizedMessage());
                }
                finally {
                    executingCommand = false;
                    sendUpdateMessageStatus(messageHandler, "");
                }
            }
        };
        executingCommand = true;

        if (paused) {
            sendUpdateMessageStatus(messageHandler, MESSAGE.RESUME.toString());
        }
        else {
            sendUpdateMessageStatus(messageHandler, MESSAGE.PAUSE.toString());
        }

        thread.start();
    }

    /**
     * This function refreshes the elements from the history.
     * 
     * @param messageHandler
     *            The class that will handle the result message
     */
    public static void refreshHistory(final Handler messageHandler) {
        // Already running or settings not ready
        if (executingRefreshHistory || !Preferences.isSet(Preferences.SABNZBD_URL)) {
            return;
        }
        final Thread thread = new Thread() {

            @Override
            public void run() {

                try {
                    final Object results[] = new Object[2];

                    final String result = makeApiCall(MESSAGE.HISTORY.toString().toLowerCase());
                    final ArrayList<Object[]> rows = new ArrayList<Object[]>();

                    JSONObject jsonObject = new JSONObject(result);

                    if (!jsonObject.isNull("error")) {
                        sendUpdateMessageStatus(messageHandler, "SABnzbd : " + jsonObject.getString("error"));
                    }
                    else {
                        jsonObject = jsonObject.getJSONObject("history");

                        if (jsonObject.get("paused") == null) {
                            paused = false;
                        }
                        else {
                            // Due to a bug(?) on sabnzbd right after a restart this
                            // field is "null" as a string
                            // parseBoolean should take care of that since anything
                            // but "true" is considered false
                            paused = Boolean.parseBoolean(jsonObject.getString("paused"));
                        }

                        results[0] = jsonObject;

                        final JSONArray jobs = jsonObject.getJSONArray("slots");
                        rows.clear();

                        for (int i = 0; i < jobs.length(); i++) {
                            final Object[] rowValues = new Object[5];
                            rowValues[0] = jobs.getJSONObject(i).getString("name");
                            rowValues[1] = jobs.getJSONObject(i).getString("size");
                            rowValues[2] = jobs.getJSONObject(i).getString("status");
                            rowValues[3] = jobs.getJSONObject(i).getString("nzo_id");
                            rowValues[4] = jobs.getJSONObject(i).getString("fail_message");
                            rows.add(rowValues);
                        }

                        results[1] = rows;

                        final Message message = new Message();
                        message.setTarget(messageHandler);
                        message.what = MESSAGE.HISTORY.ordinal();
                        message.obj = results;
                        message.sendToTarget();
                    }
                }
                catch (final RuntimeException e) {
                    Log.w(TAG, " " + e.getLocalizedMessage());
                }
                catch (final Throwable e) {
                    Log.w(TAG, " " + e.getLocalizedMessage());
                }
                finally {
                    executingRefreshHistory = false;
                    sendUpdateMessageStatus(messageHandler, "");
                }
            }
        };

        executingRefreshHistory = true;
        sendUpdateMessageStatus(messageHandler, "");
        thread.start();
    }

    /**
     * This function refreshes the elements from the queue.
     * 
     * @param messageHandler
     *            The class that will handle the result message
     */
    public static void refreshQueue(final Handler messageHandler) {

        // Already running or settings not ready
        if (executingRefreshQuery || !Preferences.isSet(Preferences.SABNZBD_URL)) {
            return;
        }

        final Thread thread = new Thread() {

            @Override
            public void run() {

                try {
                    final Object results[] = new Object[2];

                    final String result = makeApiCall(MESSAGE.QUEUE.toString().toLowerCase());
                    final ArrayList<Object[]> rows = new ArrayList<Object[]>();

                    JSONObject jsonObject = new JSONObject(result);

                    if (!jsonObject.isNull("error")) {
                        sendUpdateMessageStatus(messageHandler, "SABnzbd : " + jsonObject.getString("error"));
                    }
                    else {
                        jsonObject = jsonObject.getJSONObject("queue");

                        if (jsonObject.get("paused") == null) {
                            paused = false;
                        }
                        else {
                            // Due to a bug(?) on sabnzbd right after a restart this
                            // field is "null" as a string
                            // parseBoolean should take care of that since anything
                            // but "true" is considered false
                            paused = Boolean.parseBoolean(jsonObject.getString("paused"));
                        }

                        results[0] = jsonObject;

                        final JSONArray jobs = jsonObject.getJSONArray("slots");
                        rows.clear();

                        for (int i = 0; i < jobs.length(); i++) {
                            final Object[] rowValues = new Object[6];
                            rowValues[0] = jobs.getJSONObject(i).getString("filename");
                            rowValues[1] = jobs.getJSONObject(i).getDouble("mb");
                            rowValues[2] = jobs.getJSONObject(i).getDouble("mbleft");
                            rowValues[3] = jobs.getJSONObject(i).getString("status");
                            rowValues[4] = jobs.getJSONObject(i).getString("nzo_id");
                            rowValues[5] = jobs.getJSONObject(i).getString("timeleft");
                            rows.add(rowValues);
                        }

                        results[1] = rows;

                        final Message message = new Message();
                        message.setTarget(messageHandler);
                        message.what = MESSAGE.QUEUE.ordinal();
                        message.obj = results;
                        message.sendToTarget();
                    }
                }
                catch (final RuntimeException e) {
                    Log.w(TAG, " " + e.getLocalizedMessage());
                }
                catch (final Throwable e) {
                    Log.w(TAG, " " + e.getLocalizedMessage());
                }
                finally {
                    executingRefreshQuery = false;
                    sendUpdateMessageStatus(messageHandler, "");
                }
            }
        };

        executingRefreshQuery = true;
        sendUpdateMessageStatus(messageHandler, "");
        thread.start();
    }

    /**
     * Removes a history item
     * 
     * @param messageHandler
     *            The class that will handle the result message.
     * @param item
     *            The item id to remove from the history.
     */
    public static void removeHistoryItem(final Handler messageHandler, final Object[] item) {

        // Already running or settings not ready
        if (executingCommand || !Preferences.isSet(Preferences.SABNZBD_URL)) {
            return;
        }

        final Thread thread = new Thread() {

            @Override
            public void run() {

                try {
                    makeApiCall(MESSAGE.HISTORY.toString().toLowerCase(), "name=delete", "value=" + item[3]);
                    Thread.sleep(250);
                    SABnzbdController.refreshHistory(messageHandler);
                }
                catch (final Throwable e) {
                    Log.w(TAG, " " + e.getLocalizedMessage());
                }
                finally {
                    executingCommand = false;
                    sendUpdateMessageStatus(messageHandler, "");
                }
            }
        };

        executingCommand = true;
        sendUpdateMessageStatus(messageHandler, "");
        thread.start();
    }

    /**
     * Removes a queue item
     * 
     * @param messageHandler
     *            The class that will handle the result message.
     * @param item
     *            The item id to remove from the queue.
     */
    public static void removeQueueItem(final Handler messageHandler, final Object[] item) {

        // Already running or settings not ready
        if (executingCommand || !Preferences.isSet(Preferences.SABNZBD_URL)) {
            return;
        }

        final Thread thread = new Thread() {

            @Override
            public void run() {

                try {
                    makeApiCall(MESSAGE.QUEUE.toString().toLowerCase(), "name=delete", "value=" + item[4]);
                    Thread.sleep(250);
                    SABnzbdController.refreshQueue(messageHandler);
                }
                catch (final Throwable e) {
                    Log.w(TAG, " " + e.getLocalizedMessage());
                }
                finally {
                    executingCommand = false;
                    sendUpdateMessageStatus(messageHandler, "");
                }
            }
        };
        executingCommand = true;
        sendUpdateMessageStatus(messageHandler, "");
        thread.start();
    }

    /**
     * Sends a message to the calling {@link Activity} to update it's status bar
     * 
     * @param messageHandler
     *            The message handler to be notified
     * @param text
     *            The text to write in the message
     */
    private static void sendUpdateMessageStatus(final Handler messageHandler, final String text) {

        final Message message = new Message();
        message.setTarget(messageHandler);
        message.what = MESSAGE.UPDATE.ordinal();
        message.obj = text;
        message.sendToTarget();
    }

    /**
     * Sets a specific configuration on the Sabnzbd Server
     * 
     * @param messageHandler
     *            The class that will handle the result message.
     * @param item
     *            An array that contains the configuration section, the configuration name and the new value.
     */
    public static void setConfig(final Handler messageHandler, final Object[] item) {

        // Already running or settings not ready
        if (executingCommand || !Preferences.isSet(Preferences.SABNZBD_URL)) {
            return;
        }

        final Thread thread = new Thread() {

            @Override
            public void run() {

                try {
                    makeApiCall(MESSAGE.SET_CONFIG.toString().toLowerCase(), "section=" + item[0], "keyword=" + item[1], "value=" + item[2]);
                }
                catch (final Throwable e) {
                    Log.w(TAG, " " + e.getLocalizedMessage());
                }
                finally {
                    executingCommand = false;
                    sendUpdateMessageStatus(messageHandler, "");
                }
            }
        };

        executingCommand = true;
        sendUpdateMessageStatus(messageHandler, "");
        thread.start();
    }
}
