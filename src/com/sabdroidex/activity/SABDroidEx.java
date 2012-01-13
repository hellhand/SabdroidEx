package com.sabdroidex.activity;

import java.util.ArrayList;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.android.actionbarcompat.ActionBarActivity;
import com.sabdroidex.R;
import com.sabdroidex.activity.adapters.SABDroidExPagerAdapter;
import com.sabdroidex.fragments.HistoryFragment;
import com.sabdroidex.fragments.QueueFragment;
import com.sabdroidex.sabnzbd.SABnzbdController;
import com.sabdroidex.utils.Preferences;
import com.sabdroidex.utils.SABDroidConstants;
import com.utils.Calculator;
import com.utils.Formatter;
import com.viewpagerindicator.TabPageIndicator;

/**
 * Main SABDroid Activity
 */
public class SABDroidEx extends ActionBarActivity {

    /**
     * This is the data that will be retrieved and saved each time the application starts and stops is is used as cache.
     */
    private static ArrayList<String> downloadRows = new ArrayList<String>();
    private static ArrayList<String> historyRows = new ArrayList<String>();
    private static JSONObject backupJsonObject;
    protected boolean paused = false;

    /**
     * The two Fragments that will take place in the ViewPager
     */
    private QueueFragment queue;
    private HistoryFragment history;

    /**
     * Creating the elements of the screen
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.header);

        SharedPreferences preferences = getSharedPreferences(SABDroidConstants.PREFERENCES_KEY, 0);
        Preferences.update(preferences);

        createLists();
        manualRefreshQueue();
    }

    /**
     * Creating the whole ViewPager content
     */
    private void createLists() {

        queue = new QueueFragment(this, downloadRows);
        history = new HistoryFragment(this, historyRows);

        SABDroidExPagerAdapter pagerAdapter = new SABDroidExPagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragment(queue);
        pagerAdapter.addFragment(history);

        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(pagerAdapter);
        pager.setPageMargin(5);

        TabPageIndicator tabPageIndicator = (TabPageIndicator) findViewById(R.id.indicator);
        tabPageIndicator.setViewPager(pager);
    }

    /**
     * This function will serve as a retriever to get back the wanted data from the serialized object
     * 
     * @param data The previously Serialized cache Object[]
     * @param osition The position of the object in the array to recover
     * @return The object in the array to recover
     */
    @SuppressWarnings("unchecked")
    ArrayList<String> extracted(Object[] data, int position) {
        return (ArrayList<String>) data[position];
    }

    /**
     * Refreshing the queue during startup or on user request. Asks to configure if still not done
     */
    void manualRefreshQueue() {
        // First run setup
        if (!Preferences.isSet("server_url")) {
            showDialog(R.id.dialog_setup_prompt);
            return;
        }
        queue.manualRefreshQueue();
        history.manualRefreshHistory();
    }

    /**
     * This will refresh the labels at the top of the screen
     * 
     * @param jsonObject The object which contains the updated data to display
     */
    public void updateLabels(JSONObject jsonObject) {
        try {
            Double mbleft = jsonObject.getDouble("mbleft");
            Double kbpersec = jsonObject.getDouble("kbpersec");
            String mb = jsonObject.getString("mb");
            String diskspace2 = jsonObject.getString("diskspace2");

            ((TextView) findViewById(R.id.freeSpace)).setText(Formatter.formatFull(diskspace2) + " GB");
            ((TextView) findViewById(R.id.headerDownloaded)).setText(Formatter.formatShort(mbleft) + " / " + Formatter.formatShort(Double.parseDouble(mb))
                    + " MB");
            ((TextView) findViewById(R.id.headerSpeed)).setText(Formatter.formatShort(kbpersec) + " KB/s");
            ((TextView) findViewById(R.id.headerEta)).setText(Calculator.calculateETA(mbleft, kbpersec));
        }
        catch (Throwable e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * This creates the menu items as they will be by default
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * This is called each time the Menu Button or Menu UI Element is pressed. It allows to update the menu according to what we need
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (SABnzbdController.paused == true) {
            menu.findItem(R.id.menu_play_pause).setTitle(R.string.menu_resume);
            menu.findItem(R.id.menu_play_pause).setIcon(android.R.drawable.ic_media_play);
        }
        else {
            menu.findItem(R.id.menu_play_pause).setTitle(R.string.menu_pause);
            menu.findItem(R.id.menu_play_pause).setIcon(android.R.drawable.ic_media_pause);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * Handles item selections in the Menu
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                manualRefreshQueue();
                return true;
            case R.id.menu_quit:
                System.exit(1);
                return true;
            case R.id.menu_settings:
                showSettings();
                return true;
            case R.id.menu_play_pause:
                SABnzbdController.pauseResumeQueue(queue.getMessageHandler());
                return true;
            case R.id.menu_add_nzb:
                addDownloadPrompt();
                return true;
        }
        return false;
    }

    /**
     * Displays the Props dialog when the user wants to add a download
     */
    private void addDownloadPrompt() {
        /**
         * If nothing is configured we display the configuration pop-up
         */
        if (!Preferences.isSet("server_url")) {
            showDialog(R.id.dialog_setup_prompt);
            return;
        }

        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle(R.string.add_dialog_title);
        alert.setMessage(R.string.add_dialog_message);

        final EditText input = new EditText(this);
        alert.setView(input);

        alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString();
                SABnzbdController.addFile(queue.getMessageHandler(), value);
            }
        });

        alert.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.show();
    }

    /**
     * Displaying the application settings
     */
    private void showSettings() {
        startActivity(new Intent(this, SettingsActivity.class));
    }

    /**
     * Creating of the Settup Dialog
     */
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case R.id.dialog_setup_prompt:

                OnClickListener okListener = new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        showSettings();
                        manualRefreshQueue();
                    }
                };

                OnClickListener noListener = new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(SABDroidEx.this);
                builder.setTitle(R.string.config);
                builder.setPositiveButton(android.R.string.ok, okListener);
                builder.setNegativeButton(android.R.string.cancel, noListener);

                return builder.create();
        }
        return null;
    }

    /**
     * This function will Serialize the current data for reuse the next time it is restarted
     */
    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        Object data[] = new Object[2];
        data[0] = downloadRows;
        data[1] = historyRows;
        data[2] = backupJsonObject;
        return data;
    }

    /**
     * This function updates the Icon on the screen according to the message received
     * 
     * @param message
     */
    public void updateStatus(String message) {
        if (message.equals(SABnzbdController.MESSAGE.UPDATING.toString())) {
            getActionBarHelper().setRefreshActionItemState(true);
        }
        else {
            getActionBarHelper().setRefreshActionItemState(false);
        }
    }
}
