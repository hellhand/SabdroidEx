package com.sabdroidex.activity;

import java.util.ArrayList;
import java.util.Set;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SearchViewCompat;
import android.support.v4.widget.SearchViewCompat.OnQueryTextListenerCompat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.actionbarcompat.ActionBarActivity;
import com.sabdroidex.R;
import com.sabdroidex.adapters.SABDroidExPagerAdapter;
import com.sabdroidex.fragments.HistoryFragment;
import com.sabdroidex.fragments.QueueFragment;
import com.sabdroidex.fragments.SickbeardShowsFragment;
import com.sabdroidex.sabnzbd.SABnzbdController;
import com.sabdroidex.utils.Preferences;
import com.sabdroidex.utils.SABDroidConstants;
import com.utils.Calculator;
import com.utils.Formatter;
import com.viewpagerindicator.TabPageIndicator;

/**
 * Main SABDroid Activity
 */
public class SABDroidEx extends ActionBarActivity implements android.view.View.OnClickListener {

    /**
     * This is the data that will be retrieved and saved each time the application starts and stops is is used as cache.
     */
    private static ArrayList<Object[]> downloadRows = new ArrayList<Object[]>();
    private static ArrayList<Object[]> historyRows = new ArrayList<Object[]>();
    private static ArrayList<Object[]> showsRows = new ArrayList<Object[]>();
    private static JSONObject backupJsonObject = null;
    protected boolean paused = false;

    /**
     * The Fragments that will take place in the ViewPager
     */
    private QueueFragment queue;
    private HistoryFragment history;
    private SickbeardShowsFragment shows;

