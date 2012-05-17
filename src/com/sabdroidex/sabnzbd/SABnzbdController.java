package com.sabdroidex.sabnzbd;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.sabdroidex.utils.Preferences;
import com.utils.HttpUtil;

public final class SABnzbdController {
    
    private static boolean executingCommand = false;
    private static boolean executingRefreshHistory = false;
    private static boolean executingRefreshQuery = false;
    
    public static boolean paused = false;
    
    private static final String URL_TEMPLATE = "[SERVER_URL]/api?mode=[COMMAND]&output=json";
    
    public static enum MESSAGE {
        ADDURL, HISTORY, PAUSE, QUEUE, REMOVE, RESUME, UPDATE;
    }
    
    /**
     * 
     * @param messageHandler
     * @param value
     */
    public static void addByURL(final Handler messageHandler, final String value) {
        // Already running or settings not ready
        if (executingCommand || !Preferences.isSet(Preferences.SERVER_URL))
            return;
        
        Thread thread = new Thread() {
            
            @Override
            public void run() {
                try {
                    makeApiCall(MESSAGE.ADDURL.toString().toLowerCase(), "name=" + value);
                }
                catch (Throwable e) {
                    Log.w("ERROR", " " + e.getLocalizedMessage());
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
     * 
     * @return
     */
    private static String getPreferencesParams() {
        String username = Preferences.get(Preferences.SERVER_USERNAME);
        String password = Preferences.get(Preferences.SERVER_PASSWORD);
        
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
     * This functions handle the API calls to Sabnzbd to define the URL and
     * parameters
     * 
     * @param command
     *            The type of command that will be sent to Sabnzbd
     * @return The result of the API call
     * @throws RuntimeException
     *             Thrown if there is any unexpected problem during the
     *             communication with the server
     */
    public static String makeApiCall(String command) throws RuntimeException {
        return makeApiCall(command, "");
    }
    
    /**
     * This functions handle the API calls to Sabnzbd to define the URL and
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
    public static String makeApiCall(String command, String... extraParams) throws RuntimeException {
        
        String url = getFormattedUrl();
        
        /**
         * Checking if there is an API Key from Sabnzbd to concatenate to the
         * URL
         */
        String apiKey = Preferences.get(Preferences.SERVER_API_KEY);
        if (!apiKey.trim().equals("")) {
            url = url + "&apikey=" + apiKey;
        }
        
        url = url.replace("[COMMAND]", command);
        url = url + getPreferencesParams();
        
        for (String xTraParam : extraParams) {
            if (xTraParam != null && !xTraParam.trim().equals("")) {
                url = url + "&" + xTraParam;
            }
        }
        
        String result = new String(HttpUtil.getInstance().getDataAsCharArray(url));
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
        if ("".equals(Preferences.get(Preferences.SERVER_PORT))) {
            url = url.replace("[SERVER_URL]", Preferences.get(Preferences.SERVER_URL));
        }
        else {
            url = url.replace("[SERVER_URL]", Preferences.get(Preferences.SERVER_URL) + ":" + Preferences.get(Preferences.SERVER_PORT));
        }
        
        if (!url.toUpperCase().startsWith("HTTP://") && !url.toUpperCase().startsWith("HTTPS://")) {
            if (Preferences.isEnabled(Preferences.SERVER_SSL)) {
                url = "https://" + url;
            }
            else {
                url = "http://" + url;
            }
        }
        
        return url;
    }
    
    /**
     * Pauses or resumes a queue item depending on the current status
     * 
     * @param messageHandler
     * @param item
     */
    public static void pauseResumeItem(final Handler messageHandler, final Object[] item) {
        
        // Already running or settings not ready
        if (executingCommand || !Preferences.isSet(Preferences.SERVER_URL))
            return;
        
        Thread thread = new Thread() {
            
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
                catch (Throwable e) {
                    Log.w("ERROR", " " + e.getLocalizedMessage());
                }
                finally {
                    executingCommand = false;
                    sendUpdateMessageStatus(messageHandler, "");
                }
            }
        };
        executingCommand = true;
        
        if (paused)
            sendUpdateMessageStatus(messageHandler, MESSAGE.RESUME.toString());
        else
            sendUpdateMessageStatus(messageHandler, MESSAGE.PAUSE.toString());
        
        thread.start();
    }
    
    /**
     * Pauses or resumes the queue depending on the current status
     * 
     * @param messageHandler
     */
    public static void pauseResumeQueue(final Handler messageHandler) {
        // Already running or settings not ready
        if (executingCommand || !Preferences.isSet(Preferences.SERVER_URL))
            return;
        
        Thread thread = new Thread() {
            
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
                catch (Throwable e) {
                    Log.w("ERROR", e.getLocalizedMessage());
                }
                finally {
                    executingCommand = false;
                    sendUpdateMessageStatus(messageHandler, "");
                }
            }
        };
        executingCommand = true;
        
        if (paused)
            sendUpdateMessageStatus(messageHandler, MESSAGE.RESUME.toString());
        else
            sendUpdateMessageStatus(messageHandler, MESSAGE.PAUSE.toString());
        
        thread.start();
    }
    
    /**
     * 
     * @param messageHandler
     */
    public static void refreshHistory(final Handler messageHandler) {
        // Already running or settings not ready
        if (executingRefreshHistory || !Preferences.isSet(Preferences.SERVER_URL))
            return;
        Thread thread = new Thread() {
            
            @Override
            public void run() {
                
                try {
                    Object results[] = new Object[2];
                    
                    String queueData = makeApiCall(MESSAGE.HISTORY.toString().toLowerCase());
                    ArrayList<Object[]> rows = new ArrayList<Object[]>();
                    
                    JSONObject jsonObject = new JSONObject(queueData);
                    
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
                        
                        JSONArray jobs = jsonObject.getJSONArray("slots");
                        rows.clear();
                        
                        for (int i = 0; i < jobs.length(); i++) {
                            Object[] rowValues = new Object[5];
                            rowValues[0] = jobs.getJSONObject(i).getString("name");
                            rowValues[1] = jobs.getJSONObject(i).getString("size");
                            rowValues[2] = jobs.getJSONObject(i).getString("status");
                            rowValues[3] = jobs.getJSONObject(i).getString("nzo_id");
                            rowValues[4] = jobs.getJSONObject(i).getString("fail_message");
                            rows.add(rowValues);
                        }
                        
                        results[1] = rows;
                        
                        Message message = new Message();
                        message.setTarget(messageHandler);
                        message.what = MESSAGE.HISTORY.ordinal();
                        message.obj = results;
                        message.sendToTarget();
                    }
                }
                catch (RuntimeException e) {
                    Log.w("ERROR", " " + e.getLocalizedMessage());
                }
                catch (Throwable e) {
                    Log.w("ERROR", " " + e.getLocalizedMessage());
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
     * 
     * @param messageHandler
     */
    public static void refreshQueue(final Handler messageHandler) {
        
        // Already running or settings not ready
        if (executingRefreshQuery || !Preferences.isSet(Preferences.SERVER_URL))
            return;
        
        Thread thread = new Thread() {
            
            @Override
            public void run() {
                
                try {
                    Object results[] = new Object[2];
                    
                    String queueData = makeApiCall(MESSAGE.QUEUE.toString().toLowerCase());
                    ArrayList<Object[]> rows = new ArrayList<Object[]>();
                    
                    JSONObject jsonObject = new JSONObject(queueData);
                    
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
                        
                        JSONArray jobs = jsonObject.getJSONArray("slots");
                        rows.clear();
                        
                        for (int i = 0; i < jobs.length(); i++) {
                            Object[] rowValues = new Object[6];
                            rowValues[0] = jobs.getJSONObject(i).getString("filename");
                            rowValues[1] = jobs.getJSONObject(i).getDouble("mb");
                            rowValues[2] = jobs.getJSONObject(i).getDouble("mbleft");
                            rowValues[3] = jobs.getJSONObject(i).getString("status");
                            rowValues[4] = jobs.getJSONObject(i).getString("nzo_id");
                            rowValues[5] = jobs.getJSONObject(i).getString("timeleft");
                            rows.add(rowValues);
                        }
                        
                        results[1] = rows;
                        
                        Message message = new Message();
                        message.setTarget(messageHandler);
                        message.what = MESSAGE.QUEUE.ordinal();
                        message.obj = results;
                        message.sendToTarget();
                    }
                }
                catch (RuntimeException e) {
                    Log.w("ERROR", " " + e.getLocalizedMessage());
                }
                catch (Throwable e) {
                    Log.w("ERROR", " " + e.getLocalizedMessage());
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
     * @param item
     */
    public static void removeHistoryItem(final Handler messageHandler, final Object[] item) {
        
        // Already running or settings not ready
        if (executingCommand || !Preferences.isSet(Preferences.SERVER_URL))
            return;
        
        Thread thread = new Thread() {
            
            @Override
            public void run() {
                
                try {
                    makeApiCall(MESSAGE.HISTORY.toString().toLowerCase(), "name=delete", "value=" + item[3]);
                    Thread.sleep(250);
                    SABnzbdController.refreshHistory(messageHandler);
                }
                catch (Throwable e) {
                    Log.w("ERROR", " " + e.getLocalizedMessage());
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
     * @param item
     */
    public static void removeQueueItem(final Handler messageHandler, final Object[] item) {
        
        // Already running or settings not ready
        if (executingCommand || !Preferences.isSet(Preferences.SERVER_URL))
            return;
        
        Thread thread = new Thread() {
            
            @Override
            public void run() {
                
                try {
                    makeApiCall(MESSAGE.QUEUE.toString().toLowerCase(), "name=delete", "value=" + item[4]);
                    Thread.sleep(250);
                    SABnzbdController.refreshQueue(messageHandler);
                }
                catch (Throwable e) {
                    Log.w("ERROR", " " + e.getLocalizedMessage());
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
    private static void sendUpdateMessageStatus(Handler messageHandler, String text) {
        
        Message message = new Message();
        message.setTarget(messageHandler);
        message.what = MESSAGE.UPDATE.ordinal();
        message.obj = text;
        message.sendToTarget();
    }
}
