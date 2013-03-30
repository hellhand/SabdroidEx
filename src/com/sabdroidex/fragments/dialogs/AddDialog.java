package com.sabdroidex.fragments.dialogs;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.sabdroidex.R;
import com.sabdroidex.utils.Preferences;


public class AddDialog extends DialogFragment {
    
    private DialogActionsListener addDialogListener;

    public AddDialog(DialogActionsListener addDialogListener) {
        this.addDialogListener = addDialogListener;
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
        builder.setNegativeButton(android.R.string.cancel, onClickListener);
        
        List<CharSequence> options = new ArrayList<CharSequence>();
        options.add(getResources().getString(R.string.add_nzb_dialog_title));
        if (Preferences.isEnabled(Preferences.SICKBEARD)) {
            options.add(getResources().getString(R.string.add_show_dialog_title));
        }
        if (Preferences.isEnabled(Preferences.COUCHPOTATO)) {
            options.add(getResources().getString(R.string.add_movie_dialog_title));
        }
        
        builder.setItems(options.toArray(new CharSequence[options.size()]), new OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    addDialogListener.showAddNzbDialog();
                }
                else if (which == 1 && Preferences.isEnabled(Preferences.SICKBEARD)) {
                    addDialogListener.showAddShowDialog();
                }
                else if (which >= 1 && Preferences.isEnabled(Preferences.COUCHPOTATO)) {
                    addDialogListener.showAddMovieDialog();
                }
            }
        });
        
        return builder.create();
    }
}
