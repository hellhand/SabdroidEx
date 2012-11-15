package com.sabdroidex.activity;

import java.util.ArrayList;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.view.Window;

import com.sabdroidex.R;
import com.sabdroidex.couchpotato.CouchPotatoController;
import com.sabdroidex.utils.Preferences;
import com.sabdroidex.utils.SABDroidConstants;

public class SettingsActivity extends PreferenceActivity {

	   private final Handler messageHandler = new Handler() {

	        public void handleMessage(Message msg) {
	            if (msg.what == CouchPotatoController.MESSAGE.PROFILE_LIST.ordinal()) {
	            	ArrayList<String> labels, id;
	            	Object[] profiles = (Object[]) msg.obj;
	            	labels = (ArrayList<String>) profiles[0];
	            	id = (ArrayList<String>) profiles[1];
	            	ListPreference ProfileList = (ListPreference) findPreference(Preferences.COUCHPOTATO_PROFILE);
	            	ProfileList.setEnabled(true);
	            	ProfileList.setEntryValues(id.toArray(new CharSequence[id.size()]));
	            	ProfileList.setEntries(labels.toArray(new CharSequence[labels.size()]));
	            }
	        }
	    };
	    
    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

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
        
        setCouchPotatoListener(Preferences.COUCHPOTATO);
        setCouchPotatoListener(Preferences.COUCHPOTATO_URL);
        setCouchPotatoListener(Preferences.COUCHPOTATO_URL_EXTENTION);
        setCouchPotatoListener(Preferences.COUCHPOTATO_PORT);

        if(Preferences.isEnabled(Preferences.COUCHPOTATO))
        	fillCouchPotatoProfileList();
    }

    @SuppressWarnings("deprecation")
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
    
    private final void setCouchPotatoListener(String prefKey) {
        final Preference preference = findPreference(prefKey);

        preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

        	public boolean onPreferenceChange(Preference preference, Object newValue) {
        		if(preference.getKey().equals(Preferences.COUCHPOTATO)){
        			if(newValue.equals(Boolean.TRUE)){
        				Log.w("", newValue.toString());
        				fillCouchPotatoProfileList();
        			} else{
        				return true;
        			}
        		}
        		fillCouchPotatoProfileList();
        		return true;
        	}
        }); 
    }
    
    private final void fillCouchPotatoProfileList(){
    	findPreference(Preferences.COUCHPOTATO_PROFILE).setEnabled(false);
    	
		if(!"".equals(Preferences.get(Preferences.COUCHPOTATO_URL))
				&& !"".equals(Preferences.get(Preferences.COUCHPOTATO_PORT)) && 
				!"".equals(Preferences.get(Preferences.COUCHPOTATO_URL_EXTENTION))){
    		CouchPotatoController.getProfiles(messageHandler);
		}
    }
}
