package com.sabdroidex.fragments.dialogs;

import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.sabdroidex.R;
import com.sabdroidex.controllers.sickbeard.SickBeardController;
import com.sabdroidex.data.ShowSearch;
import com.sabdroidex.utils.SABHandler;

public class DialogFragmentManager implements DialogActionsListener {
    
    private static final String TAG = DialogFragmentManager.class.getCanonicalName();
    
    /**
     * Instantiating the Handler associated with this
     * {@link DialogFragmentManager}.
     */
    private final SABHandler messageHandler = new SABHandler() {
        
        public void handleMessage(android.os.Message msg) {
            if (msg.what == SickBeardController.MESSAGE.SB_SEARCHTVDB.ordinal()) {
                try {
                    ShowSearch showSearch = (ShowSearch) msg.obj;
                    showAddShowSelectionDialog(showSearch);
                }
                catch (Exception e) {
                    Log.w(TAG, e.getLocalizedMessage());
                }
            }
            if (msg.what == SickBeardController.MESSAGE.SHOW_ADDNEW.ordinal()) {
                try {
                    String text = mActivity.getString(R.string.add_show_dialog_title) + " : " + msg.obj;
                    Toast.makeText(mActivity, text, Toast.LENGTH_LONG).show();
                }
                catch (Exception e) {
                    Log.w(TAG, e.getLocalizedMessage());
                }
            }
        };
    };
    
    /**
     * This is the parent activity which is used to summon the dialogs.
     */
    private FragmentActivity mActivity;
    
    /**
     * Constructor
     * 
     * @param activity
     */
    public DialogFragmentManager(FragmentActivity activity) {
        this.mActivity = activity;
    }
    
    /**
     * Displays a dialog proposing to add a nzb, show, movie, ...
     */
    public void showAddDialog() {
        AddDialog addDialog = new AddDialog(this);
        addDialog.show(mActivity.getSupportFragmentManager(), "add");
    }
    
    /**
     * Displays a dialog to add a nzb to sabnzbd
     */
    @Override
    public void showAddNzbDialog() {
        AddNzbDialog addNzbDialog = new AddNzbDialog(messageHandler);
        addNzbDialog.show(mActivity.getSupportFragmentManager(), "addnzb");
    }
    
    /**
     * Displays a pop-up dialog when the user wants to add a show to SickBeard
     */
    @Override
    public void showAddShowDialog() {
        AddShowDialog addShowDialog = new AddShowDialog(messageHandler);
        addShowDialog.show(mActivity.getSupportFragmentManager(), "addshow");
    }
    
    /**
     * Displays the propositions dialog with the resulting show names found
     * after a user search to add a show to Sickbeard.
     * 
     * @param showSearch
     *            The result of the search query
     */
    private void showAddShowSelectionDialog(final ShowSearch showSearch) {
        AddShowSelectDialog addShowSelectDialog = new AddShowSelectDialog(messageHandler, showSearch);
        addShowSelectDialog.show(mActivity.getSupportFragmentManager(), "selectshow");
    }
    
    /**
     * Displays a dialog to invite the user to setup his configuration
     */
    @Override
    public void showSetupDialog() {
        SetupDialog setupDialog = new SetupDialog();
        setupDialog.show(mActivity.getSupportFragmentManager(), "setup");
    }
    
    /**
     * Displays a dialog when the application has been updated, it usually
     * contains a description of what has been modified in the new version
     */
    @Override
    public void showNewVersionDialog() {
        NewVersionDialog newVersionDialog = new NewVersionDialog();
        newVersionDialog.show(mActivity.getSupportFragmentManager(), "newversion");
    }
    
    @Override
    public void showAddMovieDialog() {
        // TODO Auto-generated method stub
        
    }
}
