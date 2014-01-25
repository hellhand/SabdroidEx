package com.sabdroidex.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.sabdroidex.controllers.SABController;
import com.sabdroidex.controllers.couchpotato.CouchPotatoController;
import com.sabdroidex.utils.Preferences;
import com.sabdroidex.utils.SABDroidConstants;
import com.utils.HttpUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Activity for receiving intent action from IMDB application.
 *
 * @author roy
 */
public class CouchPotatoActivity extends Activity {

    private static Context context;
    private final Handler messageHandler = new Handler() {

        public void handleMessage(Message msg) {
            if (msg.what == CouchPotatoController.MESSAGE.UPDATE.hashCode()) {
                if (SABController.MESSAGE.ERROR.equals(msg.obj)) {
                    //TODO: Resources
                    makeToast("Failed to add movie\nCheck settings!");
                    finish();
                } else if (!SABController.MESSAGE.EMPTY.equals(msg.obj)) {
                    //TODO: Resources
                    makeToast("Added: " + msg.obj);
                    finish();
                }
            }

        }
    };
    private Pattern pattern = Pattern.compile("(?<=/)tt[0-9]*");

    /**
     * Print text to screen
     *
     * @param text The text to display in the Toast
     */
    public static void makeToast(String text) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }

    /**
     * Receive intent and if possible add movie to CouchPotato
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = getSharedPreferences(SABDroidConstants.PREFERENCES_KEY, 0);
        Preferences.update(preferences);

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        context = getApplicationContext();
        if (Preferences.isEnabled(Preferences.COUCHPOTATO)) {
            if (Intent.ACTION_SEND.equals(action) && type != null) {
                if (HttpUtil.TEXT_PLAIN.equals(type)) {
                    handleSendIntent(intent);
                    finish();
                }
            }
        } else {
            //TODO resource BUNDLE
            makeToast("Couchpotato is not configured yet.\n Please configure");
            startActivity(new Intent(this, SABDroidEx.class));
            finish();
        }
    }

    /**
     * Handle intent and add to CouchPotato
     *
     * @param intent The Intent containing the imdb id to send to Couchpotato
     */
    private void handleSendIntent(Intent intent) {
        String idMDBi;
        String text = intent.getStringExtra(Intent.EXTRA_TEXT);
        String[] array = text.split("\n");
        String title = array[0];
        Matcher matcher = pattern.matcher(array[1]);
        if (matcher.find()) {
            idMDBi = matcher.group();
            CouchPotatoController.addMovie(messageHandler, Preferences.get(Preferences.COUCHPOTATO_PROFILE), idMDBi, title);
        }
    }
}
