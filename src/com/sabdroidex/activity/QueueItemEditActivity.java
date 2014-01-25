package com.sabdroidex.activity;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.actionbarcompat.ActionBarPreferencesActivity;
import com.sabdroidex.R;
import com.sabdroidex.controllers.sabnzbd.SABnzbdController;
import com.sabdroidex.data.sabnzbd.Categories;
import com.sabdroidex.data.sabnzbd.QueueElement;
import com.sabdroidex.data.sabnzbd.Scripts;

/**
 * Created by Marc on 27/12/13.
 */
public class QueueItemEditActivity extends ActionBarPreferencesActivity {

    private static final String TAG = ShowActivity.class.getCanonicalName();
    private static final String ELEMENT = "element";

    private QueueElement element;

    /**
     * Instantiating the Handler associated with this {@link android.app.Activity}. It will
     * be notified when the request to retrieve the show data is successful
     */
    private final Handler messageHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == SABnzbdController.MESSAGE.GET_CATS .hashCode() && msg.obj instanceof Categories) {
                Categories categories = (Categories) msg.obj;
                QueueItemEditActivity.this.updateCategories(categories);
            }
            if (msg.what == SABnzbdController.MESSAGE.GET_SCRIPTS.hashCode() && msg.obj instanceof Scripts) {
                Scripts scripts = (Scripts) msg.obj;
                QueueItemEditActivity.this.updateScripts(scripts);
            }
        }
    };

    /**
     *
     * @param categories
     */
    private void updateCategories(Categories categories) {
        Spinner categorySpinner = (Spinner) findViewById(R.id.download_category);
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, categories.getCategories());
        categorySpinner.setAdapter(adapter);
        categorySpinner.setSelection(categories.getCategories().indexOf(element.getCategory()));
    }

    /**
     *
     * @param scripts
     */
    private void updateScripts(Scripts scripts) {
        Spinner scriptSpinner = (Spinner) findViewById(R.id.download_processing);
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, scripts.getScripts());
        scriptSpinner.setAdapter(adapter);
        scriptSpinner.setSelection(scripts.getScripts().indexOf(element.getScript()));
    }

    private SharedPreferences preferences;
    private TextView empty;

    /**
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        setContentView(R.layout.download_settings);

        element = (QueueElement) getIntent().getExtras().get(ELEMENT);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        EditText editText = (EditText) findViewById(R.id.download_name);
        editText.setText(element.getFilename());

        SABnzbdController.getCategories(messageHandler);
        SABnzbdController.getScripts(messageHandler);
    }

    /**
     * Handles item selections in the Menu
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
