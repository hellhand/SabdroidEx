package com.sabdroidex.utils;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class Preferences {

    public static final String SABNZBD_URL = "sabnzbd_url";
    public static final String SABNZBD_PORT = "sabnzbd_port";
    public static final String SABNZBD_URL_EXTENTION = "sabnzbd_url_extention";
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
    public static final String SICKBEARD_URL_EXTENTION = "sickbeard_url_extention";
    public static final String SICKBEARD_SSL = "sickbeard_ssl";
    public static final String SICKBEARD_RATE = "sickbeard_rate";
    public static final String SICKBEARD_HTTP_AUTH = "sickbeard_auth";
    public static final String SICKBEARD_USERNAME = "sickbeard_auth_username";
    public static final String SICKBEARD_PASSWORD = "sickbeard_auth_password";
    public static final String SICKBEARD_API_KEY = "sickbeard_api_key";
    
    public static final String COUCHPOTATO = "couchpotato";
    public static final String COUCHPOTATO_URL = "couchpotato_url";
    public static final String COUCHPOTATO_PORT = "couchpotato_port";
    public static final String COUCHPOTATO_URL_EXTENTION = "couchpotato_url_extention";
    public static final String COUCHPOTATO_SSL = "couchpotato_ssl";
    public static final String COUCHPOTATO_API_KEY = "couchpotato_api_key";
    public static final String COUCHPOTATO_AUTH = "couchpotato_auth";
    public static final String COUCHPOTATO_PROFILE = "couchpotato_profile";
    public static final String COUCHPOTATO_USERNAME = "couchpotato_auth_username";
    public static final String COUCHPOTATO_PASSWORD = "couchpotato_auth_password";

    public static final String MOVIE_AVAILABLE_FILES = "movie_available_files";
    public static final String MOVIE_READD = "movie_readd";
    public static final String MOVIE_CHANGE_MOVIE_INFO = "movie_change_movie_info";
    public static final String MOVIE_REMOVE = "movie_remove";
    public static final String MOVIE_DOWLOAD_BEST = "movie_dowload_best";
    public static final String MOVIE_PICK_RELEASE = "movie_pick_release";
    public static final String MOVIE_MARK_DONE = "movie_mark_done";
    public static final String WANTED = "wanted";

    public static final String APACHE = "apache";
    public static final String APACHE_USERNAME = "apache_auth_username";
    public static final String APACHE_PASSWORD = "apache_auth_password";

    public static final String NZBS_ORG_UID = "nzbs_org_uid";
    public static final String NZBS_ORG_HASH = "nzbs_org_hash";

    public static final String NZBMATRIX_UID = "nzbmatrix_uid";
    public static final String NZBMATRIX_HASH = "nzbmatrix_hash";

    public static final String DATA_CACHE = "data_cache";
    public static final String DATA_IMAGE_CACHE = "data_image_cache";
    public static final String DATA_IMAGE_LOWRES = "data_image_lowres";
    public static final String DATA_NO_MEDIA = "data_nomedia";

    public static final String BACKUP = "backup_preferences";
    public static final String RESTORE = "restore_preferences";
    public static final String BACKUP_FILE = "preferences.json";

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

    public static void put(String key, Boolean value) {
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static String get(String key, String defaultValue) {
        return preferences.getString(key, defaultValue);
    }

    public static Boolean isEnabled(String key) {
        return preferences.getBoolean(key, Boolean.FALSE);
    }

    public static Boolean isEnabled(String key, Boolean defaultValue) {
        return preferences.getBoolean(key, defaultValue);
    }

    public static boolean isSet(String key) {

        if (preferences == null) {
            return false;
        }

        if (preferences.getString(key, null) == null) {
            return false;
        }

        if (preferences.getString(key, "").trim().equals("")) {
            return false;
        }

        return true;
    }

    /**
     * This method is used to migrate preferences.
     * It is highly un-elegant but it does it's job pretty well
     */
    public static void setUpNewVersion() {

        if ("2.4.0".compareTo(get(VERSION)) < 0) {

            put(DATA_IMAGE_CACHE, true);
            put(DATA_IMAGE_LOWRES, true);
            put(DATA_NO_MEDIA, true);
        }
    }
}
