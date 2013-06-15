package com.sabdroidex.controllers.sabnzbd;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.sabdroidex.controllers.SABController;
import com.sabdroidex.data.sabnzbd.History;
import com.sabdroidex.data.sabnzbd.Queue;
import com.sabdroidex.data.sabnzbd.QueueElement;
import com.sabdroidex.data.sabnzbd.SabnzbdConfig;
import com.sabdroidex.utils.Preferences;
import com.sabdroidex.utils.json.SimpleJsonMarshaller;
import com.utils.ApacheCredentialProvider;
import com.utils.HttpUtil;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;

/**
 * @author Marc
 * 
 */
public final class SABnzbdController extends SABController {
    
    public static enum MESSAGE {
        ADDFILE, ADDURL, HISTORY, PAUSE, QUEUE, REMOVE, RESUME, CONFIG, SET_CONFIG, GET_CONFIG, UPDATE
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
                    Log.e(TAG, " " + e.getLocalizedMessage());
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
     * @param name Defines the file name that was read.
     * @param file Is the read file in a char[] format.
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
                    makePostApiCall(MESSAGE.ADDFILE.toString().toLowerCase(), "application/x-nzb", URLEncoder.encode(name, "UTF-8"), file, "name=" + URLEncoder.encode(name, "UTF-8"), "cat=", "priority=");
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
     */
    public static void getAllConfigs(final Handler messageHandler) {
        
        // Already running or settings not ready
        if (executingCommand || !Preferences.isSet(Preferences.SABNZBD_URL)) {
            return;
        }
        
        final Thread thread = new Thread() {
            
            @Override
            public void run() {
                
                try {
                    final String result = makeApiCall(MESSAGE.GET_CONFIG.toString().toLowerCase());
                    JSONObject jsonObject = new JSONObject(result);
                    
                    if (!jsonObject.isNull("error")) {
                        sendUpdateMessageStatus(messageHandler, "SABnzbd : " + jsonObject.getString("error"));
                    }
                    else {
                        jsonObject = jsonObject.getJSONObject("config");
                        
                        SimpleJsonMarshaller jsonMarshaller = new SimpleJsonMarshaller(SabnzbdConfig.class);
                        SabnzbdConfig config = (SabnzbdConfig) jsonMarshaller.unmarshal(jsonObject);
                        
                        final Message message = new Message();
                        message.setTarget(messageHandler);
                        message.what = MESSAGE.GET_CONFIG.hashCode();
                        message.obj = config;
                        message.sendToTarget();
                    }
                }
                catch (final Throwable e) {
                    Log.e(TAG, " " + e.getLocalizedMessage());
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
     *            An array that contains the configuration section, the
     *            configuration name and the new value.
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
                    message.what = MESSAGE.GET_CONFIG.hashCode();
                    message.obj = results;
                    message.sendToTarget();
                }
                catch (final Throwable e) {
                    Log.e(TAG, " " + e.getLocalizedMessage());
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
        if ("".equals(Preferences.get(Preferences.SABNZBD_URL_EXTENTION))) {
            url = url.replace("[SABNZBD_URL_EXTENTION]", Preferences.get(Preferences.SABNZBD_URL_EXTENTION));
        }
        else {
            url = url.replace("[SABNZBD_URL_EXTENTION]", Preferences.get(Preferences.SABNZBD_URL_EXTENTION) + "/");
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
         * Checking if there is an API Key from Sabnzbd to concatenate to the
         * URL
         */
        final String apiKey = Preferences.get(Preferences.SABNZBD_API_KEY);
        if (!apiKey.trim().equals("")) {
            url = url + "&apikey=" + apiKey;
        }
        
        return url;
    }
    
    /**
     * This function gets the Sabnzbd user name and password in an usable URL
     * format.
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
    public static String makeApiCall(final String command) throws Exception {
        return makeApiCall(command, "");
    }
    
    /**
     * This function handle the API calls to Sabnzbd to define the URL and
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
    public static String makeApiCall(final String command, final String... extraParams) throws Exception {
        
        String url = getFormattedUrl();
        
        url = url.replace("[COMMAND]", command);
        url = url + getPreferencesParams();
        
        for (final String xTraParam : extraParams) {
            if (xTraParam != null && !xTraParam.trim().equals("")) {
                url = url + "&" + xTraParam;
            }
        }
        
        final String result = new String(HttpUtil.getInstance().getDataAsCharArray(url, ApacheCredentialProvider.getCredentialsProvider()));
        return result;
    }
    
    /**
     * This function handle the API calls to Sabnzbd to define the URL and
     * parameters
     * 
     * 
     * @param command
     *            The type of command that will be sent to Sabnzbd
     * @param contentType
     *            The type of the content that will be sent (application/x-nzb
     *            or else).
     * @param content
     *            The content that will be sent. The type should match what is
     *            sent in contentType.
     * @param extraParams
     *            Any parameter that will have to be part of the URL
     * @return The result of the API call
     * @throws RuntimeException
     *             Thrown if there is any unexpected problem during the
     *             communication with the server
     */
    public static String makePostApiCall(final String command, final String contentType, String contentName, final char[] content, final String... extraParams) throws Exception {
        
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
    public static void pauseResumeItem(final Handler messageHandler, final QueueElement item) {
        
        // Already running or settings not ready
        if (executingCommand || !Preferences.isSet(Preferences.SABNZBD_URL)) {
            return;
        }
        
        final Thread thread = new Thread() {
            
            @Override
            public void run() {
                try {
                    if ("Paused".equals(item.getStatus())) {
                        makeApiCall(MESSAGE.QUEUE.toString().toLowerCase(), "name=resume", "value=" + item.getNzoId());
                    }
                    else {
                        makeApiCall(MESSAGE.QUEUE.toString().toLowerCase(), "name=pause", "value=" + item.getNzoId());
                    }
                    Thread.sleep(100);
                    SABnzbdController.refreshQueue(messageHandler);
                }
                catch (final Throwable e) {
                    Log.e(TAG, " " + e.getLocalizedMessage());
                }
                finally {
                    executingCommand = false;
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
                }
                catch (final Throwable e) {
                    Log.e(TAG, e.getLocalizedMessage());
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
                    final String result = makeApiCall(MESSAGE.HISTORY.toString().toLowerCase());
                    JSONObject jsonObject = new JSONObject(result);
                    
                    if (!jsonObject.isNull("error")) {
                        sendUpdateMessageStatus(messageHandler, "SABnzbd : " + jsonObject.getString("error"));
                    }
                    else {
                        jsonObject = jsonObject.getJSONObject("history");
                        SimpleJsonMarshaller jsonMarshaller = new SimpleJsonMarshaller(History.class);
                        History history = (History) jsonMarshaller.unmarshal(jsonObject);
                        
                        final Message message = new Message();
                        message.setTarget(messageHandler);
                        message.what = MESSAGE.HISTORY.hashCode();
                        message.obj = history;
                        message.sendToTarget();
                    }
                }
                catch (final IOException e) {
                    Log.e(TAG, " " + e.getLocalizedMessage());
                }
                catch (final Throwable e) {
                    Log.e(TAG, " " + e.getLocalizedMessage());
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
                
                String statusMessage = "";
                
                try {
                    final String result = makeApiCall(MESSAGE.QUEUE.toString().toLowerCase());
                    JSONObject jsonObject = new JSONObject(result);
                    
                    if (!jsonObject.isNull("error")) {
                        statusMessage = "SABnzbd : " + jsonObject.getString("error");
                    }
                    else {
                        jsonObject = jsonObject.getJSONObject("queue");
                        SimpleJsonMarshaller jsonMarshaller = new SimpleJsonMarshaller(Queue.class);
                        Queue queue = (Queue) jsonMarshaller.unmarshal(jsonObject);
                        paused = queue.getPaused();
                        
                        final Message message = new Message();
                        message.setTarget(messageHandler);
                        message.what = MESSAGE.QUEUE.hashCode();
                        message.obj = queue;
                        message.sendToTarget();
                    }
                }
                catch (final IOException e) {
                    Log.e(TAG, " " + e.getLocalizedMessage());
                    statusMessage = e.getLocalizedMessage();
                }
                catch (final Throwable e) {
                    Log.e(TAG, " " + e.getLocalizedMessage());
                }
                finally {
                    executingRefreshQuery = false;
                    sendUpdateMessageStatus(messageHandler, statusMessage);
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
     *            The item nzo_id to remove from the history.
     */
    public static void removeHistoryItem(final Handler messageHandler, final String item) {
        
        // Already running or settings not ready
        if (executingCommand || !Preferences.isSet(Preferences.SABNZBD_URL)) {
            return;
        }
        
        final Thread thread = new Thread() {
            
            @Override
            public void run() {
                
                try {
                    makeApiCall(MESSAGE.HISTORY.toString().toLowerCase(), "name=delete", "value=" + item);
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
    public static void removeQueueItem(final Handler messageHandler, final QueueElement item) {
        
        // Already running or settings not ready
        if (executingCommand || !Preferences.isSet(Preferences.SABNZBD_URL)) {
            return;
        }
        
        final Thread thread = new Thread() {
            
            @Override
            public void run() {
                
                try {
                    makeApiCall(MESSAGE.QUEUE.toString().toLowerCase(), "name=delete", "value=" + item.getNzoId());
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
     * Sets a specific configuration on the Sabnzbd Server
     * 
     * @param messageHandler
     *            The class that will handle the result message.
     * @param item
     *            An array that contains the configuration section, the
     *            configuration name and the new value.
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
