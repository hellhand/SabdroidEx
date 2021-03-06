/*
 * Copyright (C) 2011-2013  Roy Kokkelkoren
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.*
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.sabdroidex.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sabdroidex.R;
import com.sabdroidex.activity.MovieActivity;
import com.sabdroidex.adapters.MovieGridAdapter;
import com.sabdroidex.controllers.SABController;
import com.sabdroidex.controllers.couchpotato.CouchPotatoController;
import com.sabdroidex.data.JSONBased;
import com.sabdroidex.data.couchpotato.Movie;
import com.sabdroidex.data.couchpotato.MovieList;
import com.sabdroidex.fragments.dialogs.couchpotato.MovieDetailsDialog;
import com.sabdroidex.utils.ImageUtils;
import com.sabdroidex.utils.ImageWorker.ImageType;
import com.sabdroidex.utils.Preferences;
import com.sabdroidex.utils.SABHandler;

public class MoviesFragment extends SABFragment {

    private static final String TAG = MoviesFragment.class.getCanonicalName();
    private static MovieList movieList;
    /**
     * Instantiating the Handler associated with this {@link Fragment}.
     */
    private final SABHandler messageHandler = new SABHandler() {

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == CouchPotatoController.MESSAGE.MOVIE_LIST.hashCode()) {
                try {
                    movieList = (MovieList) msg.obj;
                    updateMovieList();
                }
                catch (Exception e) {
                    Log.e(TAG, e.getLocalizedMessage() == null ? e.toString() : e.getLocalizedMessage());
                }
            }
            else if (msg.what == SABController.MESSAGE.UPDATE.hashCode()) {
                try {
                    if (msg.obj instanceof String && !"".equals(msg.obj)) {
                        Toast.makeText(getActivity(), (String) msg.obj, Toast.LENGTH_LONG).show();
                    }
                }
                catch (Exception e) {
                    Log.e(TAG, e.getLocalizedMessage() == null ? e.toString() : e.getLocalizedMessage());
                }
            }
        }
    };

    private GridView movieGrid;
    private MovieGridAdapter mMovieGridAdapter;

    /**
     *
     */
    public MoviesFragment() {
    }

    /**
     * Constructor with default {@link MovieList} to be displayed.
     *
     * @param movieRows
     */
    public MoviesFragment(MovieList movieRows) {
        movieList = movieRows;
    }

    @Override
    public int getTitle() {
        return R.string.tab_movies;
    }

    /**
     * Refreshing the queue during startup or on user request. Asks to configure
     * if still not done
     */
    public void manualRefreshMovies() {
        if (!Preferences.isEnabled(Preferences.COUCHPOTATO)) {
            return;
        }
        CouchPotatoController.refreshMovies(messageHandler, "");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        messageHandler.setActivity(getActivity());
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mMovieGridAdapter = new MovieGridAdapter(getActivity(), movieList.getMovieElements());

        LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.list_movies, null);
        movieGrid = (GridView) linearLayout.findViewById(R.id.elementGrid);
        movieGrid.setAdapter(mMovieGridAdapter);

        // Based on the existence of that view we choose the listener we will apply
        LinearLayout layout = (LinearLayout) linearLayout.findViewById(R.id.movieStatus);
        if (layout != null) {
            movieGrid.setOnItemClickListener(new GridItemClickListener());
            movieGrid.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
            mMovieGridAdapter.showOverlay(true);
        }
        else {
            movieGrid.setOnItemClickListener(new GridItemDialogClickListener());
        }

        manualRefreshMovies();
        return linearLayout;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        if (movieList.getMovieElements().size() > 0 && getView() != null && getView().findViewById(R.id.movieStatus) != null) {
            movieGrid.setSoundEffectsEnabled(false);
            movieGrid.performItemClick(movieGrid, 0, movieGrid.getItemIdAtPosition(0));
            movieGrid.setSoundEffectsEnabled(true);
        }
        super.onViewCreated(view, savedInstanceState);
    }

    public void setupMovieElements(View view, Movie movie) {
        ImageView moviePoster = (ImageView) view.findViewById(R.id.moviePoster);

        TextView movieTitle = (TextView) view.findViewById(R.id.movie_name);
        movieTitle.setText(movie.getTitle());

        TextView movieProfile = (TextView) view.findViewById(R.id.movie_profile);
        movieProfile.setText(CouchPotatoController.getProfile(movie.getProfileID()));

        TextView moviePlot = (TextView) view.findViewById(R.id.movie_plot);
        moviePlot.setText(movie.getPlot());

        TextView movieRuntime = (TextView) view.findViewById(R.id.movie_runtime);
        movieRuntime.setText(Integer.toString(movie.getLibrary().getInfo().getRuntime()));

        TextView movieStatus = (TextView) view.findViewById(R.id.movie_status);
        movieStatus.setText(CouchPotatoController.getStatus(movie.getStatusID()));

        TextView movieReleased = (TextView) view.findViewById(R.id.movie_released);
        movieReleased.setText(movie.getLibrary().getInfo().getReleased());

        TextView movieGenre = (TextView) view.findViewById(R.id.movie_genre);
        movieGenre.setText(movie.getGenres());

        TextView movieRating = (TextView) view.findViewById(R.id.movie_rating);
        movieRating.setText(movie.getLibrary().getInfo().getRating().getImdbRating().toString());

        Button moreButton = (Button) view.findViewById(R.id.more_button);
        moreButton.setOnClickListener(new ShowMoreButtonClickListener(movie));

        String imageKey = ImageType.MOVIE_POSTER.name() + movie.getMovieID();
        ImageUtils.getImageWorker().loadImage(moviePoster, ImageType.MOVIE_POSTER, imageKey, movie.getMovieID(), movie.getTitle(), movie.getLibrary().getInfo().getPosters().getOriginalPoster());
    }

    @Override
    public JSONBased getDataCache() {
        return movieList;
    }

    /**
     * Updates the movie {@link android.widget.ListView} with the current data
     */
    private void updateMovieList() {
        if (mMovieGridAdapter != null && movieList != null) {
            mMovieGridAdapter.setDataSet(movieList.getMovieElements());
            mMovieGridAdapter.notifyDataSetChanged();
            if (movieList.getMovieElements().size() > 0 && getView() != null && getView().findViewById(R.id.movieStatus) != null) {
                movieGrid.setSoundEffectsEnabled(false);
                movieGrid.performItemClick(movieGrid, 0, movieGrid.getItemIdAtPosition(0));
                movieGrid.setSoundEffectsEnabled(true);
            }
        }
    }

    private class GridItemClickListener implements OnItemClickListener {

        /**
         * When an item is selected by a click the show details are displayed at
         * the side of the ListView
         */
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (getView() == null) {
                /**
                 * This can be caused when a click is called in the Handler
                 * whilst the parent view is null
                 */
                return;
            }
            setupMovieElements(getView(), movieList.getMovieElements().get(position));
            movieGrid.invalidateViews();
        }
    }

    private class GridItemDialogClickListener implements OnItemClickListener {

        /**
         * When an item is selected by a click on a normal device, a Dialog appears to display
         * the show details.
         */
        @Override
        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
            try {
                Movie movie = movieList.getMovieElements().get(position);
                MovieDetailsDialog movieDetailsDialog = new MovieDetailsDialog();
                MovieDetailsDialog.setMovie(movie);
                movieDetailsDialog.show(getActivity().getSupportFragmentManager(), movie.getTitle());
            }
            catch (Exception e) {
                Log.e(TAG, e.getLocalizedMessage());
            }
        }
    }

    private class ShowMoreButtonClickListener implements View.OnClickListener {

        private Movie movie;

        public ShowMoreButtonClickListener(Movie movie) {
            this.movie = movie;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity().getApplicationContext(), MovieActivity.class);
            intent.putExtra(MovieActivity.MOVIE, movie);
            startActivity(intent);
        }
    }
}
