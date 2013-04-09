package com.sabdroidex.activity;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Set;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.os.Environment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SearchViewCompat;
import android.support.v4.widget.SearchViewCompat.OnQueryTextListenerCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.actionbarcompat.ActionBarActivity;
import com.sabdroidex.R;
import com.sabdroidex.adapters.SABDroidExPagerAdapter;
import com.sabdroidex.controllers.sabnzbd.SABnzbdController;
import com.sabdroidex.data.History;
import com.sabdroidex.data.Queue;
import com.sabdroidex.data.SabnzbdStatus;
import com.sabdroidex.data.ShowList;
import com.sabdroidex.fragments.ComingFragment;
import com.sabdroidex.fragments.HistoryFragment;
import com.sabdroidex.fragments.MoviesFragment;
import com.sabdroidex.fragments.QueueFragment;
import com.sabdroidex.fragments.ShowsFragment;
import com.sabdroidex.fragments.dialogs.DialogFragmentManager;
import com.sabdroidex.interfaces.UpdateableActivity;
import com.sabdroidex.utils.ImageUtils;
import com.sabdroidex.utils.ImageUtils.NoMediaChecker;
import com.sabdroidex.utils.Preferences;
import com.sabdroidex.utils.SABDroidConstants;
import com.utils.Calculator;
import com.utils.Formatter;
import com.viewpagerindicator.TabPageIndicator;

/**
 * Main SABDroid Activity
 */
public class SABDroidEx extends ActionBarActivity implements UpdateableActivity {
    
    private static final String TAG = SABDroidEx.class.getCanonicalName();
    private static String APPLICATION_VERSION;
    
    /**
     * The Fragments that will take place in the ViewPager
     */
    private QueueFragment queueFragment;
    private HistoryFragment historyFragment;
    private ShowsFragment showsFragment;
    private ComingFragment comingFragment;
    private MoviesFragment moviesFragment;
    private DialogFragmentManager dialogFragmentManager;
    
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
        NoMediaChecker.check(Environment.getExternalStorageDirectory().getAbsolutePath());
        ImageUtils.initImageWorker(getApplicationContext());
        
        dialogFragmentManager = new DialogFragmentManager(this);
        
        try {
            APPLICATION_VERSION = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        }
        catch (NameNotFoundException e) {
            Log.e(TAG, e.getMessage());
        }
        
        if (!Preferences.get(Preferences.VERSION).equals(APPLICATION_VERSION)) {
            Log.i(TAG, "New version detected : Opening version popup");
            deleteFile(Preferences.DATA_CACHE);
            dialogFragmentManager.showNewVersionDialog();
            Preferences.put(Preferences.VERSION, APPLICATION_VERSION);
        }
        createLists();
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
    
    /**
     * When the activity is stopped the status is saved so that data stays
     * available offline if the data cache has been enabled.
     */
    @Override
    protected void onStop() {
        if (Preferences.isEnabled(Preferences.DATA_CACHE)) {
            /**
             * Saving data for off line use.
             */
            String FILENAME = Preferences.DATA_CACHE;
            FileOutputStream fos = null;
            ObjectOutputStream oos = null;
            try {
                fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
                oos = new ObjectOutputStream(fos);
                oos.writeObject(queueFragment.getDataCache());
                oos.writeObject(historyFragment.getDataCache());
                oos.writeObject(showsFragment.getDataCache());
                oos.writeObject(comingFragment.getDataCache());
            }
            catch (Exception e) {
                Log.e(TAG, " " + e.getLocalizedMessage());
            }
            finally {
                try {
                    oos.close();
                    fos.close();
                }
                catch (Exception e) {
                    // we do not care
                }
            }
        }
        super.onStop();
    }
    
    /**
     * This function will Serialize the current data for reuse the next time the
     * application is reopened
     */
    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        Object data[] = new Object[5];
        data[0] = queueFragment.getDataCache();
        data[1] = historyFragment.getDataCache();
        data[2] = showsFragment.getDataCache();
        data[3] = comingFragment.getDataCache();
        return data;
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
        
        Queue queue = new Queue();
        History history = new History();
        ShowList shows = new ShowList();
        ArrayList<Object[]> coming = new ArrayList<Object[]>();
        
        /**
         * Restoring data if the cache is enabled.
         */
        
        if (Preferences.isEnabled(Preferences.DATA_CACHE)) {
            
            FileInputStream fis = null;
            ObjectInputStream ois = null;
            try {
                fis = openFileInput(Preferences.DATA_CACHE);
                ois = new ObjectInputStream(fis);
                queue = (Queue) ois.readObject();
                history = (History) ois.readObject();
                shows = (ShowList) ois.readObject();
                coming = (ArrayList<Object[]>) ois.readObject();
            }
            catch (Exception e) {
                Log.e(TAG, " " + e.getLocalizedMessage());
            }
            finally {
                try {
                    ois.close();
                    fis.close();
                }
                catch (Exception e) {
                    // we do not care
                }
            }
        }
        
        updateLabels(queue);
        queueFragment = new QueueFragment(this, queue);
        queueFragment.setRetainInstance(true);
        historyFragment = new HistoryFragment(this, history);
        historyFragment.setRetainInstance(true);
        if (Preferences.isEnabled(Preferences.SICKBEARD)) {
            showsFragment = new ShowsFragment(shows);
            showsFragment.setRetainInstance(true);
            comingFragment = new ComingFragment(coming);
            comingFragment.setRetainInstance(true);
        }
        
