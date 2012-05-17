package com.sabdroidex.utils;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class Preferences {

    public static final String SERVER_URL = "server_url";
    public static final String SERVER_PORT = "server_port";
    public static final String SERVER_SSL = "server_ssl";
    public static final String SERVER_RATE = "refresh_rate";
    public static final String SERVER_HTTP_AUTH = "server_http_auth";
    public static final String SERVER_USERNAME = "sabnzb_auth_username";
    public static final String SERVER_PASSWORD = "sabnzb_auth_password";
    public static final String SERVER_API_KEY = "sabnzb_api_key";

    public static final String SICKBEARD = "sickbeard";
    public static final String SICKBEARD_URL = "sickbeard_url";
    public static final String SICKBEARD_PORT = "sickbeard_port";
    public static final String SICKBEARD_SSL = "sickbeard_ssl";
    public static final String SICKBEARD_RATE = "sickbeard_rate";
    public static final String SICKBEARD_API_KEY = "sickbeard_api_key";
    public static final String SICKBEARD_CACHE = "sickbeard_cache";
    public static final String SICKBEARD_LOWRES = "sickbeard_lowres";
    public static final String SICKBEARD_NOMEDIA = "sickbeard_nomedia";

    public static final String NZBS_ORG_UID = "nzbs_org_uid";
    public static final String NZBS_ORG_HASH = "nzbs_org_hash";

    public static final String NZBMATRIX_UID = "nzbmatrix_uid";
    public static final String NZBMATRIX_HASH = "nzbmatrix_hash";

    public static final String DATA_CACHE = "data.cache";
    
    public static final String VERSION = "data_version";

    private static SharedPreferences preferences;
    
    private static Editor editor;

    public static void update(SharedPreferences preferences) {
        Preferences.preferences = preferences;
        Preferences.editor = preferences.edit();
    }

    public static String get(String key) {
        return preferences.getString(key, "");
    }

    public static void put(String key, String value) {
        editor.putString(key, value);
        editor.commit();
    }
    
    public static String get(String key, String defaultValue) {
        return preferences.getString(key, defaultValue);
    }

    public static Boolean isEnabled(String key) {
        return preferences.getBoolean(key, false);
    }

    public static Boolean isEnabled(String key, Boolean defaultValue) {
        return preferences.getBoolean(key, defaultValue);
    }

    public static boolean isSet(String key) {
        if (preferences.getString(key, null) == null) {
            return false;
        }

        if (preferences.getString(key, "").toString().trim().equals("")) {
            return false;
        }

        return true;
    }
}