    SearchViewCompat.OnQueryTextListenerCompat onQueryTextListenerCompat = new SearchViewCompat.OnQueryTextListenerCompat() {

        @Override
        public boolean onQueryTextChange(String newText) {
            // TODO Auto-generated method stub
            System.out.println("Typed text : " + newText);
            return super.onQueryTextChange(newText);
        }

        @Override
        public boolean onQueryTextSubmit(String query) {
            // TODO Auto-generated method stub
            System.out.println("Query text : " + query);
            return super.onQueryTextSubmit(query);
        }
    };

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
        manualRefresh();
    }

    @Override
    public void onNewIntent(final Intent newIntent) {
        super.onNewIntent(newIntent);
        final String queryAction = newIntent.getAction();
        if (Intent.ACTION_SEARCH.equals(queryAction)) {
            doSearchQuery(newIntent, "onNewIntent()");
        }
    }

    @Override
    protected void onResume() {
        /**
         * Checking if SickBeard has been just been disabled
         */
        if (!Preferences.isEnabled(Preferences.SICKBEARD) && shows != null) {
            ViewPager pager = (ViewPager) findViewById(R.id.pager);
            SABDroidExPagerAdapter pagerAdapter = (SABDroidExPagerAdapter) pager.getAdapter();
            if (pagerAdapter.contains(shows)) {
                pagerAdapter.removeFragment(shows);
                shows = null;
            }
            TabPageIndicator tabPageIndicator = (TabPageIndicator) findViewById(R.id.indicator);
            tabPageIndicator.notifyDataSetChanged();
            tabPageIndicator.setCurrentItem(0);
            pager.refreshDrawableState();
        }
        if (Preferences.isEnabled(Preferences.SICKBEARD) && shows == null) {
            ViewPager pager = (ViewPager) findViewById(R.id.pager);
            SABDroidExPagerAdapter pagerAdapter = (SABDroidExPagerAdapter) pager.getAdapter();
            shows = new SickbeardShowsFragment(this, showsRows);
            shows.setRetainInstance(true);
            if (!pagerAdapter.contains(shows)) {
                pagerAdapter.addFragment(shows);
            }
            TabPageIndicator tabPageIndicator = (TabPageIndicator) findViewById(R.id.indicator);
            tabPageIndicator.notifyDataSetChanged();
            tabPageIndicator.setCurrentItem(0);
            pager.refreshDrawableState();
        }
        super.onResume();
    }

    private void doSearchQuery(final Intent queryIntent, final String entryPoint) {
        Bundle bundle = queryIntent.getExtras();
        Set<String> keySet = bundle.keySet();
        System.out.println((String) bundle.get((String) keySet.toArray()[1]));
    }

    /**
     * Creating the whole ViewPager content
     */
    private void createLists() {

        queue = new QueueFragment(this, downloadRows);
        queue.setRetainInstance(true);
        history = new HistoryFragment(this, historyRows);
        history.setRetainInstance(true);
        if (Preferences.isEnabled(Preferences.SICKBEARD)) {
            shows = new SickbeardShowsFragment(this, showsRows);
            shows.setRetainInstance(true);
        }

        SABDroidExPagerAdapter pagerAdapter = new SABDroidExPagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragment(queue);
        pagerAdapter.addFragment(history);
        if (Preferences.isEnabled(Preferences.SICKBEARD)) {
            pagerAdapter.addFragment(shows);
        }

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
    void manualRefresh() {
        // First run setup
        if (!Preferences.isSet("server_url")) {
            showDialog(R.id.dialog_setup_prompt);
            return;
        }
        queue.manualRefreshQueue();
        history.manualRefreshHistory();
        if (Preferences.isEnabled(Preferences.SICKBEARD)) {
            shows.manualRefreshShows();
        }
        getActionBarHelper().setRefreshActionItemState(true);
    }

    /**
     * This will refresh the labels at the top of the screen
     * 
     * @param jsonObject The object which contains the updated data to display
     */
    public void updateLabels(JSONObject jsonObject) {
        if (jsonObject == null)
            return;
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
        }
    }

    View searchItemView;

    /**
     * This creates the menu items as they will be by default
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main, menu);
        setupSearchView(menu);
        return true;
    }

    /**
     * Setting up the Search View
     * 
     * @param menu
     */
    private void setupSearchView(Menu menu) {
        // Place an action bar item for searching.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            MenuItem searchItem = menu.findItem(R.id.menu_search);
            searchItem.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        }
        View searchView = SearchViewCompat.newSearchView(this);
        if (searchView != null) {
            SearchViewCompat.setOnQueryTextListener(searchView, queryTextListener);
            MenuItem item = menu.findItem(R.id.menu_search);
            MenuItemCompat.setActionView(item, searchView);
        }
    }

    OnQueryTextListenerCompat queryTextListener = new OnQueryTextListenerCompat() {

        @Override
        public boolean onQueryTextChange(String newText) {
            System.out.println(newText);
            return super.onQueryTextChange(newText);
        }

        @Override
        public boolean onQueryTextSubmit(String query) {
            System.out.println(query);
            return super.onQueryTextSubmit(query);
        }
    };

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
                manualRefresh();
                return true;
            case R.id.menu_quit:
                finish();
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
            case R.id.menu_search:
                onSearchRequested();
                return true;
        }
        return super.onOptionsItemSelected(item);
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
     * Creating of the global Dialogs Specific dialogs can be handled here too if possible
     */
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case R.id.dialog_setup_prompt:

                OnClickListener clickListener = new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (whichButton == Dialog.BUTTON1) {
                            showSettings();
                            manualRefresh();
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(SABDroidEx.this);
                builder.setTitle(R.string.config);
                builder.setPositiveButton(android.R.string.ok, clickListener);
                builder.setNegativeButton(android.R.string.cancel, clickListener);

                return builder.create();
        }
        return null;
    }

    /**
     * This function will Serialize the current data for reuse the next time it is restarted
     */
    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        Object data[] = new Object[4];
        data[0] = downloadRows;
        data[1] = historyRows;
        data[2] = showsRows;
        data[3] = backupJsonObject;
        return data;
    }

    @Override
    protected void onPause() {

        super.onPause();
    }

    /**
     * This function updates the Icon on the screen according to the message received
     * 
     * @param message
     */
    public void updateStatus(String message) {
        if (message.equals(SABnzbdController.MESSAGE.UPDATE.toString())) {
            getActionBarHelper().setRefreshActionItemState(true);
        }
        else {
            getActionBarHelper().setRefreshActionItemState(false);
        }
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub

    }

}
