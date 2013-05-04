package com.sabdroidex.fragments.dialogs.couchpotato;

import java.util.ArrayList;
import java.util.HashMap;

import com.sabdroidex.R;
import com.sabdroidex.controllers.couchpotato.CouchPotatoController;
import com.sabdroidex.data.couchpotato.Movie;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;

public class MovieProfileDialog extends DialogFragment {
    
    private Movie movie;
    private Handler messageHandler;
    
    public MovieProfileDialog(Movie movie, Handler messageHandler) {
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
        builder.setTitle(getActivity().getResources().getString(R.string.menu_movie_edit_profile));
        final HashMap<Integer, String> profiles = CouchPotatoController.getAllProfiles();
        final ArrayList<String> options = new ArrayList<String>();
        for (Integer key : profiles.keySet()) {
            options.add(profiles.get(key));
        }
        builder.setItems(options.toArray(new String[profiles.size()]), new OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int newKey = -1;
                String newProfile = options.get(which);
                for (int key : profiles.keySet()) {
                    if (profiles.get(key).equals(newProfile)) {
                        newKey = key;
                        break;
                    }
                }
                CouchPotatoController.editMovie(messageHandler, newKey, movie.getMovieID());
            }
        });
        return builder.create();
    }
}