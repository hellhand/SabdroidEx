package com.sabdroidex.fragments.dialogs.couchpotato;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.ArrayAdapter;
import com.sabdroidex.R;
import com.sabdroidex.controllers.couchpotato.CouchPotatoController;
import com.sabdroidex.data.couchpotato.MovieSearch;
import com.sabdroidex.data.couchpotato.MovieSearchResult;
import com.sabdroidex.utils.Preferences;
import com.sabdroidex.utils.SABHandler;

import java.util.ArrayList;

public class AddMovieSelectDialog extends DialogFragment {

    private static SABHandler messageHandler;
    private static MovieSearch movieList;

    public static void setMessageHandler(SABHandler messageHandler) {
        AddMovieSelectDialog.messageHandler = messageHandler;
    }

    public static void setMovieList(MovieSearch movieSearch) {
        AddMovieSelectDialog.movieList = movieSearch;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

        ArrayList<String> movies = new ArrayList<String>();
        for (MovieSearchResult movie : movieList.getMovieSearchResults()) {
            movies.add(movie.getOriginalTitle());
        }

        int title = movies.size() > 0 ? R.string.add_movie_selection_dialog_title : R.string.add_movie_not_found;
        alert.setTitle(title);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, movies);
        alert.setAdapter(adapter, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                MovieSearchResult selected = movieList.getMovieSearchResults().get(which);
                CouchPotatoController.addMovie(messageHandler, Preferences.get(Preferences.COUCHPOTATO_PROFILE), selected.getImdb(),
                        selected.getOriginalTitle());
                dialog.dismiss();
            }
        });

        alert.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });

        return alert.create();
    }
}
