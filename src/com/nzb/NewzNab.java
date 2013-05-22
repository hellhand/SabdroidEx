package com.nzb;

import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.impl.client.BasicCredentialsProvider;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.sabdroidex.utils.Preferences;
import com.utils.HttpUtil;


public class NewzNab {

    private static String URL_TEMPLATE = "[SERVER_URL]/api?apikey=[APIKEY]&t=[COMMAND]&q=[SEARCH]&o=json";
    
    private static boolean executingCommand = false;
    
    public static enum MESSAGE {
        SEARCH, UPDATE
    }
    
    /**
     * 
     * @param search The Search {@link String}
     * @return 
     * @return a {@link String[][]} containing the result of the research. The inner array contains : 1 Name, 2 Size, 3 Category, 4 Password, 5 Image.
     */
    public static void search(final Handler messageHandler, final Object[] item) {
        // Already running or settings not ready
        if (executingCommand || !Preferences.isSet(Preferences.SABNZBD_URL))
            return;

        Thread thread = new Thread() {

            @Override
            public void run() {
                try {
                    Object results[] = new Object[2];

                    String searchData = makeApiCall(MESSAGE.SEARCH.toString().toLowerCase(), "name=" + URLEncoder.encode(((String)item[0]), "UTF-8"), "lang=en");
                    ArrayList<Object[]> rows = new ArrayList<Object[]>();

                    /**
                     * Getting the values from the JSON Object
                     */
                    JSONObject jsonObject = new JSONObject(searchData);
                    if (!jsonObject.isNull("message") && !"".equals(jsonObject.getString("message"))) {
                        sendUpdateMessageStatus(messageHandler, "NewzNab : " + jsonObject.getString("message"));
                    }
                    else {
                        jsonObject = jsonObject.getJSONObject("data");
                        results[0] = jsonObject;

                        JSONArray jobs = jsonObject.getJSONArray("results");
                        rows.clear();

                        for (int i = 0; i < jobs.length(); i++) {
                            Object[] rowValues = new Object[3];
                            rowValues[0] = jobs.getJSONObject(i).getString("first_aired");
                            rowValues[1] = jobs.getJSONObject(i).getString("name");
                            rowValues[2] = jobs.getJSONObject(i).getString("tvdbid");
                            rows.add(rowValues);
                        }

                        results[1] = rows;

                        Message message = new Message();
                        message.setTarget(messageHandler);
                        message.what = MESSAGE.SEARCH.hashCode();
                        message.obj = results;
                        message.sendToTarget();
                    }
                }
                catch (Throwable e) {
                    Log.w("ERROR", " " + e.getLocalizedMessage());
                }
                finally {
                    sendUpdateMessageStatus(messageHandler, "");
                }
            }
        };

        sendUpdateMessageStatus(messageHandler, MESSAGE.SEARCH.toString());

        thread.start();
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
    public static String makeApiCall(String command, String... extraParams) throws Exception {
        
        String url = getFormattedUrl();
        
        /**
         * Checking if there is an API Key from Sabnzbd to concatenate to the
         * URL
         */
        String apiKey = Preferences.get(Preferences.SABNZBD_API_KEY);
        if (!apiKey.trim().equals("")) {
            url = url + "&apikey=" + apiKey;
        }
        
        url = url.replace("[COMMAND]", command);
        
        for (String xTraParam : extraParams) {
            if (xTraParam != null && !xTraParam.trim().equals("")) {
                url = url + "&" + xTraParam;
            }
        }
        
        String result = new String(HttpUtil.getInstance().getDataAsCharArray(url, new BasicCredentialsProvider()));
        return result;
    }
    
    /**
     * This function gets the URL used to connect to the NewzNab server
     * 
     * @return url A {@link String} containing the URL of the NewzNab server
     */
    private static String getFormattedUrl() {
        String url = URL_TEMPLATE;
        /**
         * Checking if there is a port to concatenate to the URL
         */
        if ("".equals(Preferences.get(Preferences.SABNZBD_PORT))) {
            url = url.replace("[SERVER_URL]", Preferences.get(Preferences.SABNZBD_URL));
        }
        else {
            url = url.replace("[SERVER_URL]", Preferences.get(Preferences.SABNZBD_URL) + ":" + Preferences.get(Preferences.SABNZBD_PORT));
        }
        
        if (!url.toUpperCase().startsWith("HTTP://") && !url.toUpperCase().startsWith("HTTPS://")) {
            if (Preferences.isEnabled(Preferences.SABNZBD_SSL)) {
                url = "https://" + url;
            }
            else {
                url = "http://" + url;
            }
        }
        
        return url;
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
