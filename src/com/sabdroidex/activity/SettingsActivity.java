package com.sabdroidex.activity;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.actionbarcompat.ActionBarPreferencesActivity;
import com.sabdroidex.R;
import com.sabdroidex.utils.Preferences;
import com.sabdroidex.utils.SABDroidConstants;
import com.utils.FileUtil;

import org.json.JSONObject;

import java.io.File;
import java.util.Iterator;

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

        setSummaryChangeListener(Preferences.SABNZBD_URL, R.string.setting_sabnzbd_url);
        setSummaryChangeListener(Preferences.SABNZBD_URL_EXTENTION, R.string.setting_sabnzbd_url_extention);
        setSummaryChangeListener(Preferences.SABNZBD_PORT, R.string.setting_sabnzbd_port);
        setSummaryChangeListener(Preferences.SABNZBD_RATE, R.string.setting_sabnzbd_refresh_rate);
        setSummaryChangeListener(Preferences.SABNZBD_API_KEY, R.string.setting_sabnzbd_api_key);

        setSummaryChangeListener(Preferences.SICKBEARD_URL, R.string.setting_sickbeard_url);
        setSummaryChangeListener(Preferences.SICKBEARD_URL_EXTENTION, R.string.setting_sickbeard_url_extention);
        setSummaryChangeListener(Preferences.SICKBEARD_PORT, R.string.setting_sickbeard_port);
        setSummaryChangeListener(Preferences.SICKBEARD_RATE, R.string.setting_sabnzbd_refresh_rate);
        setSummaryChangeListener(Preferences.SICKBEARD_API_KEY, R.string.setting_sickbeard_api_key);

        setSummaryChangeListener(Preferences.COUCHPOTATO_URL, R.string.setting_couchpotato_url);
        setSummaryChangeListener(Preferences.COUCHPOTATO_URL_EXTENTION, R.string.setting_couchpotato_url_extention);
        setSummaryChangeListener(Preferences.COUCHPOTATO_PORT, R.string.setting_sickbeard_port);
        setSummaryChangeListener(Preferences.COUCHPOTATO_API_KEY, R.string.setting_couchpotato_api_key);

        setBackupClickListener(Preferences.BACKUP);
        setRestoreClickListener(Preferences.RESTORE);
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
                }
                else {
                    preference.setSummary(getString(resId));
                    return false;
                }
            }
        });
    }

    private void setBackupClickListener(String prefKey) {
        final Preference preference = findPreference(prefKey);
        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                String json = null;
                SharedPreferences sharedPreferences = getSharedPreferences(SABDroidConstants.PREFERENCES_KEY, MODE_PRIVATE);
                try {
                    JSONObject jsonObject = new JSONObject();
                    for (Iterator iterator = sharedPreferences.getAll().keySet().iterator() ;iterator.hasNext();) {
                        String key = (String) iterator.next();
                        jsonObject.put(key, sharedPreferences.getAll().get(key));
                    }
                    json = jsonObject.toString(4);
                }
                catch (Exception e) {
                    //
                }
                finally {
                    try {
                        FileUtil.saveFileFromCharArray(FileUtil.SABDROIDEX + File.separator + Preferences.BACKUP_FILE, json.toCharArray());
                        Toast.makeText(SettingsActivity.this, getString(R.string.setting_backup_done), Toast.LENGTH_LONG).show();
                    }
                    catch (Exception e) {
                        //
                    }
                }
                return true;
            }
        });
    }

    private void setRestoreClickListener(String prefKey) {
        final Preference preference = findPreference(prefKey);
        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                char[] data = FileUtil.getFileAsCharArray(FileUtil.SABDROIDEX + File.separator + Preferences.BACKUP_FILE);
                if (data == null) {
                    return false;
                }
                String json = new String(data);
                SharedPreferences sharedPreferences = getSharedPreferences(SABDroidConstants.PREFERENCES_KEY, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    Iterator iterator = jsonObject.keys();
                    while (iterator.hasNext()) {
                        String key = (String) iterator.next();
                        if (jsonObject.get(key) instanceof String)
                            editor.putString(key, (String) jsonObject.get(key));
                        if (jsonObject.get(key) instanceof Number)
                            editor.putInt(key, (Integer) jsonObject.get(key));
                    }
                    editor.commit();
                    Toast.makeText(SettingsActivity.this, getString(R.string.setting_backup_restored), Toast.LENGTH_LONG).show();
                }
                catch (Exception e) {
                    //
                }
                return true;
            }
        });
    }
}