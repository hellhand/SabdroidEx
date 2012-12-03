package com.sabdroidex.activity;

import java.util.ArrayList;

import com.sabdroidex.R;
import com.sabdroidex.controllers.couchpotato.CouchPotatoController;
import com.sabdroidex.utils.Preferences;
import com.sabdroidex.utils.SABDroidConstants;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.view.Window;

public class CouchSettingsActivity extends PreferenceActivity {

	private final Handler messageHandler = new Handler() {

		@SuppressWarnings({ "unchecked", "deprecation" })
		public void handleMessage(Message msg) {
			if (msg.what == CouchPotatoController.MESSAGE.PROFILE_LIST
					.ordinal()) {
				ArrayList<String> labels, id;
				Object[] profiles = (Object[]) msg.obj;
				labels = (ArrayList<String>) profiles[0];
				id = (ArrayList<String>) profiles[1];
				ListPreference ProfileList = (ListPreference) findPreference(Preferences.COUCHPOTATO_PROFILE);
				ProfileList.setEnabled(true);
				ProfileList.setEntryValues(id.toArray(new CharSequence[id
						.size()]));
				ProfileList.setEntries(labels.toArray(new CharSequence[labels
						.size()]));
			}
		}
	};

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        getPreferenceManager().setSharedPreferencesName(SABDroidConstants.PREFERENCES_KEY);
        addPreferencesFromResource(R.xml.couch_settings);
		
		setCouchPotatoListener(Preferences.COUCHPOTATO);
		setCouchPotatoListener(Preferences.COUCHPOTATO_URL);
		setCouchPotatoListener(Preferences.COUCHPOTATO_URL_EXTENTION);
		setCouchPotatoListener(Preferences.COUCHPOTATO_PORT);

		if (Preferences.isEnabled(Preferences.COUCHPOTATO))
			fillCouchPotatoProfileList();
	}

	@SuppressWarnings("deprecation")
	private final void setCouchPotatoListener(String prefKey) {
		final Preference preference = findPreference(prefKey);

		preference
				.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						if (preference.getKey().equals(Preferences.COUCHPOTATO)) {
							if (newValue.equals(Boolean.TRUE)) {
								Log.w("", newValue.toString());
								fillCouchPotatoProfileList();
							} else {
								return true;
							}
						}
						fillCouchPotatoProfileList();
						return true;
					}
				});
	}

	@SuppressWarnings("deprecation")
	private final void fillCouchPotatoProfileList() {
		findPreference(Preferences.COUCHPOTATO_PROFILE).setEnabled(false);

		if (!"".equals(Preferences.get(Preferences.COUCHPOTATO_URL))
				&& !"".equals(Preferences.get(Preferences.COUCHPOTATO_PORT))
				&& !"".equals(Preferences
						.get(Preferences.COUCHPOTATO_URL_EXTENTION))) {
			CouchPotatoController.getProfiles(messageHandler);
		}
	}
}