        SABDroidExPagerAdapter pagerAdapter = new SABDroidExPagerAdapter(getApplicationContext(),
                getSupportFragmentManager());
        pagerAdapter.addFragment(queueFragment);
        pagerAdapter.addFragment(historyFragment);
        if (Preferences.isEnabled(Preferences.SICKBEARD)) {
            pagerAdapter.addFragment(showsFragment);
            pagerAdapter.addFragment(comingFragment);
        }
        
        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setOffscreenPageLimit(0);
        pager.setAdapter(pagerAdapter);
        pager.setPageMargin(5);
        
        TabPageIndicator tabPageIndicator = (TabPageIndicator) findViewById(R.id.indicator);
        tabPageIndicator.setViewPager(pager);
    }
    
    /**
     * Refreshing the queue during startup or on user request. Asks to configure
     * if still not done
     */
    void manualRefresh() {
        Log.v(TAG, "Refreshing");
        // First run setup
        if (!Preferences.isSet(Preferences.SABNZBD_URL)) {
            dialogFragmentManager.showSetupDialog();
            return;
        }
        queueFragment.manualRefreshQueue();
        historyFragment.manualRefreshHistory();
        if (Preferences.isSet(Preferences.SICKBEARD_URL) && Preferences.isEnabled(Preferences.SICKBEARD)) {
            showsFragment.manualRefreshShows();
            comingFragment.manualRefreshComing();
        }
        getActionBarHelper().setRefreshActionItemState(true);
    }
    
    /**
     * This method is used to refresh the contents of the status panel.
     */
    @Override
    public void updateLabels(SabnzbdStatus status) {
        if (status == null)
            return;
        try {
            Double mbleft = new Double(status.getMbLeft());
            Double kbpersec = new Double(status.getKbPerSec());
            String mb = status.getMb();
            String diskspace2 = status.getDiskSpace2();
            
            ((TextView) findViewById(R.id.freeSpace)).setText(Formatter.formatFull(diskspace2) + " "
                    + getString(R.string.header_free_unit));
            ((TextView) findViewById(R.id.headerDownloaded)).setText(Formatter.formatShort(mbleft) + " / "
                    + Formatter.formatShort(Double.parseDouble(mb)) + " MB");
            ((TextView) findViewById(R.id.headerSpeed)).setText(Formatter.formatShort(kbpersec) + " "
                    + getString(R.string.header_speed_unit));
            ((TextView) findViewById(R.id.headerEta)).setText(Calculator.calculateETA(mbleft, kbpersec));
        }
        catch (Exception exception) {
            Log.w(TAG, "Error Updating Labels");
        }
    }
    
    /**
     * This method is used to set the refresh button as active or not
     */
    @Override
    public void updateState(boolean showAsUpdate) {
        getActionBarHelper().setRefreshActionItemState(showAsUpdate);
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
            searchItem.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM
                    | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        }
        View searchView = SearchViewCompat.newSearchView(getApplicationContext());
        if (searchView != null) {
            SearchViewCompat.setOnQueryTextListener(searchView, queryTextListener);
            MenuItem item = menu.findItem(R.id.menu_search);
            MenuItemCompat.setActionView(item, searchView);
        }
    }
    
    /**
     * Listener for the search text field contents.
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
     * This is called each time the Menu Button or Menu UI Element is pressed.
     * It allows to update the menu according to what we need
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
        if (Preferences.isEnabled(Preferences.COUCHPOTATO)) {
            menu.findItem(R.id.menu_couch_settings).setVisible(true);
        }
        else {
            menu.findItem(R.id.menu_couch_settings).setVisible(false);
        }
        if (!Debug.isDebuggerConnected()) {
            menu.findItem(R.id.menu_clear).setVisible(true);
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
            case R.id.menu_settings:
                showSettings();
                break;
            case R.id.menu_play_pause:
                pauseResume();
                break;
            case R.id.menu_add_nzb:
                showAddDialog();
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
            case R.id.menu_couch_settings:
                showCouchSettings();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private void showAddDialog() {
        dialogFragmentManager.showAddDialog();
    }
    
    /**
     * Clears the application version. For testing purposes.
     */
    private void clearVersion() {
        Preferences.put(Preferences.VERSION, "");
        deleteFile(Preferences.DATA_CACHE);
    }
    
    /**
     * Displaying the application settings
     */
    private void showSettings() {
        startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
    }
    
    /**
     * Displaying the Sabnzbd server settings
     */
    private void showServerSettings() {
        startActivity(new Intent(getApplicationContext(), ServerSettingsActivity.class));
    }
    
    /**
     * Displaying the Couchpotato server settings
     */
    private void showCouchSettings() {
        startActivity(new Intent(getApplicationContext(), CouchSettingsActivity.class));
    }
    
    /**
     * This method invokes either the pause or resume functionality of sabnzbd
     * regarding of the known status
     */
    private void pauseResume() {
        SABnzbdController.pauseResumeQueue(queueFragment.getMessageHandler());
    }
    
}
