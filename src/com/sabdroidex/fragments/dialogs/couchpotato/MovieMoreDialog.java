package com.sabdroidex.fragments.dialogs.couchpotato;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;

import com.sabdroidex.R;
import com.sabdroidex.data.couchpotato.Movie;

public class MovieMoreDialog extends DialogFragment {

    private Movie movie;
    private Handler messageHandler;

    public MovieMoreDialog(Movie movie, Handler messageHandler) {
        this.movie = movie;
        this.messageHandler = messageHandler;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setNegativeButton(android.R.string.cancel, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });

        builder.setTitle(movie.getTitle());
        String[] options = new String[3];
        options[0] = getActivity().getResources().getString(R.string.menu_movie_delete);
        options[1] = getActivity().getResources().getString(R.string.menu_movie_edit_profile);
        options[2] = getActivity().getResources().getString(R.string.menu_movie_release);

        builder.setItems(options, new OnClickListener() {

            // TODO: finnish
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        // CouchPotatoController.deleteMovie(messageHandler,
                        // movie);
                        break;
                    case 1:
                        // Dialog profileDialog =
                        // createSelectProfileDialog(movie);
                        // profileDialog.show();
                        break;
                    default:
                        break;
                }
            }
        });
        return builder.create();
    }
}
