package com.sabdroidex.fragments.dialogs;

import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.sabdroidex.R;
import com.sabdroidex.controllers.couchpotato.CouchPotatoController;
import com.sabdroidex.controllers.sickbeard.SickBeardController;
import com.sabdroidex.data.couchpotato.MovieSearch;
import com.sabdroidex.data.sickbeard.ShowSearch;
import com.sabdroidex.fragments.dialogs.couchpotato.AddMovieDialog;
import com.sabdroidex.fragments.dialogs.couchpotato.AddMovieSelectDialog;
import com.sabdroidex.fragments.dialogs.sabnzbd.AddNzbDialog;
import com.sabdroidex.fragments.dialogs.sabnzbd.AddNzbFileDialog;
import com.sabdroidex.fragments.dialogs.sickbeard.AddShowDialog;
import com.sabdroidex.fragments.dialogs.sickbeard.AddShowSelectDialog;
import com.sabdroidex.utils.SABHandler;

public class DialogFragmentManager implements DialogActionsListener {

    private static final String TAG = DialogFragmentManager.class.getCanonicalName();
    /**
     * This is the parent activity which is used to summon the dialogs.
     */
    private static FragmentActivity mActivity;
    /**
     * Instantiating the Handler associated with this
     * {@link DialogFragmentManager}.
     */
    private final SABHandler messageHandler = new SABHandler() {

        public void handleMessage(android.os.Message msg) {
            if (msg.what == SickBeardController.MESSAGE.SB_SEARCHTVDB.hashCode()) {
                try {
                    ShowSearch showSearch = (ShowSearch) msg.obj;
                    showAddShowSelectionDialog(showSearch);
                } catch (Exception e) {
                    Log.w(TAG, e.getLocalizedMessage());
                }
            }
            if (msg.what == SickBeardController.MESSAGE.SHOW_ADDNEW.hashCode()) {
                try {
                    String text = mActivity.getString(R.string.add_show_dialog_title) + " : " + msg.obj;
                    Toast.makeText(mActivity, text, Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Log.w(TAG, e.getLocalizedMessage());
                }
            }
            if (msg.what == CouchPotatoController.MESSAGE.MOVIE_SEARCH.hashCode()) {
                try {
                    MovieSearch movieSearch = (MovieSearch) msg.obj;
                    showAddMovieSelectionDialog(movieSearch);
                } catch (Exception e) {
                    Log.w(TAG, e.getLocalizedMessage());
                }

            }
            if (msg.what == CouchPotatoController.MESSAGE.MOVIE_ADD.hashCode()) {
                // TODO: USE the resource bundle no hard coded strings !!!!!!
                if ("Error".equals(msg.obj)) {
                    Toast.makeText(mActivity, "Failed to add movie\nCheck settings!", Toast.LENGTH_LONG).show();
                } else if (!"".equals(msg.obj)) {
                    Toast.makeText(mActivity, "Added: " + msg.obj, Toast.LENGTH_LONG).show();
                }
            }
        }
    };

    /**
     * Constructor
     *
     * @param activity
     */
    public DialogFragmentManager(FragmentActivity activity) {
        mActivity = activity;
    }

    /**
     * Displays a dialog proposing to add a nzb, show, movie, ...
     */
    public void showAddDialog() {
        AddDialog addDialog = new AddDialog();
        AddDialog.setAddDialogListener(this);
        addDialog.show(mActivity.getSupportFragmentManager(), "add");
    }

    /**
     * Displays a dialog to add a nzb to sabnzbd
     */
    @Override
    public void showAddNzbDialog() {
        AddNzbDialog addNzbDialog = new AddNzbDialog();
        AddNzbDialog.setMessageHandler(messageHandler);
        addNzbDialog.show(mActivity.getSupportFragmentManager(), "addnzb");
    }

    /**
     * Displays a dialog to add an opened nzb to sabnzbd
     */
    @Override
    public void showAddNzbFileDialog(String path) {
        AddNzbFileDialog addNzbFileDialog = new AddNzbFileDialog();
        AddNzbFileDialog.setMessageHandler(messageHandler);
        AddNzbFileDialog.setPath(path);
        addNzbFileDialog.show(mActivity.getSupportFragmentManager(), "addnzbfile");
    }

    /**
     * Displays a pop-up dialog when the user wants to add a show to SickBeard
     */
    @Override
    public void showAddShowDialog() {
        AddShowDialog addShowDialog = new AddShowDialog();
        AddShowDialog.setMessageHandler(messageHandler);
        addShowDialog.show(mActivity.getSupportFragmentManager(), "addshow");
    }

    /**
     * Displays the propositions dialog with the resulting show names found
     * after a user search to add a show to Sickbeard.
     *
     * @param showSearch The result of the search query
     */
    private void showAddShowSelectionDialog(final ShowSearch showSearch) {
        AddShowSelectDialog addShowSelectDialog = new AddShowSelectDialog();
        AddShowSelectDialog.setMessageHandler(messageHandler);
        AddShowSelectDialog.setShowSearch(showSearch);
        addShowSelectDialog.show(mActivity.getSupportFragmentManager(), "selectshow");
    }

    @Override
    public void showAddMovieDialog() {
        AddMovieDialog addMovieDialog = new AddMovieDialog();
        AddMovieDialog.setMessageHandler(messageHandler);
        addMovieDialog.show(mActivity.getSupportFragmentManager(), "addshow");
    }

    /**
     * Displays the propositions dialog with the resulting movie titles found
     * after a user search to add a movie to CouchPotato.
     *
     * @param movieSearch The result of the search query that will be used to generate the proposition list in the {@link AddMovieSelectDialog}
     */
    private void showAddMovieSelectionDialog(final MovieSearch movieSearch) {
        AddMovieSelectDialog addMovieSelectDialog = new AddMovieSelectDialog();
        AddMovieSelectDialog.setMessageHandler(messageHandler);
        AddMovieSelectDialog.setMovieList(movieSearch);
        addMovieSelectDialog.show(mActivity.getSupportFragmentManager(), "selectmovie");
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
}
