package com.sabdroidex.activity;

import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.view.MenuItem;

import com.android.actionbarcompat.ActionBarPreferencesActivity;
import com.sabdroidex.R;
import com.sabdroidex.utils.Preferences;
import com.sabdroidex.utils.SABDroidConstants;

public class SettingsActivity extends ActionBarPreferencesActivity {

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        getPreferenceManager().setSharedPreferencesName(SABDroidConstants.PREFERENCES_KEY);
        addPreferencesFromResource(R.xml.preferences);

        setSummaryChangeListener(Preferences.SABNZBD_URL, R.string.setting_server_url);
        setSummaryChangeListener(Preferences.SABNZBD_URL_EXTENTION, R.string.setting_server_url_extention);
        setSummaryChangeListener(Preferences.SABNZBD_PORT, R.string.setting_server_port);
        setSummaryChangeListener(Preferences.SABNZBD_RATE, R.string.setting_refresh_rate);
        setSummaryChangeListener(Preferences.SABNZBD_API_KEY, R.string.setting_api_key);

        setSummaryChangeListener(Preferences.SICKBEARD_URL, R.string.setting_sickbeard_url);
        setSummaryChangeListener(Preferences.SICKBEARD_URL_EXTENTION, R.string.setting_sickbeard_url_extention);
        setSummaryChangeListener(Preferences.SICKBEARD_PORT, R.string.setting_sickbeard_port);
        setSummaryChangeListener(Preferences.SICKBEARD_RATE, R.string.setting_refresh_rate);
        setSummaryChangeListener(Preferences.SICKBEARD_API_KEY, R.string.setting_sickbeard_api_key);

        setSummaryChangeListener(Preferences.COUCHPOTATO_URL, R.string.setting_couchpotato_url);
        setSummaryChangeListener(Preferences.COUCHPOTATO_URL_EXTENTION, R.string.setting_couchpotato_url_extention);
        setSummaryChangeListener(Preferences.COUCHPOTATO_PORT, R.string.setting_sickbeard_port);
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

    @SuppressWarnings("deprecation")
    private void setSummaryChangeListener(String prefKey, final int resId) {
        final Preference preference = findPreference(prefKey);

        String currentValue = getSharedPreferences(SABDroidConstants.PREFERENCES_KEY, 0).getString(prefKey, null);
        if (currentValue != null) {
            preference.setSummary(currentValue);
        }

        preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (newValue != null) {
                    preference.setSummary(newValue.toString());
                    return true;
                } else {
                    preference.setSummary(getString(resId));
                    return false;
                }
            }
        });
    }
}
