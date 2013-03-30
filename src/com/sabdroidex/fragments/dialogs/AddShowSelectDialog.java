package com.sabdroidex.fragments.dialogs;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.widget.ArrayAdapter;

import com.sabdroidex.R;
import com.sabdroidex.controllers.sickbeard.SickBeardController;
import com.sabdroidex.data.ShowSearch;
import com.sabdroidex.data.ShowSearchResult;


public class AddShowSelectDialog extends DialogFragment {

    private Handler messageHandler;
    private ShowSearch showSearch;

    public AddShowSelectDialog(Handler messageHandler, ShowSearch showSearch) {
        this.messageHandler = messageHandler;
        this.showSearch = showSearch;
    }
    
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
        
        ArrayList<String> shows = new ArrayList<String>();
        for (ShowSearchResult show : showSearch.getResults()) {
            shows.add(show.getName());
        }
        
        if (shows.size() > 0) {
            builder.setTitle(R.string.add_show_selection_dialog_title);
        }
        else {
            builder.setTitle(R.string.add_show_not_found);
        }
        
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_list_item_1, shows);
        builder.setAdapter(adapter, new OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ShowSearchResult selected = showSearch.getResults().get(which);
                SickBeardController.addShow(messageHandler, selected.getTvdbid().toString());
                dialog.dismiss();
            }
        });
        
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });
        
        return builder.create();
    }
}
