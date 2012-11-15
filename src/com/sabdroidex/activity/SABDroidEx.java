package com.sabdroidex.activity;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Set;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SearchViewCompat;
import android.support.v4.widget.SearchViewCompat.OnQueryTextListenerCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.TextView;

import com.android.actionbarcompat.ActionBarActivity;
import com.sabdroidex.R;
import com.sabdroidex.adapters.SABDroidExPagerAdapter;
import com.sabdroidex.fragments.ComingFragment;
import com.sabdroidex.fragments.HistoryFragment;
import com.sabdroidex.fragments.MoviesFragment;
import com.sabdroidex.fragments.QueueFragment;
import com.sabdroidex.fragments.ShowsFragment;
import com.sabdroidex.sabnzbd.SABnzbdController;
import com.sabdroidex.utils.Preferences;
import com.sabdroidex.utils.RawReader;
import com.sabdroidex.utils.SABDroidConstants;
import com.utils.Calculator;
import com.utils.Formatter;
import com.viewpagerindicator.TabPageIndicator;

/**
 * Main SABDroid Activity
 */
public class SABDroidEx extends ActionBarActivity implements OnLongClickListener {

    private static final String TAG = SABDroidEx.class.getSimpleName();
    
    /**
     * This is the data that will be retrieved and saved each time the application starts and stops is is used as cache.
     */
    private static ArrayList<Object[]> downloadRows = new ArrayList<Object[]>();
    private static ArrayList<Object[]> historyRows = new ArrayList<Object[]>();
    private static ArrayList<Object[]> showsRows = new ArrayList<Object[]>();
    private static ArrayList<Object[]> comingRows = new ArrayList<Object[]>();
    private static String APPLICATION_VERSION;

    /**
     * The Fragments that will take place in the ViewPager
     */
    private QueueFragment queue;
    private HistoryFragment history;
    private ShowsFragment shows;
    private ComingFragment coming;
    private MoviesFragment movies;

