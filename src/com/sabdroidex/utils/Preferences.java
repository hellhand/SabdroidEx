package com.sabdroidex.utils;

import android.content.SharedPreferences;

public class Preferences {

    public static final String SERVER_URL = "server_url";
    public static final String SERVER_PORT = "server_port";
    public static final String SERVER_HTTP_AUTH = "server_http_auth";
    public static final String SERVER_USERNAME = "sabnzb_auth_username";
    public static final String SERVER_PASSWORD = "sabnzb_auth_password";
    public static final String SERVER_API_KEY = "sabnzb_api_key";
    public static final String NZBS_ORG_UID = "nzbs_ord_uid";// "nzbs_org_uid";
    public static final String NZBS_ORG_HASH = "nzbs_org_hash";// "nzbs_org_hash";

    private static SharedPreferences preferences;

    public static void update(SharedPreferences preferences) {
        Preferences.preferences = preferences;
    }

    public static String get(String key) {
        return preferences.getString(key, "").toString();
    }

    public static String get(String key, String defaultValue) {
        return preferences.getString(key, defaultValue);
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
