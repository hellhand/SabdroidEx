package com.sabdroidex.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.Preference;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.actionbarcompat.ActionBarPreferencesActivity;
import com.sabdroidex.R;
import com.sabdroidex.controllers.couchpotato.CouchPotatoController;
import com.sabdroidex.data.couchpotato.Movie;
import com.sabdroidex.fragments.dialogs.couchpotato.MovieFilesDialog;
import com.sabdroidex.utils.Preferences;

/**
 * Created by Marc on 27/05/13.
 */
public class MovieActivity extends ActionBarPreferencesActivity {

    public static final String MOVIE = "movie";
    private static final String TAG = MovieActivity.class.getCanonicalName();

    /**
     * Instantiating the Handler associated with this {@link android.app.Fragment}. It will
     * be notified when the request to retrieve the show data is successful
     */
    private final Handler messageHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == CouchPotatoController.MESSAGE.SEARCHER_TRY_NEXT.hashCode()) {
                try {
                    if (msg.obj instanceof String && !"".equals(msg.obj)) {
                        Toast.makeText(getApplicationContext(), (String) msg.obj, Toast.LENGTH_LONG).show();
                    }
                }
                catch (Exception e) {
                    Log.w(TAG, e.getLocalizedMessage());
                }
            }
            if (msg.what == CouchPotatoController.MESSAGE.MOVIE_DELETE.hashCode()) {
                try {
                    if (msg.obj instanceof String && !"".equals(msg.obj)) {
                        Toast.makeText(getApplicationContext(), (String) msg.obj, Toast.LENGTH_LONG).show();
                    }
                }
                catch (Exception e) {
                    Log.w(TAG, e.getLocalizedMessage());
                }
            }
            if (msg.what == CouchPotatoController.MESSAGE.RELEASE_DOWNLOAD.hashCode()) {
                try {
                    if (msg.obj instanceof String && !"".equals(msg.obj)) {
                        Toast.makeText(getApplicationContext(), (String) msg.obj, Toast.LENGTH_LONG).show();
                    }
                }
                catch (Exception e) {
                    Log.w(TAG, e.getLocalizedMessage());
                }
            }
        }
    };

    private Movie movie;
    private PreferenceElementClickListener preferenceElementClickListener = new PreferenceElementClickListener();

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        movie = (Movie) getIntent().getExtras().get(MOVIE);
        setTitle(movie.getTitle());
        addPreferencesFromResource(R.xml.movie_preferences);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        findPreference(Preferences.MOVIE_AVAILABLE_FILES).setOnPreferenceClickListener(preferenceElementClickListener);
        findPreference(Preferences.MOVIE_READD).setOnPreferenceClickListener(preferenceElementClickListener);
        findPreference(Preferences.MOVIE_REMOVE).setOnPreferenceClickListener(preferenceElementClickListener);
        findPreference(Preferences.MOVIE_PICK_RELEASE).setOnPreferenceClickListener(preferenceElementClickListener);
        /*
        findPreference(Preferences.MOVIE_CHANGE_MOVIE_INFO).setOnPreferenceClickListener(preferenceElementClickListener);
        findPreference(Preferences.MOVIE_DOWLOAD_BEST).setOnPreferenceClickListener(preferenceElementClickListener);
        findPreference(Preferences.MOVIE_MARK_DONE).setOnPreferenceClickListener(preferenceElementClickListener);
        */
        /*
         * If the movie does not have the done status the release selection is available
         */
        findPreference(Preferences.WANTED).setEnabled(movie.getStatusID() != 3);
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

    private class PreferenceElementClickListener implements Preference.OnPreferenceClickListener {

        @Override
        public boolean onPreferenceClick(Preference preference) {
            if (Preferences.MOVIE_AVAILABLE_FILES.equals(preference.getKey())) {
                MovieFilesDialog movieFilesDialog = new MovieFilesDialog();
                MovieFilesDialog.setMovie(movie);
                movieFilesDialog.show(MovieActivity.this);
            }
            if (Preferences.MOVIE_READD.equals(preference.getKey())) {
                CouchPotatoController.snatchNextMovieRelease(messageHandler, movie.getMovieID());
            }

            if (Preferences.MOVIE_REMOVE.equals(preference.getKey())) {
                CouchPotatoController.deleteMovie(messageHandler, movie.getMovieID());
            }
            if (Preferences.MOVIE_PICK_RELEASE.equals(preference.getKey())) {
                Intent intent = new Intent(getBaseContext(), ReleaseActivity.class);
                intent.putExtra("movie", movie);
                startActivity(intent);
            }
            /*
            if (Preferences.MOVIE_CHANGE_MOVIE_INFO.equals(preference.getKey())) {

            }
            if (Preferences.MOVIE_DOWLOAD_BEST.equals(preference.getKey())) {

            }
            if (Preferences.MOVIE_MARK_DONE.equals(preference.getKey())) {

            }
            */
            return true;
        }
    }
}