    /**
     * Creating the elements of the screen
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Log.v(TAG, "Starting SABDroidEx");
        
        setContentView(R.layout.header);

        SharedPreferences preferences = getSharedPreferences(SABDroidConstants.PREFERENCES_KEY, 0);
        Preferences.update(preferences);

        try {
            APPLICATION_VERSION = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
        }
        catch (NameNotFoundException e) {
            Log.e(TAG, e.getMessage());
        }

        if (!Preferences.get(Preferences.VERSION).equals(APPLICATION_VERSION)) {
            Log.i(TAG, "New version detected : Opening vestion popup");
            //deleteFile(Preferences.DATA_CACHE);
            showVersionUpdatePopUp();
            Preferences.put(Preferences.VERSION, APPLICATION_VERSION);
        }

        createLists();
        manualRefresh();
    }

    @Override
    public void onNewIntent(final Intent newIntent) {
        super.onNewIntent(newIntent);
        Log.v(TAG, "New Intent received : " + newIntent.getAction());
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
        if (!Preferences.isEnabled(Preferences.SICKBEARD) && coming != null) {
            ViewPager pager = (ViewPager) findViewById(R.id.pager);
            SABDroidExPagerAdapter pagerAdapter = (SABDroidExPagerAdapter) pager.getAdapter();
            if (pagerAdapter.contains(coming)) {
                pagerAdapter.removeFragment(coming);
                coming = null;
            }
            TabPageIndicator tabPageIndicator = (TabPageIndicator) findViewById(R.id.indicator);
            tabPageIndicator.notifyDataSetChanged();
            tabPageIndicator.setCurrentItem(0);
            pager.refreshDrawableState();
        }
        if (Preferences.isEnabled(Preferences.SICKBEARD) && shows == null) {
            ViewPager pager = (ViewPager) findViewById(R.id.pager);
            SABDroidExPagerAdapter pagerAdapter = (SABDroidExPagerAdapter) pager.getAdapter();
            shows = new ShowsFragment(this, showsRows);
            shows.setRetainInstance(true);
            if (!pagerAdapter.contains(shows)) {
                pagerAdapter.addFragment(shows);
            }
            TabPageIndicator tabPageIndicator = (TabPageIndicator) findViewById(R.id.indicator);
            tabPageIndicator.notifyDataSetChanged();
            tabPageIndicator.setCurrentItem(0);
            pager.refreshDrawableState();
        }
        if (Preferences.isEnabled(Preferences.SICKBEARD) && coming == null) {
            ViewPager pager = (ViewPager) findViewById(R.id.pager);
            SABDroidExPagerAdapter pagerAdapter = (SABDroidExPagerAdapter) pager.getAdapter();
            coming = new ComingFragment(this, comingRows);
            coming.setRetainInstance(true);
            if (!pagerAdapter.contains(coming)) {
                pagerAdapter.addFragment(coming);
            }
            TabPageIndicator tabPageIndicator = (TabPageIndicator) findViewById(R.id.indicator);
            tabPageIndicator.notifyDataSetChanged();
            tabPageIndicator.setCurrentItem(0);
            pager.refreshDrawableState();
        }
        super.onResume();
    }

    @Override
    protected void onStop() {
        if (Preferences.isEnabled(Preferences.DATA_CACHE)) {
            /**
             * Saving data for offline use.
             */
            String FILENAME = Preferences.DATA_CACHE;
            FileOutputStream fos = null;
            try {
                fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(downloadRows);
                oos.writeObject(historyRows);
                oos.writeObject(showsRows);
                oos.writeObject(comingRows);
            }
            catch (Exception e) {
                Log.e(TAG, " " + e.getLocalizedMessage());
            }
        }
        super.onStop();
    }

    /**
     * 
     * @param queryIntent
     * @param entryPoint
     */
    private void doSearchQuery(final Intent queryIntent, final String entryPoint) {
        Bundle bundle = queryIntent.getExtras();
        Set<String> keySet = bundle.keySet();
        System.out.println((String) bundle.get((String) keySet.toArray()[1]));
    }

    /**
     * Creating the whole ViewPager content
     */
    @SuppressWarnings("unchecked")
    private void createLists() {

        if (Preferences.isEnabled(Preferences.DATA_CACHE)) {

            /**
             * Restoring data.
             */

            FileInputStream fis = null;
            try {
                fis = openFileInput(Preferences.DATA_CACHE);
                ObjectInputStream ois = new ObjectInputStream(fis);
                downloadRows = (ArrayList<Object[]>) ois.readObject();
                historyRows = (ArrayList<Object[]>) ois.readObject();
                showsRows = (ArrayList<Object[]>) ois.readObject();
                comingRows = (ArrayList<Object[]>) ois.readObject();
            }
            catch (Exception e) {
                Log.e(TAG, " " + e.getLocalizedMessage());
            }
        }
        
        queue = new QueueFragment(this, downloadRows);
        queue.setRetainInstance(true);
        history = new HistoryFragment(this, historyRows);
        history.setRetainInstance(true);
        movies = new MoviesFragment(this);
        if (Preferences.isEnabled(Preferences.SICKBEARD)) {
            shows = new ShowsFragment(this, showsRows);
            shows.setRetainInstance(true);
            coming = new ComingFragment(this, comingRows);
            coming.setRetainInstance(true);
        }

        SABDroidExPagerAdapter pagerAdapter = new SABDroidExPagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragment(queue);
        pagerAdapter.addFragment(history);
        if (Preferences.isEnabled(Preferences.SICKBEARD)) {
            pagerAdapter.addFragment(shows);
            pagerAdapter.addFragment(coming);
        }

        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(pagerAdapter);
        pager.setPageMargin(5);

        TabPageIndicator tabPageIndicator = (TabPageIndicator) findViewById(R.id.indicator);
        tabPageIndicator.setViewPager(pager);

        View statusBar = (View) findViewById(R.id.statusBar);
        statusBar.setOnLongClickListener(this);
    }

    /**
     * Refreshing the queue during startup or on user request. Asks to configure if still not done
     */
    @SuppressWarnings("deprecation")
    void manualRefresh() {
        Log.v(TAG, "Refreshing");
        // First run setup
        if (!Preferences.isSet(Preferences.SABNZBD_URL)) {
            showDialog(R.id.dialog_setup_prompt);
            return;
        }
        queue.manualRefreshQueue();
        history.manualRefreshHistory();
        if (Preferences.isSet(Preferences.SICKBEARD_URL) && Preferences.isEnabled(Preferences.SICKBEARD)) {
            shows.manualRefreshShows();
            coming.manualRefreshComing();
        }
        getActionBarHelper().setRefreshActionItemState(true);
    }

    /**
     * This will refresh the labels at the top of the screen
     * 
     * @param jsonObject
     *            The object which contains the updated data to display
     */
    public void updateLabels(JSONObject jsonObject) {
        if (jsonObject == null)
            return;
        try {
            Double mbleft = jsonObject.getDouble("mbleft");
            Double kbpersec = jsonObject.getDouble("kbpersec");
            String mb = jsonObject.getString("mb");
            String diskspace2 = jsonObject.getString("diskspace2");

            ((TextView) findViewById(R.id.freeSpace)).setText(Formatter.formatFull(diskspace2) + " " + getString(R.string.header_free_unit));
            ((TextView) findViewById(R.id.headerDownloaded)).setText(Formatter.formatShort(mbleft) + " / " + Formatter.formatShort(Double.parseDouble(mb))
                    + " MB");
            ((TextView) findViewById(R.id.headerSpeed)).setText(Formatter.formatShort(kbpersec) + " " + getString(R.string.header_speed_unit));
            ((TextView) findViewById(R.id.headerEta)).setText(Calculator.calculateETA(mbleft, kbpersec));
        }
        catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * This creates the menu items as they will be by default
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main, menu);
        setupSearchView(menu);
        return super.onCreateOptionsMenu(menu);
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

    /**
     * Listener for the query text.
     */
    OnQueryTextListenerCompat queryTextListener = new OnQueryTextListenerCompat() {

        @Override
        public boolean onQueryTextChange(String newText) {
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
        if (!Debug.isDebuggerConnected()) {
            menu.findItem(R.id.menu_clear).setVisible(false);
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
                break;
            case R.id.menu_quit:
                finish();
                break;
            case R.id.menu_settings:
                showSettings();
                break;
            case R.id.menu_play_pause:
                SABnzbdController.pauseResumeQueue(queue.getMessageHandler());
                break;
            case R.id.menu_add_nzb:
                addPrompt();
                break;
            case R.id.menu_search:
                onSearchRequested();
                break;
            case R.id.menu_clear:
                clearVersion();
                break;
            case R.id.menu_server_settings:
                showServerSettings();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Clears the application version. For testing purposes.
     */
    private void clearVersion() {
        Preferences.put(Preferences.VERSION, "");
    }

    /**
     * Shows the add popup for the different elements.
     */
    private void addPrompt() {

        OnClickListener onClickListener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setNegativeButton(android.R.string.cancel, onClickListener);

        String[] options = null;
        if (Preferences.isEnabled(Preferences.COUCHPOTATO)){
        	options = new String[3];
            options[0] = getResources().getString(R.string.add_nzb_dialog_title);
            options[2] = getResources().getString(R.string.add_movie_dialog_title);
            options[1] = getResources().getString(R.string.add_show_dialog_title);
        }
        else if (Preferences.isEnabled(Preferences.SICKBEARD)) {
            options = new String[2];
            options[0] = getResources().getString(R.string.add_nzb_dialog_title);
            options[1] = getResources().getString(R.string.add_show_dialog_title);
        }
        else {
            options = new String[1];
            options[0] = getResources().getString(R.string.add_nzb_dialog_title);
        }
        builder.setItems(options, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        queue.addDownloadPrompt();
                        break;
                    case 1:
                        shows.addShowPrompt();
                        break;
                    case 2:
                    	movies.addMoviePrompt();
                    	break;
                    default:
                        break;
                }
            }
        });

        AlertDialog dialog = null;
        dialog = builder.create();
        dialog.show();
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
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case R.id.dialog_setup_prompt:

                OnClickListener clickListener = new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (whichButton == Dialog.BUTTON_POSITIVE) {
                            showSettings();
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
     * This method creates the pop-up that is displayed when a new version of the application is installed
     */
    private void showVersionUpdatePopUp() {
        OnClickListener clickListener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        };

        String versionInfo = RawReader.readTextRaw(getApplicationContext(), R.raw.version_info);

        AlertDialog.Builder builder = new AlertDialog.Builder(SABDroidEx.this);
        builder.setTitle(R.string.new_version);
        builder.setPositiveButton(android.R.string.ok, clickListener);
        builder.setMessage(versionInfo);

        AlertDialog dialog = null;
        dialog = builder.create();
        dialog.show();

        TextView messageView = (TextView) dialog.findViewById(android.R.id.message);
        messageView.setGravity(Gravity.LEFT);
    }

    /**
     * This function will Serialize the current data for reuse the next time the application is reopened
     */
    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        Object data[] = new Object[5];
        data[0] = downloadRows;
        data[1] = historyRows;
        data[2] = showsRows;
        data[3] = comingRows;
        return data;
    }

    /**
     * This function updates the Refresh Icon on the screen according to the message received
     * 
     * @param message
     */
    public void updateStatus(boolean showAsUpdate) {
        getActionBarHelper().setRefreshActionItemState(showAsUpdate);
    }

    @Override
    public boolean onLongClick(View v) {

        /**
         * If a long click is done on the status bar this opens the
         */
        if (v.getId() == R.id.statusBar) {
            showServerSettings();
            return true;
        }

        return false;
    }

    private void showServerSettings() {
        startActivity(new Intent(this, ServerSettingsActivity.class));
    }
}
