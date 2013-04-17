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

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sabdroidex.R;
import com.sabdroidex.adapters.MovieGridAdapter;
import com.sabdroidex.controllers.couchpotato.CouchPotatoController;
import com.sabdroidex.data.couchpotato.Movie;
import com.sabdroidex.data.couchpotato.MovieList;
import com.sabdroidex.fragments.dialogs.MovieDetailsDialog;
import com.sabdroidex.utils.ImageUtils;
import com.sabdroidex.utils.ImageWorker.ImageType;
import com.sabdroidex.utils.Preferences;
import com.sabdroidex.utils.SABDroidConstants;

public class MoviesFragment extends SABFragment {
    
    private static final String TAG = "MovieFragment";
    private MovieList movieList;
    private GridView mMovieList;
    private MovieGridAdapter mMovieListRowAdapter;
    
    /**
     * Instantiating the Handler associated with this {@link Fragment}.
     */
    private final Handler messageHandler = new Handler() {
        
        @Override
        public void handleMessage(Message msg) {
            // TODO: Create Pojos for this to prevent unchecked and display
            // Toast if nothing there is no need to go further.
            if (msg.what == CouchPotatoController.MESSAGE.MOVIE_SEARCH.ordinal()) {
                try {
                    Object[] result = (Object[]) msg.obj;
                    selectMoviePrompt((ArrayList<Object[]>) result[0]);
                }
                catch (Exception e) {
                    Log.w(TAG, e.getLocalizedMessage());
                }
                
            }
            else if (msg.what == CouchPotatoController.MESSAGE.MOVIE_ADD.ordinal()) {
                // TODO: USE the resource bundle no hard coded strings !!!!!!
                if ("Error".equals(msg.obj)) {
                    Toast.makeText(getActivity(), "Failed to add movie\nCheck settings!", Toast.LENGTH_LONG).show();
                }
                else if (!"".equals(msg.obj)) {
                    Toast.makeText(getActivity(), "Added: " + msg.obj, Toast.LENGTH_LONG).show();
                }
            }
            else if (msg.what == CouchPotatoController.MESSAGE.MOVIE_LIST.ordinal()) {
                
                movieList = (MovieList) msg.obj;
                
                if (mMovieListRowAdapter != null) {
                    mMovieListRowAdapter.setDataSet(movieList.getMovieElements());
                    mMovieListRowAdapter.notifyDataSetChanged();
                }
            }
            else if (msg.what == CouchPotatoController.MESSAGE.UPDATE.ordinal()) {
                try {
                    if (msg.obj instanceof String && !"".equals(msg.obj)) {
                        Toast.makeText(getActivity(), (String) msg.obj, Toast.LENGTH_LONG).show();
                    }
                }
                catch (Exception e) {
                    Log.w(TAG, e.getLocalizedMessage());
                }
            }
        }
    };
    
    /**
     * 
     */
    public MoviesFragment() {
        movieList = new MovieList();
    }
    
    /**
     * 
     * @param downloadRows
     */
    public MoviesFragment(MovieList movieList) {
        this.movieList = movieList;
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
        CouchPotatoController.refreshMovies(messageHandler);
    }
    
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        SharedPreferences preferences = getActivity().getSharedPreferences(SABDroidConstants.PREFERENCES_KEY, 0);
        Preferences.update(preferences);
        
        mMovieList = (GridView) inflater.inflate(R.layout.large_grid, null);
        
        mMovieListRowAdapter = new MovieGridAdapter(getActivity(), movieList.getMovieElements());
        mMovieList.setAdapter(mMovieListRowAdapter);
        mMovieList.setOnItemLongClickListener(new ListItemLongClickListener());
        
        manualRefreshMovies();
        
