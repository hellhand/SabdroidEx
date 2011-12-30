package com.sabdroidex.sabnzbd;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.sabdroidex.Preferences;
import com.sabdroidex.util.HttpUtil;
import com.sabdroidex.util.HttpUtil.ServerConnectinoException;

public final class SABnzbdController {

    public static double speed = 0.0;
    public static boolean paused = false;

    public static final int MESSAGE_UPDATE_QUEUE = 1;
    public static final int MESSAGE_UPDATE_HISTORY = 2;
    public static final int MESSAGE_STATUS_UPDATE = 3;

    private static boolean executingRefreshQuery = false;
    private static boolean executingRefreshHistory = false;
    private static boolean executingCommand = false;

    private static final String URL_TEMPLATE = "http://[SERVER_URL]/api?mode=[COMMAND]&output=json";

    public static enum MESSAGE {
        UPDATING, PAUSING, RESUMING, ADDING, REMOVING
    }

    /**
     * Pauses or resumes the queue depending on the current status
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
                        makeApiCall("resume");
                    }
                    else {
                        makeApiCall("pause");
                    }
                    Thread.sleep(100);
                    SABnzbdController.refreshQueue(messageHandler);
                }
                catch (Throwable e) {
                    // Ignore for now
                    // TODO do something different
                }
                finally {
                    executingCommand = false;

                    sendUpdateMessageStatus(messageHandler, "");
                }
            }
        };
        executingCommand = true;

        if (paused)
            sendUpdateMessageStatus(messageHandler, MESSAGE.RESUMING.toString());
        else
            sendUpdateMessageStatus(messageHandler, MESSAGE.PAUSING.toString());

        thread.start();
    }

    /**
     * Pauses or resumes a queue item depending on the current status
     */
    public static void pauseResumeItem(final Handler messageHandler, final String item) {

        // Already running or settings not ready
        if (executingCommand || !Preferences.isSet(Preferences.SERVER_URL))
            return;

        Thread thread = new Thread() {

            @Override
            public void run() {
                String[] values = item.split("#");
                try {
                    if (values[3].equals("Paused")) {
                        makeApiCall("queue", "name=resume", "value=" + values[4]);
                    }
                    else {
                        makeApiCall("queue", "name=pause", "value=" + values[4]);
                    }
                    Thread.sleep(100);
                    SABnzbdController.refreshQueue(messageHandler);
                }
                catch (Throwable e) {
                    // Ignore for now
                    // TODO do something different
                }
                finally {
                    executingCommand = false;

                    sendUpdateMessageStatus(messageHandler, "");
                }
            }
        };
        executingCommand = true;

        if (paused)
            sendUpdateMessageStatus(messageHandler, MESSAGE.RESUMING.toString());
        else
            sendUpdateMessageStatus(messageHandler, MESSAGE.PAUSING.toString());

        thread.start();
    }

    public static void refreshQueue(final Handler messageHandler) {

        // Already running or settings not ready
        if (executingRefreshQuery || !Preferences.isSet(Preferences.SERVER_URL))
            return;

        Thread thread = new Thread() {

            public void run() {

                try {
                    Object results[] = new Object[2];

                    String queueData = makeApiCall("queue");
                    // Removing unnecessary top tag in server answer
                    queueData = queueData.substring(9, queueData.length() - 1);
                    ArrayList<Object> rows = new ArrayList<Object>();

                    JSONObject jsonObject = new JSONObject(queueData);
                    speed = jsonObject.optLong("kbpersec", 0);
                    if (jsonObject.get("paused") == null)
                        paused = false;
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
                        String rowValues = jobs.getJSONObject(i).get("filename").toString();
                        rowValues = rowValues + "#" + jobs.getJSONObject(i).getDouble("mb");
                        rowValues = rowValues + "#" + jobs.getJSONObject(i).getDouble("mbleft");
                        rowValues = rowValues + "#" + jobs.getJSONObject(i).getString("status");
                        rowValues = rowValues + "#" + jobs.getJSONObject(i).getString("nzo_id");
                        rows.add(rowValues);
                    }

                    results[1] = rows;

                    Message message = new Message();
                    message.setTarget(messageHandler);
                    message.what = MESSAGE_UPDATE_QUEUE;
                    message.obj = results;
                    message.sendToTarget();

                    sendUpdateMessageStatus(messageHandler, "");
                }
                catch (ServerConnectinoException e) {
                    Log.w("ERROR", e);
                    sendUpdateMessageStatus(messageHandler, e.getMessage());
                }
                catch (Throwable e) {
                    Log.w("ERROR", e);
                    sendUpdateMessageStatus(messageHandler, "");
                }
                finally {
                    executingRefreshQuery = false;
                }
            }
        };

        executingRefreshQuery = true;

        sendUpdateMessageStatus(messageHandler, MESSAGE.UPDATING.toString());

