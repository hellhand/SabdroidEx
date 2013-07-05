package com.sabdroidex.test;

import java.util.ArrayList;

import com.sabdroidex.data.couchpotato.Movie;
import com.sabdroidex.data.couchpotato.MovieList;

import android.test.AndroidTestCase;

public class VersionTests extends AndroidTestCase {

    /**
     * This test verifies that there is indeed a problem when using isEmpty on
     * an <apiL9. Thus causing problems this forces the use of equals(""); This
     * is indeed a problem and should be reset as isEmpty when upgrading minimal
     * version as the call is done natively.
     */
    public void testSetting() {
        MovieList movieList = new MovieList();
        ArrayList<Movie> movies = new ArrayList<Movie>();

        movieList.setMovieElements(movies);
    }

}