        return mMovieList;
    }
    
    public void setupShowElements(View view, Movie movie) {
        
        ImageView moviePoster = (ImageView) view.findViewById(R.id.moviePoster);
        
        TextView movieTitle = (TextView) view.findViewById(R.id.movie_title);
        movieTitle.setText(movie.getTitle());
        
        TextView movieProfile = (TextView) view.findViewById(R.id.movie_profile);
        movieProfile.setText(CouchPotatoController.getProfile(movie.getProfileID()));
        
        TextView moviePlot = (TextView) view.findViewById(R.id.movie_plot);
        moviePlot.setText(movie.getPlot());
        
        TextView movieRuntime = (TextView) view.findViewById(R.id.movie_runtime);
        movieRuntime.setText(Integer.toString(movie.getLibrary().getInfo().getRuntime()) + " min");
        
        TextView movieStatus = (TextView) view.findViewById(R.id.movie_status);
        movieStatus.setText(CouchPotatoController.getStatus(movie.getStatusID()));
        
        TextView movieReleased = (TextView) view.findViewById(R.id.movie_released);
        movieReleased.setText(movie.getLibrary().getInfo().getReleased());
        
        TextView movieGenre = (TextView) view.findViewById(R.id.movie_genre);
        movieGenre.setText(movie.getGenres());
        
        TextView movieRating = (TextView) view.findViewById(R.id.movie_rating);
        movieRating.setText(movie.getLibrary().getInfo().getRating().getImdbRating() + ", Votes: "
                + movie.getLibrary().getInfo().getRating().getImdbVotes() + ")");
        
        String imageKey = ImageType.MOVIE_POSTER.name() + movie.getMovieID();
        ImageUtils.getImageWorker().loadImage(moviePoster, ImageType.MOVIE_POSTER, imageKey, movie.getMovieID(),
                movie.getTitle(), movie.getLibrary().getInfo().getPosters().getSimplePoster());
    }
    
    /*
     * Getter for this {@link Fragment}'s message {@link Handler}
     * 
     * @return the message {@link Handler} for this {@link Activity}
     */
    public Handler getMessageHandler() {
        return messageHandler;
    }
    
    //TODO: put this somewhere else !!
    @SuppressWarnings("deprecation")
    public void addMoviePrompt() {
        
        if (!Preferences.isSet(Preferences.COUCHPOTATO_URL)) {
            getActivity().showDialog(R.id.dialog_setup_prompt);
            return;
        }
        
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        
        alert.setTitle(R.string.add_movie_dialog_title);
        alert.setMessage(R.string.add_movie_dialog_message);
        
        final EditText input = new EditText(getActivity());
        alert.setView(input);
        
        alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString();
                Toast.makeText(getActivity(), getActivity().getText(R.string.add_show_background_search),
                        Toast.LENGTH_LONG).show();
                CouchPotatoController.searchMovie(getMessageHandler(), value);
            }
        });
        
        alert.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });
        
        alert.show();
        
    }
    
    /**
     * Displays the propositions dialog with the resulting movie titles found
     * after a user search to add a movie to CouchPotato.
     * 
     * @param result
     *            The result of the search query
     */
    private void selectMoviePrompt(final ArrayList<Object[]> result) {
        
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        
        ArrayList<String> movies = new ArrayList<String>();
        for (Object[] show : result) {
            movies.add(show[0] + "");
        }
        // TODO: remove this (check before)
        if (movies.size() > 0) {
            alert.setTitle(R.string.add_movie_selection_dialog_title);
        }
        else {
            alert.setTitle(R.string.add_movie_not_found);
        }
        
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,
                movies);
        alert.setAdapter(adapter, new OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Object[] selected = result.get(which);
                CouchPotatoController.addMovie(messageHandler, Preferences.get(Preferences.COUCHPOTATO_PROFILE),
                        ((String) selected[1]), ((String) selected[0]));
                dialog.dismiss();
            }
        });
        
        alert.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });
        
        alert.show();
    }
    
    @Override
    public Object getDataCache() {
        return movieList;
    }
    
    private class ListItemLongClickListener implements OnItemLongClickListener {
        
        /**
         * When an item is selected by a long click a Dialog appears to display
         * the show details.
         */
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
            Movie movie = movieList.getMovieElements().get(position);
            MovieDetailsDialog movieDetailsDialog = new MovieDetailsDialog(movie);
            movieDetailsDialog.show(getActivity().getSupportFragmentManager(), movie.getTitle());
            return true;
        }
    }
}