        thread.start();
    }

    public static void refreshHistory(final Handler messageHandler) {
        // Already running or settings not ready
        if (executingRefreshHistory || !Preferences.isSet(Preferences.SERVER_URL))
            return;
        Thread thread = new Thread() {

            public void run() {

                try {
                    Object results[] = new Object[2];

                    String queueData = makeApiCall("history");
                    // Removing unnecessary top tag in server answer
                    queueData = queueData.substring(11, queueData.length() - 1);
                    ArrayList<Object> rows = new ArrayList<Object>();

                    JSONObject jsonObject = new JSONObject(queueData);
                    speed = jsonObject.optLong("kbpersec", 0);
                    if (jsonObject.get("paused") == null)
                        paused = false;
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
                        String rowValues = jobs.getJSONObject(i).get("name").toString();
                        rowValues = rowValues + "#" + jobs.getJSONObject(i).getString("size");
                        rowValues = rowValues + "#" + jobs.getJSONObject(i).getString("status");
                        rowValues = rowValues + "#" + jobs.getJSONObject(i).getString("nzo_id");
                        rows.add(rowValues);
                    }

                    results[1] = rows;

                    Message message = new Message();
                    message.setTarget(messageHandler);
                    message.what = MESSAGE_UPDATE_HISTORY;
                    message.obj = results;
                    message.sendToTarget();

                    sendUpdateMessageStatus(messageHandler, "");
                }
                catch (ServerConnectinoException e) {
                    Log.w("ERROR", e);
                    sendUpdateMessageStatus(messageHandler, e.getMessage());
                }
                catch (Throwable e) {
                    Log.w("ERROR", e);
                    sendUpdateMessageStatus(messageHandler, "");
                }
                finally {
                    executingRefreshHistory = false;
                }
            }
        };

        executingRefreshHistory = true;

        sendUpdateMessageStatus(messageHandler, MESSAGE.UPDATING.toString());

        thread.start();
    }

    /**
     * Sends a message to the calling {@link Activity} to update it's status bar
     */
    private static void sendUpdateMessageStatus(Handler messageHandler, String text) {

        Message message = new Message();
        message.setTarget(messageHandler);
        message.what = MESSAGE_STATUS_UPDATE;
        message.obj = text;
        message.sendToTarget();
    }

    public static String makeApiCall(String command, String... xTraParams) throws ServerConnectinoException {

        String url = URL_TEMPLATE;
        if (Preferences.SERVER_PORT == null || "".equals(Preferences.SERVER_PORT))
            url = url.replace("[SERVER_URL]", fixUrlFromPreferences(Preferences.get(Preferences.SERVER_URL)));
        else
            url = url.replace("[SERVER_URL]", fixUrlFromPreferences(Preferences.get(Preferences.SERVER_URL) + ":" + Preferences.get(Preferences.SERVER_PORT)));
        String apiKey = Preferences.get(Preferences.SERVER_API_KEY);
        if (!apiKey.trim().equals("")) {
            url = url + "&apikey=" + apiKey;
        }
        url = url.replace("[COMMAND]", command);
        url = url + getPreferencesParams();
        for (String xTraParam : xTraParams) {
            if (xTraParam != null && !xTraParam.trim().equals("")) {
                url = url + "&" + xTraParam;
            }
        }
        // System.out.println(url);
        return HttpUtil.instance().getData(url);
    }

    /**
     * Removes the http if included in the settings URL
     * 
     * @param string
     * @return
     */
    private static CharSequence fixUrlFromPreferences(String url) {
        if (url.toUpperCase().startsWith("HTTP://")) {
            return url.substring(7);
        }
        return url;
    }

    private static String getPreferencesParams() {
        String username = Preferences.get(Preferences.SERVER_USERNAME);
        String password = Preferences.get(Preferences.SERVER_PASSWORD);

        if (username != null && password != null) {
            return "&ma_username=" + username + "&ma_password=" + password;
        }

        return "";
    }

    public static String makeApiCall(String command) throws ServerConnectinoException {
        return makeApiCall(command, "");
    }

    public static void addFile(final Handler messageHandler, final String value) {
        // Already running or settings not ready
        if (executingCommand || !Preferences.isSet(Preferences.SERVER_URL))
            return;

        Thread thread = new Thread() {

            @Override
            public void run() {
                try {
                    makeApiCall("addurl", "name=" + value);
                }
                catch (Throwable e) {
                }
                finally {
                    sendUpdateMessageStatus(messageHandler, "");
                }
            }
        };

        sendUpdateMessageStatus(messageHandler, MESSAGE.ADDING.toString());

        thread.start();
    }

    /**
     * Removes a queue item
     */
    public static void removeQueueItem(final Handler messageHandler, final String item) {

        // Already running or settings not ready
        if (executingCommand || !Preferences.isSet(Preferences.SERVER_URL))
            return;

        Thread thread = new Thread() {

            @Override
            public void run() {

                String[] values = item.split("#");
                try {
                    makeApiCall("queue", "name=delete", "value=" + values[4]);
                    Thread.sleep(100);
                    SABnzbdController.refreshQueue(messageHandler);
                }
                catch (Throwable e) {
                }
                finally {
                    executingCommand = false;
                    sendUpdateMessageStatus(messageHandler, "");
                }
            }
        };
        executingCommand = true;

        if (paused)
            sendUpdateMessageStatus(messageHandler, MESSAGE.RESUMING.toString());
        else
            sendUpdateMessageStatus(messageHandler, MESSAGE.PAUSING.toString());

        thread.start();
    }
}
