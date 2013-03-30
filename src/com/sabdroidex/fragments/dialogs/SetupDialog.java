package com.sabdroidex.fragments.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.sabdroidex.R;
import com.sabdroidex.activity.SettingsActivity;


public class SetupDialog extends DialogFragment {
    
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        
        OnClickListener clickListener = new DialogInterface.OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                if (whichButton == Dialog.BUTTON_POSITIVE) {
                    showSettings();
                }
            }
        };
        
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.config);
        builder.setPositiveButton(android.R.string.ok, clickListener);
        builder.setNegativeButton(android.R.string.cancel, clickListener);
        
        return builder.create();
    }
    
    /**
     * Displaying the application settings
     */
    private void showSettings() {
        startActivity(new Intent(getActivity().getApplicationContext(), SettingsActivity.class));
    }
}
