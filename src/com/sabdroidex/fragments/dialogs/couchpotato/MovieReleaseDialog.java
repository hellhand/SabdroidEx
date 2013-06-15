package com.sabdroidex.fragments.dialogs.couchpotato;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;

import com.sabdroidex.R;
import com.sabdroidex.controllers.couchpotato.CouchPotatoController;
import com.sabdroidex.data.couchpotato.MovieRelease;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class MovieReleaseDialog extends DialogFragment {

    private static WeakReference<MovieRelease> movieRelease;
    private static WeakReference<Handler> messageHandler;

    public static void setMovieRelease(MovieRelease movieRelease) {
        MovieReleaseDialog.movieRelease = new WeakReference<MovieRelease>(movieRelease);
    }

    public static void setMessageHandler(Handler messageHandler) {
        MovieReleaseDialog.messageHandler = new WeakReference<Handler>(messageHandler);
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {

        List<CharSequence> options = new ArrayList<CharSequence>();
        options.add(getResources().getString(R.string.movie_release_download));
        options.add(getResources().getString(R.string.movie_release_ignore));

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.movie_release_title);

        builder.setItems(options.toArray(new CharSequence[2]), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        CouchPotatoController.downloadRelease(messageHandler.get(), movieRelease.get().getId());
                        break;
                    case 1:
                        CouchPotatoController.ignoreRelease(messageHandler.get(), movieRelease.get().getId());
                        break;
                }
            }
        });

        builder.setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        return builder.create();
    }
}