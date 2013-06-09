package com.sabdroidex.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.TextView;

import com.sabdroidex.R;
import com.sabdroidex.controllers.couchpotato.CouchPotatoController;
import com.sabdroidex.utils.Preferences;
import com.sabdroidex.utils.SABDroidConstants;

public class CouchSettingsActivity extends PreferenceActivity {
    
    private TextView empty;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        
        empty = new TextView(getApplicationContext());
        empty.setText(R.string.setting_empty);
        empty.setGravity(Gravity.CENTER);
        empty.setId(1337);
        getWindow().addContentView(empty, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        Logger.getLogger(getClass().getName()).log(Level.INFO, "" + empty.getId());
        
        fillCouchPotatoProfileList();
        
    }
    
    @SuppressWarnings("deprecation")
    protected void onPostCreate() {
        
        getWindow().findViewById(empty.getId()).setVisibility(View.GONE);
        
        getPreferenceManager().setSharedPreferencesName(SABDroidConstants.PREFERENCES_KEY);
        addPreferencesFromResource(R.xml.couch_settings);
        
        setCouchPotatoListener(Preferences.COUCHPOTATO);
        setCouchPotatoListener(Preferences.COUCHPOTATO_URL);
        setCouchPotatoListener(Preferences.COUCHPOTATO_URL_EXTENTION);
        setCouchPotatoListener(Preferences.COUCHPOTATO_PORT);
        
        if (Preferences.isEnabled(Preferences.COUCHPOTATO))
            fillCouchPotatoProfileList();
    }
    
    private final Handler messageHandler = new Handler() {
        
        @SuppressWarnings({ "unchecked", "deprecation" })
        public void handleMessage(Message msg) {
            if (msg.what == CouchPotatoController.MESSAGE.PROFILE_LIST.hashCode()) {
                ArrayList<String> labels, id;
                Object[] results = (Object[]) msg.obj;
                HashMap<Integer, String> profiles = (HashMap<Integer, String>) results[0];
                labels = new ArrayList<String>();
                id = new ArrayList<String>();
                for (Integer key : profiles.keySet()) {
                    id.add(Integer.toString(key));
                    labels.add(profiles.get(key));
                }
                
                ListPreference ProfileList = (ListPreference) findPreference(Preferences.COUCHPOTATO_PROFILE);
                ProfileList.setEnabled(true);
                ProfileList.setEntryValues(id.toArray(new CharSequence[id.size()]));
                ProfileList.setEntries(labels.toArray(new CharSequence[labels.size()]));
            }
        }
    };
    
    @SuppressWarnings("deprecation")
    private void setCouchPotatoListener(String prefKey) {
        final Preference preference = findPreference(prefKey);
        
        preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                fillCouchPotatoProfileList();
                return true;
            }
        });
    }
    
    private void fillCouchPotatoProfileList() {
        CouchPotatoController.getProfiles(messageHandler);
    }
}
