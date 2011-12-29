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
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.sabdroidex.Preferences;
import com.sabdroidex.R;
import com.sabdroidex.SABDroidConstants;
import com.sabdroidex.activity.adapters.SABDroidExPagerAdapter;
import com.sabdroidex.sabnzbd.SABnzbdController;
import com.sabdroidex.util.Calculator;
import com.sabdroidex.util.Formatter;
import com.viewpagerindicator.TabPageIndicator;

/**
 * Main SABDroid Activity
 */
public class SABDroidEx extends FragmentActivity {

    /**
     * Menu IDentifiers
     */
    private static final int MENU_REFRESH = 1;
    private static final int MENU_SETTINGS = 2;
    private static final int MENU_QUIT = 3;
    private static final int MENU_PLAY_PAUSE = 4;
    private static final int MENU_ADD_NZB = 5;

    final static int DIALOG_SETUP_PROMPT = 999;

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
    private Queue queue;
    private History history;
    private Search search;

    /**
     * Creating the elements of the screen
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
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

        queue = new Queue(this);
        history = new History(this);
        search = new Search(this);

        SABDroidExPagerAdapter pagerAdapter = new SABDroidExPagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragment(queue);
        pagerAdapter.addFragment(history);
        pagerAdapter.addFragment(search);

        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(pagerAdapter);
        pager.setPageMargin(10);

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

    @Override
    protected void onPause() {
        super.onPause();
        paused = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        paused = false;
    }

    /**
     * Refreshing the queue during startup or on user request. Asks to configure if still not done
     */
    void manualRefreshQueue() {
        // First run setup
        if (!Preferences.isSet("server_url")) {
            showDialog(DIALOG_SETUP_PROMPT);
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
    void updateLabels(JSONObject jsonObject) {
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
        menu.add(0, MENU_REFRESH, 0, R.string.refresh).setIcon(android.R.drawable.ic_menu_share);
        menu.add(0, MENU_PLAY_PAUSE, 0, R.string.resume).setIcon(android.R.drawable.ic_media_play);
        menu.add(0, MENU_ADD_NZB, 0, R.string.add).setIcon(android.R.drawable.ic_menu_add);
        menu.add(0, MENU_SETTINGS, 0, R.string.settings).setIcon(android.R.drawable.ic_menu_preferences);
        menu.add(0, MENU_QUIT, 0, R.string.quit).setIcon(android.R.drawable.ic_menu_close_clear_cancel);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * This is called each time the Menu Button or Menu UI Element is pressed. It allows to update the menu according to what we need
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (SABnzbdController.paused == true) {
            menu.findItem(MENU_PLAY_PAUSE).setTitle(R.string.resume);
            menu.findItem(MENU_PLAY_PAUSE).setIcon(android.R.drawable.ic_media_play);
        }
        else {
            menu.findItem(MENU_PLAY_PAUSE).setTitle(R.string.pause);
            menu.findItem(MENU_PLAY_PAUSE).setIcon(android.R.drawable.ic_media_pause);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * Handles item selections in the Menu
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_REFRESH:
                manualRefreshQueue();
                return true;
            case MENU_QUIT:
                System.exit(1);
                return true;
            case MENU_SETTINGS:
                showSettings();
                return true;
            case MENU_PLAY_PAUSE:
                SABnzbdController.pauseResumeQueue(queue.getMessageHandler());
                return true;
            case MENU_ADD_NZB:
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
            showDialog(DIALOG_SETUP_PROMPT);
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
            case DIALOG_SETUP_PROMPT:

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
    void updateStatus(String message) {
        if (message.equals(SABnzbdController.MESSAGE.UPDATING.toString())) {
            ImageView status = (ImageView) findViewById(R.id.countIcon);
            status.setImageResource(R.drawable.icon_green);
        }
        else if (SABnzbdController.paused == true) {
            ImageView status = (ImageView) findViewById(R.id.countIcon);
            status.setImageResource(R.drawable.icon_grey);
        }
        else if (message.equals("")) {
            ImageView status = (ImageView) findViewById(R.id.countIcon);
            status.setImageResource(R.drawable.icon);
        }
    }
}
