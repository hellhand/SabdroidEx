package com.sabdroidex.activity;

import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.android.actionbarcompat.ActionBarActivity;
import com.sabdroidex.R;
import com.sabdroidex.adapters.ReleaseAdapter;
import com.sabdroidex.controllers.couchpotato.CouchPotatoController;
import com.sabdroidex.data.couchpotato.Movie;
import com.sabdroidex.data.couchpotato.MovieReleases;
import com.sabdroidex.fragments.dialogs.couchpotato.MovieReleaseDialog;
import com.sabdroidex.utils.SABHandler;

/**
 * Created by Marc on 2/06/13.
 */
public class ReleaseActivity extends ActionBarActivity {

    private static final String TAG = ReleaseActivity.class.getCanonicalName();
    private ReleaseAdapter releaseAdapter;
    private Movie movie;
    private MovieReleases movieReleases;

    // Instantiating the Handler associated with the main thread.
    private final SABHandler messageHandler = new SABHandler() {

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == CouchPotatoController.MESSAGE.RELEASE_FOR_MOVIE.hashCode()) {
                try {
                    if (msg.obj instanceof MovieReleases) {
                        ReleaseActivity.this.movieReleases = (MovieReleases) msg.obj;
                        ReleaseActivity.this.updateReleases();
                    }
                }
                catch (Exception e) {
                    Log.e(TAG, e.getLocalizedMessage() == null ? " " : e.getLocalizedMessage());
                }
            }
            if (msg.what == CouchPotatoController.MESSAGE.RELEASE_DOWNLOAD.hashCode()) {
                try {
                    if (msg.obj instanceof Boolean) {
                        String text = new StringBuilder().append(getResources().getString(R.string.movie_release_download_status)).append((Boolean) msg.obj ? getResources().getString(R.string.success) : getResources().getString(R.string.failed)).toString();
                        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
                    }
                }
                catch (Exception e) {
                    Log.e(TAG, e.getLocalizedMessage() == null ? " " : e.getLocalizedMessage());
                }
            }
            if (msg.what == CouchPotatoController.MESSAGE.UPDATE.hashCode()) {
                try {
                    if (msg.obj instanceof String && !"".equals(msg.obj)) {
                        Toast.makeText(getApplicationContext(), (String) msg.obj, Toast.LENGTH_LONG).show();
                    }
                }
                catch (Exception e) {
                    Log.e(TAG, e.getLocalizedMessage() == null ? " " : e.getLocalizedMessage());
                }
            }
        }
    };

    private void updateReleases() {
        if (releaseAdapter != null || movieReleases != null) {
            releaseAdapter.setItems(movieReleases.getReleases());
            releaseAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        setContentView(R.layout.list_releases);

        movie = (Movie) getIntent().getExtras().get("movie");
        setTitle(movie.getTitle());

        GridView gridView = (GridView) findViewById(R.id.elementGrid);

        movieReleases = new MovieReleases();
        releaseAdapter = new ReleaseAdapter(getApplicationContext(), movieReleases.getReleases());
        gridView.setAdapter(releaseAdapter);
        gridView.setOnItemClickListener(new MovieReleaseClickListener());

        refresh();
    }

    /**
     * This method calls the Controller to retrieve the releases
     */
    private void refresh() {
        CouchPotatoController.getReleasesForMovie(messageHandler, movie.getMovieID());
    }

    /**
     * Handles item selections in the Menu
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                refresh();
                break;
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private class MovieReleaseClickListener implements AdapterView.OnItemClickListener {

        /**
         * Callback method to be invoked when an item in this AdapterView has
         * been clicked.
         * <p/>
         * Implementers can call getItemAtPosition(position) if they need
         * to access the data associated with the selected item.
         *
         * @param parent   The AdapterView where the click happened.
         * @param view     The view within the AdapterView that was clicked (this
         *                 will be a view provided by the adapter)
         * @param position The position of the view in the adapter.
         * @param id       The row id of the item that was clicked.
         */
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            try {
                MovieReleaseDialog movieReleaseDialog = new MovieReleaseDialog();
                MovieReleaseDialog.setMovieRelease(movieReleases.getReleases().get(position));
                MovieReleaseDialog.setMessageHandler(messageHandler);
                movieReleaseDialog.show(getSupportFragmentManager(),"releaseSelection");
            }
            catch (Exception e) {
                Log.e(TAG, e.getLocalizedMessage());
            }
        }
    }
}
