package com.sabdroidex.utils;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class Preferences {

    public static final String SABNZBD_URL = "sabnzbd_url";
    public static final String SABNZBD_PORT = "sabnzbd_port";
    public static final String SABNZBD_SSL = "sabnzbd_ssl";
    public static final String SABNZBD_RATE = "sabnzbd_rate";
    public static final String SABNZBD_HTTP_AUTH = "sabnzbd_auth";
    public static final String SABNZBD_USERNAME = "sabnzbd_auth_username";
    public static final String SABNZBD_PASSWORD = "sabnzbd_auth_password";
    public static final String SABNZBD_API_KEY = "sabnzbd_api_key";

    public static final String SERVER_MISC = "misc";
    public static final String SERVER_BANDWITH = "bandwidth_limit";
    public static final String SERVER_CACHE_DIR = "cache_dir";
    public static final String SERVER_CACHE_LIMIT = "cache_limit";
    public static final String SERVER_DIRSCAN_DIR = "dirscan_dir";
    public static final String SERVER_DIRSCAN_SPEED = "dirscan_speed";
    public static final String SERVER_DOWNLOAD_DIR = "download_dir";
    public static final String SERVER_COMPLETE_DIR = "complete_dir";
    
    public static final String SICKBEARD = "sickbeard";
    public static final String SICKBEARD_URL = "sickbeard_url";
    public static final String SICKBEARD_PORT = "sickbeard_port";
    public static final String SICKBEARD_SSL = "sickbeard_ssl";
    public static final String SICKBEARD_RATE = "sickbeard_rate";
    public static final String SICKBEARD_HTTP_AUTH = "sickbeard_auth";
    public static final String SICKBEARD_USERNAME = "sickbeard_auth_username";
    public static final String SICKBEARD_PASSWORD = "sickbeard_auth_password";
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
