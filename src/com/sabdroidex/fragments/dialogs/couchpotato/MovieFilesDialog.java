package com.sabdroidex.fragments.dialogs.couchpotato;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.sabdroidex.R;
import com.sabdroidex.data.couchpotato.Movie;

public class MovieFilesDialog extends DialogFragment {

    private static Movie movie;

    public static void setMovie(Movie movie) {
        MovieFilesDialog.movie = movie;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getActivity().getResources().getString(R.string.menu_movie_edit_profile));

        final ArrayList<String> files = new ArrayList<String>();
        for (MovieFile file : movie.getFirstRelease().getMovieFiles()) {
            files.add(file.getPath());
        }
        builder.setItems(files.toArray(new String[files.size()]), null);
        builder.setNegativeButton(R.string.close, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });

        return builder.create();
    }

}
