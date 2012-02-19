package com.sabdroidex.sickbeard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.json.JSONObject;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.sabdroidex.utils.Preferences;
import com.utils.HttpUtil;

public final class SickBeardController {

    public static final int MESSAGE_UPDATE = 1;

    private static boolean executingRefresh = false;
    private static boolean executingCommand = false;

    private static final String URL_TEMPLATE = "[SICKBEARD_SERVER_URL]/api/[SICKBEARD_API_KEY]?cmd=[COMMAND]";
    private static final String URL_TVDB = "http://thetvdb.com/banners/posters/[TVDBID]";

    public static enum MESSAGE {
        SHOWS, SHOW, FUTURE, SHOW_GETBANNER, SHOW_GETPOSTER, ADD
    }

    public static void refreshShows(final Handler messageHandler) {

        // Already running or settings not ready
        if (executingRefresh || !Preferences.isSet(Preferences.SICKBEARD_URL))
            return;

        Thread thread = new Thread() {

            public void run() {

                try {
                    Object results[] = new Object[2];
                    String queueData = makeApiCall(MESSAGE.SHOWS.toString().toLowerCase(), "sort=name");

                    ArrayList<Object[]> rows = new ArrayList<Object[]>();

                    /**
                     * Getting the values from the JSON Object
                     */
                    JSONObject jsonObject = new JSONObject(queueData);
                    jsonObject = jsonObject.getJSONObject("data");
                    results[0] = jsonObject;

                    List<String> sortKey = new ArrayList<String>();
                    Iterator<?> iterator = jsonObject.keys();
                    while (iterator.hasNext()) {
                        sortKey.add((String) iterator.next());
                    }
                    Collections.sort(sortKey);
                    rows.clear();

                    for (int i = 0; i < sortKey.size(); i++) {
                        /**
                         * The seventh item will be the banner The eighth item will be the poster
                         */
                        Object[] rowValues = new Object[7];
                        JSONObject current = jsonObject.getJSONObject(sortKey.get(i));
                        rowValues[0] = sortKey.get(i);
                        rowValues[1] = current.getString("status");
                        rowValues[2] = current.getString("quality");
                        rowValues[3] = current.getString("next_ep_airdate");
                        rowValues[4] = current.getString("network");
                        rowValues[5] = current.getInt("tvdbid");
                        rowValues[6] = current.getString("language");
                        rows.add(rowValues);
                    }

                    results[1] = rows;

                    Message message = new Message();
                    message.setTarget(messageHandler);
                    message.what = MESSAGE_UPDATE;
                    message.obj = results;
                    message.sendToTarget();
                }
                catch (RuntimeException e) {
                    Log.w("ERROR", " " + e.getLocalizedMessage());
                }
                catch (Throwable e) {
                    Log.w("ERROR", " " + e.getLocalizedMessage());
                }
                finally {
                    executingRefresh = false;
                }
            }
        };

        executingRefresh = true;

        thread.start();
    }

    public static String makeApiCall(String command, String... extraParams) throws RuntimeException {

        /**
         * Correcting the command names to be understood by SickBeard
         */
        command = command.replace('_', '.');

        String url = URL_TEMPLATE;
        /**
         * Checking if there is a port to concatenate to the URL
         */
        if ("".equals(Preferences.get(Preferences.SICKBEARD_PORT))) {
            url = url.replace("[SICKBEARD_SERVER_URL]", Preferences.get(Preferences.SICKBEARD_URL));
        }
        else {
            url = url.replace("[SICKBEARD_SERVER_URL]", Preferences.get(Preferences.SICKBEARD_URL) + ":" + Preferences.get(Preferences.SICKBEARD_PORT));
        }

        /**
         * Checking if there is an API Key from SickBeard to concatenate to the URL
         */
        if ("".equals(Preferences.get(Preferences.SICKBEARD_API_KEY))) {
            url = url.replace("[SICKBEARD_API_KEY]", "");
        }
        else {
            url = url.replace("[SICKBEARD_API_KEY]", Preferences.get(Preferences.SICKBEARD_API_KEY) + "/");
        }

        url = url.replace("[COMMAND]", command);

        for (String xTraParam : extraParams) {
            if (xTraParam != null && !xTraParam.trim().equals("")) {
                url = url + "&" + xTraParam;
            }
        }
        String result = new String(HttpUtil.getInstance().getDataAsCharArray(url));
        return result;
    }

    public static String getBannerURL(String command, Integer tvdbid) {

        /**
         * Correcting the command names to be understood by SickBeard
         */
        command = command.replace('_', '.');

        String url = URL_TEMPLATE;
        /**
         * Checking if there is a port to concatenate to the URL
         */
        if ("".equals(Preferences.get(Preferences.SICKBEARD_PORT))) {
            url = url.replace("[SICKBEARD_SERVER_URL]", Preferences.get(Preferences.SICKBEARD_URL));
        }
        else {
            url = url.replace("[SICKBEARD_SERVER_URL]", Preferences.get(Preferences.SICKBEARD_URL) + ":" + Preferences.get(Preferences.SICKBEARD_PORT));
        }

        /**
         * Checking if there is an API Key from SickBeard to concatenate to the URL
         */
        if ("".equals(Preferences.get(Preferences.SICKBEARD_API_KEY))) {
            url = url.replace("[SICKBEARD_API_KEY]", "");
        }
        else {
            url = url.replace("[SICKBEARD_API_KEY]", Preferences.get(Preferences.SICKBEARD_API_KEY) + "/");
        }

        url = url.replace("[COMMAND]", command);
        url = url + "&tvdbid=" + tvdbid;

        return url;
    }

    public static String getPosterURL(String command, Integer tvdbid) {
        String url = URL_TVDB;
        url = url.replace("[TVDBID]", tvdbid + "-1.jpg");

        return url;
    }

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

    public static String makeApiCall(String command) throws RuntimeException {
        return makeApiCall(command, "");
    }
}
