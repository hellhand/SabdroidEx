package com.sabdroidex.fragments.dialogs.couchpotato;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

import com.sabdroidex.R;
import com.sabdroidex.data.couchpotato.Movie;
import com.sabdroidex.data.couchpotato.MovieFile;

import java.util.ArrayList;

public class MovieFilesDialog {

    private static Movie movie;

    public static void setMovie(Movie movie) {
        MovieFilesDialog.movie = movie;
    }

    public void show(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getResources().getString(R.string.movie_available_files));

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

        builder.create();
        builder.show();
    }

}
