package com.sabdroidex.activity;

import android.os.Bundle;
import android.widget.GridView;

import com.android.actionbarcompat.ActionBarActivity;
import com.sabdroidex.R;
import com.sabdroidex.adapters.ReleaseAdapter;
import com.sabdroidex.data.couchpotato.Movie;

/**
 * Created by Marc on 2/06/13.
 */
public class ReleaseActivity extends ActionBarActivity {

    ReleaseAdapter releaseAdapter;
    Movie movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.small_grid);

        movie = (Movie) getIntent().getExtras().get("movie");
        GridView gridView = (GridView) findViewById(R.id.elementGrid);

        releaseAdapter = new ReleaseAdapter(getApplicationContext(), movie.getReleases());
        gridView.setAdapter(releaseAdapter);
    }
}
