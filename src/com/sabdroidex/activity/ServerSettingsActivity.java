package com.sabdroidex.activity;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.Preference;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

import com.android.actionbarcompat.ActionBarPreferencesActivity;
import com.sabdroidex.R;
import com.sabdroidex.controllers.sabnzbd.SABnzbdController;
import com.sabdroidex.data.sabnzbd.SabnzbdConfig;
import com.sabdroidex.utils.Preferences;
import com.sabdroidex.utils.SABDroidConstants;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerSettingsActivity extends ActionBarPreferencesActivity {

    /**
     * Instantiating the Handler associated with the main thread.
     */
    private final Handler messageHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == SABnzbdController.MESSAGE.GET_CONFIG.hashCode()) {
                SabnzbdConfig config = (SabnzbdConfig) msg.obj;

                setPreferenceValue(Preferences.SERVER_BANDWITH, config.getMisc().getBanwidthLimit());
                setPreferenceValue(Preferences.SERVER_CACHE_DIR, config.getMisc().getCacheDir());
                setPreferenceValue(Preferences.SERVER_CACHE_LIMIT, config.getMisc().getCacheLimit());
                setPreferenceValue(Preferences.SERVER_DIRSCAN_DIR, config.getMisc().getDirscanDir());
                setPreferenceValue(Preferences.SERVER_DIRSCAN_SPEED, config.getMisc().getDirscanSpeed());
                setPreferenceValue(Preferences.SERVER_DOWNLOAD_DIR, config.getMisc().getDownloadDir());
                setPreferenceValue(Preferences.SERVER_COMPLETE_DIR, config.getMisc().getCompleteDir());

                onPostCreate();
            }
        }
    };
    private SharedPreferences preferences;
    private TextView empty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        empty = new TextView(getApplicationContext());
        empty.setText(R.string.setting_empty);
        empty.setGravity(Gravity.CENTER);
        empty.setId(1337);
        getWindow().addContentView(empty, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        Logger.getLogger(getClass().getName()).log(Level.INFO, "" + empty.getId());

        preferences = getSharedPreferences(SABDroidConstants.PREFERENCES_KEY, MODE_PRIVATE);

        getValuesFromServer();

    }

    @SuppressWarnings("deprecation")
    private void onPostCreate() {

        getWindow().findViewById(empty.getId()).setVisibility(View.GONE);

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

    /**
     * This is an asynchronous method to get the Sabnzbd server bandwidth
     */
    private void getValuesFromServer() {
        SABnzbdController.getAllConfigs(messageHandler);
    }

    /**
     * This method sets the settings value retrieved from the server to it's counterpart in the application.
     *
     * @param prefKey The key of the preference
     * @param value   The value to set.
     */
    private void setPreferenceValue(String prefKey, Object value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(prefKey);
        editor.commit();
        editor.putString(prefKey, String.valueOf(value));
        editor.commit();
    }

    /**
     * Sets a change listener on a setting in order to send the change to the server when the user changes one of the values.
     *
     * @param prefKey
     */
    @SuppressWarnings("deprecation")
    private final void setChangeListener(String prefKey) {
        final Preference preference = findPreference(prefKey);

        preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            public boolean onPreferenceChange(Preference preference, Object newValue) {
                SABnzbdController.setConfig(messageHandler, new Object[]{"misc", preference.getKey(), newValue});
                return true;
            }
        });
    }
}
