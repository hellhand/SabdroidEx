package com.sabdroidex.fragments.dialogs;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.sabdroidex.R;
import com.sabdroidex.controllers.couchpotato.CouchPotatoController;
import com.sabdroidex.data.couchpotato.Movie;

public class MovieDialogs {
    
    public static MovieMoreDialog getMoreDialog(Movie movie, Handler messageHandler) {
        return new MovieMoreDialog(movie, messageHandler);
    }
    
    public static MovieReleaseDialog getReleaseDialog(Movie movie, Handler messageHandler) {
        return new MovieReleaseDialog(movie, messageHandler);
    }
    
    public static MovieProfileDialog getProfileDialog(Movie movie, Handler messageHandler) {
        return new MovieProfileDialog(movie, messageHandler);
    }
    
    public static class MovieMoreDialog extends DialogFragment {
        
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
    
    public static class MovieReleaseDialog extends DialogFragment {
        
        private Movie movie;
        private Handler messageHandler;
        
        public MovieReleaseDialog(Movie movie, Handler messageHandler) {
            this.movie = movie;
            this.messageHandler = messageHandler;
        }
        
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Dialog dialog = new Dialog(getActivity());
            
            dialog.setContentView(R.layout.list_movie_release);
            dialog.setTitle(R.string.movie_release_titel);
            
            TableLayout table = (TableLayout) dialog.findViewById(R.id.movie_release_table);
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            
            View.OnClickListener onClickListener = new View.OnClickListener() {
                
                @Override
                public void onClick(View v) {
                    int releaseId;
                    if (v.getParent().getClass() == TableRow.class) {
                        TableRow curRow = (TableRow) v.getParent();
                        releaseId = curRow.getId();
                    }
                    else {
                        return;
                    }
                    if (R.id.movie_release_download == v.getId()) {
                        CouchPotatoController.downloadRelease(messageHandler, releaseId);
                    }
                    else if (R.id.movie_release_ignore == v.getId()) {
                        CouchPotatoController.ignoreRelease(messageHandler, releaseId);
                    }
                }
            };
            
            Object[] releases = new Object[0];
            for (int d = 0; d < releases.length; d++) {
                Object[] curRelease = (Object[]) releases[d];
                TableRow row = (TableRow) inflater.inflate(R.layout.list_movie_release_row, null);
                if (((String) curRelease[2]).equals("Snatched")) {
                    ((TextView) row.findViewById(R.id.movie_release_ind))
                            .setBackgroundColor(R.color.movie_release_current_release);
                }
                else if (((String) curRelease[2]).equals("Ignored")) {
                    ((TextView) row.findViewById(R.id.movie_release_ind))
                            .setBackgroundColor(R.color.movie_release_ignored_release);
                }
                else {
                    ((TextView) row.findViewById(R.id.movie_release_ind))
                            .setBackgroundColor(R.color.movie_release_default);
                }
                ((TextView) row.findViewById(R.id.movie_release_name)).setText((String) curRelease[1]);
                ((TextView) row.findViewById(R.id.movie_release_size)).setText((String) curRelease[4]);
                ((ImageButton) row.findViewById(R.id.movie_release_download)).setOnClickListener(onClickListener);
                ((ImageButton) row.findViewById(R.id.movie_release_ignore)).setOnClickListener(onClickListener);
                row.setId((Integer) curRelease[0]);
                
                if ((getActivity().getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE
                        && getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    ((TextView) row.findViewById(R.id.movie_release_status)).setText((String) curRelease[2]);
                    ((TextView) row.findViewById(R.id.movie_release_quality)).setText((String) curRelease[3]);
                    ((TextView) row.findViewById(R.id.movie_release_provider)).setText((String) curRelease[5]);
                }
                
                table.addView(row);
            }
            return dialog;
        }
    }
    
    public static class MovieProfileDialog extends DialogFragment {
        
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
}
