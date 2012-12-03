package com.sabdroidex.activity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.sabdroidex.controllers.couchpotato.CouchPotatoController;
import com.sabdroidex.utils.Preferences;
import com.sabdroidex.utils.SABDroidConstants;

public class CouchPotatoActivity extends Activity {
	
	private static Context context;
	private Pattern pattern = Pattern.compile("(?<=/)tt[0-9]*");
	
   private final Handler messageHandler = new Handler() {

        public void handleMessage(Message msg) {
            if (msg.what == CouchPotatoController.MESSAGE.UPDATE.ordinal()) {
            	if ("Error".equals(msg.obj)){
             		makeToast("Failed to add movie\nCheck settings!");
             		finish();
             	}
            	else if(!"".equals(msg.obj)){
            		makeToast("Added: "+msg.obj);
            		finish();
            	}
            }

        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        SharedPreferences preferences = getSharedPreferences(SABDroidConstants.PREFERENCES_KEY, 0);
        Preferences.update(preferences);
        
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        context = getApplicationContext();
        if(Preferences.isEnabled(Preferences.COUCHPOTATO)){
		    if (Intent.ACTION_SEND.equals(action) && type != null) {
		        if ("text/plain".equals(type)) {
		        	handleSendIntent(intent);
		        	finish();
		        }
		    }
        }else{
        	makeToast("Couchpotato is not configured yet.\n Please configure");
        	startActivity(new Intent(this,SABDroidEx.class));
        	finish();
        }
    }

    private void handleSendIntent(Intent intent){
    	String idMDBi;
    	String text = intent.getStringExtra(Intent.EXTRA_TEXT);
    	String [] array = text.split("\n");
		String title = array[0];
    	Matcher matcher = pattern.matcher(array[1]);
    	if(matcher.find()){
    		idMDBi = matcher.group();
    		CouchPotatoController.addMovie(messageHandler,Preferences.get(Preferences.COUCHPOTATO_PROFILE),idMDBi, title);
    	}
    }
    
    public static void makeToast(String text){
    	Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }
}
