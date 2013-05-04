package com.sabdroidex.fragments.dialogs.sickbeard;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;

import com.sabdroidex.R;
import com.sabdroidex.controllers.sickbeard.SickBeardController;
import com.sabdroidex.data.sickbeard.Episode;

public class ShowEpisodeDialog extends DialogFragment {
    
    /**
     * Enumeration defining the different statuses an episode can be given by
     * SickBeard
     * 
     * @author Marc
     * 
     */
    private static enum STATUS {
        WANTED(R.string.episode_status_wanted), SKIPPED(R.string.episode_status_skipped), ARCHIEVED(
                R.string.episode_status_archieved), IGNORED(R.string.episode_status_ignored);
        
        private int string;
        
        STATUS(int string) {
            this.string = string;
        }
        
        public int getString() {
            return string;
        }
    }
    
    private Episode episode;
    private Handler messageHandler;
    
    public ShowEpisodeDialog(Handler messageHandler, Episode episode) {
        this.episode = episode;
        this.messageHandler = messageHandler;
    }
    
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        
        OnClickListener onClickListener = new DialogInterface.OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.episode_status_set);
        builder.setNegativeButton(android.R.string.cancel, onClickListener);
        
        List<CharSequence> options = new ArrayList<CharSequence>();
        for (STATUS status : STATUS.values()) {
            options.add(getResources().getString(status.getString()));
        }
        
        builder.setItems(options.toArray(new CharSequence[options.size()]), new OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SickBeardController.setEpisodeStatus(messageHandler, episode.getShowId().toString(), episode
                        .getSeasonNr().toString(), episode.getEpisode().toString(), STATUS.values()[which].toString()
                        .toLowerCase());
            }
        });
        
        return builder.create();
    }
}
