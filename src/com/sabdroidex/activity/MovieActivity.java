package com.sabdroidex.activity;

import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.actionbarcompat.ActionBarActivity;
import com.android.actionbarcompat.ActionBarPreferencesActivity;
import com.sabdroidex.R;
import com.sabdroidex.data.couchpotato.Movie;
import com.sabdroidex.fragments.dialogs.couchpotato.MovieFilesDialog;
import com.sabdroidex.utils.Preferences;

/**
 * Created by Marc on 27/05/13.
 */
public class MovieActivity extends ActionBarActivity {

    public static final String MOVIE = "movie";
    private Movie movie;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        movie = (Movie) getIntent().getExtras().get(MOVIE);

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
                //movieFilesDialog.show(getFragmentManager(), "");
            }
            if (Preferences.MOVIE_READD.equals(preference.getKey())) {
                
            }
            if (Preferences.MOVIE_CHANGE_MOVIE_INFO.equals(preference.getKey())) {
                
            }
            if (Preferences.MOVIE_REMOVE.equals(preference.getKey())) {
                
            }
            if (Preferences.MOVIE_DOWLOAD_BEST.equals(preference.getKey())) {
                
            }
            if (Preferences.MOVIE_PICK_RELEASE.equals(preference.getKey())) {
                
            }
            if (Preferences.MOVIE_MARK_DONE.equals(preference.getKey())) {
                
            }
            return true;
        }
    }
}
