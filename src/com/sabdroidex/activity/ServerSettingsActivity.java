package com.sabdroidex.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.view.Window;

import com.sabdroidex.R;
import com.sabdroidex.controllers.sabnzbd.SABnzbdController;
import com.sabdroidex.utils.Preferences;
import com.sabdroidex.utils.SABDroidConstants;

public class ServerSettingsActivity extends PreferenceActivity {

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        
        preferences = getSharedPreferences(SABDroidConstants.PREFERENCES_KEY, MODE_PRIVATE);
        
        getValuesFromServer();

    }

    @SuppressWarnings("deprecation")
    private void onPostCreate() {

        getPreferenceManager().setSharedPreferencesName(SABDroidConstants.PREFERENCES_KEY);
        addPreferencesFromResource(R.xml.server_settings);

        setChangeListener(Preferences.SERVER_BANDWITH);
        setChangeListener(Preferences.SERVER_CACHE_DIR);
        setChangeListener(Preferences.SERVER_CACHE_LIMIT);
        setChangeListener(Preferences.SERVER_DIRSCAN_DIR);
        setChangeListener(Preferences.SERVER_DIRSCAN_SPEED);
        setChangeListener(Preferences.SERVER_DOWNLOAD_DIR);
        setChangeListener(Preferences.SERVER_COMPLETE_DIR);
    }

    /**
     * This is an asynchronous method to get the Sabnzbd server bandwidth
     */
    private void getValuesFromServer() {
        SABnzbdController.getAllConfigs(messageHandler, null);
    }

    /**
     * Instantiating the Handler associated with the main thread.
     */
    private final Handler messageHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == SABnzbdController.MESSAGE.GET_CONFIG.ordinal()) {
                values = (Object[]) ((Object[]) msg.obj)[1];

                setPreferenceValue(Preferences.SERVER_BANDWITH, values[0]);
                setPreferenceValue(Preferences.SERVER_CACHE_DIR, values[1]);
                setPreferenceValue(Preferences.SERVER_CACHE_LIMIT, values[2]);
                setPreferenceValue(Preferences.SERVER_DIRSCAN_DIR, values[3]);
                setPreferenceValue(Preferences.SERVER_DIRSCAN_SPEED, values[4]);
                setPreferenceValue(Preferences.SERVER_DOWNLOAD_DIR, values[5]);
                setPreferenceValue(Preferences.SERVER_COMPLETE_DIR, values[6]);

                onPostCreate();
            }
        }
    };

    /**
     * This method sets the settings value retrieved from the server to it's counterpart in the application.
     * 
     * @param prefKey
     *            The key of the preference
     * @param Value
     *            The value to set.
     */
    private void setPreferenceValue(String prefKey, Object value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(prefKey);
        editor.commit();
        editor.putString(prefKey, String.valueOf(value));
        editor.commit();
    }

    private static Object[] values;

    /**
     * Sets a change listener on a setting in order to send the change to the server when the user changes one of the values.
     * 
     * @param prefKey
     * @param resId
     */
    @SuppressWarnings("deprecation")
    private final void setChangeListener(String prefKey) {
        final Preference preference = findPreference(prefKey);

        preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            public boolean onPreferenceChange(Preference preference, Object newValue) {
                SABnzbdController.setConfig(messageHandler, new Object[] { "misc", preference.getKey(), newValue });
                return true;
            }
        });
    }
}
