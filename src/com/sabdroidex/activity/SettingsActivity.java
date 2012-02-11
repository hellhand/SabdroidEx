package com.sabdroidex.activity;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.view.Window;

import com.sabdroidex.R;
import com.sabdroidex.utils.Preferences;
import com.sabdroidex.utils.SABDroidConstants;

public class SettingsActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        getPreferenceManager().setSharedPreferencesName(SABDroidConstants.PREFERENCES_KEY);
        addPreferencesFromResource(R.xml.preferences);

        setSummaryChangeListener(Preferences.SERVER_URL, R.string.setting_server_url);
        setSummaryChangeListener(Preferences.SERVER_PORT, R.string.setting_server_port);
        setSummaryChangeListener(Preferences.SERVER_RATE, R.string.setting_refresh_rate);
        setSummaryChangeListener(Preferences.SERVER_USERNAME, R.string.setting_auth_username);
        setSummaryChangeListener(Preferences.SERVER_PASSWORD, R.string.setting_auth_password);
        setSummaryChangeListener(Preferences.SERVER_API_KEY, R.string.setting_api_key);

        setSummaryChangeListener(Preferences.SICKBEARD_URL, R.string.setting_sickbeard_url);
        setSummaryChangeListener(Preferences.SICKBEARD_PORT, R.string.setting_sickbeard_port);
        setSummaryChangeListener(Preferences.SICKBEARD_RATE, R.string.setting_refresh_rate);
        setSummaryChangeListener(Preferences.SICKBEARD_API_KEY, R.string.setting_sickbeard_api_key);

    }

    private final void setSummaryChangeListener(String prefKey, final int resId) {
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
                }
                else {
                    preference.setSummary(getString(resId));
                    return false;
                }
            }
        });
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        return true;
    }
}
