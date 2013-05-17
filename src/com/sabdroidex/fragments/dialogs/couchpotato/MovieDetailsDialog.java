package com.sabdroidex.fragments.dialogs.couchpotato;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.sabdroidex.R;
import com.sabdroidex.controllers.couchpotato.CouchPotatoController;
import com.sabdroidex.data.couchpotato.Movie;
import com.sabdroidex.utils.ImageUtils;
import com.sabdroidex.utils.ImageWorker.ImageType;

public class MovieDetailsDialog extends DialogFragment {
    
    private Movie mMovie;
    
    public MovieDetailsDialog() {
        this.mMovie = new Movie();
    }
    
    public MovieDetailsDialog(Movie movie) {
        this.mMovie = movie;
    }
    
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        
        LayoutInflater inflater = LayoutInflater.from(getActivity().getBaseContext());
        ScrollView mMovieView = (ScrollView) inflater.inflate(R.layout.movie_status, null);
        
        setupMovieElements(mMovieView, mMovie);

        builder.setPositiveButton(R.string.more, new DialogInterface.OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                //TODO: well do something
            }
        });
        
        builder.setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });
        
        builder.setView(mMovieView);
        
        return builder.create();
    }
    
    @Override
    public void onDismiss(DialogInterface dialog) {
        mMovie = null;
        super.onDismiss(dialog);
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
        
        String imageKey = ImageType.MOVIE_POSTER.name() + movie.getMovieID();
        ImageUtils.getImageWorker().loadImage(moviePoster, ImageType.MOVIE_POSTER, imageKey, movie.getMovieID(),
                movie.getTitle(), movie.getLibrary().getInfo().getPosters().getOriginalPoster());
    }
}
