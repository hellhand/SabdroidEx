package com.sabdroidex.fragments.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.widget.EditText;
import android.widget.Toast;

import com.sabdroidex.R;
import com.sabdroidex.controllers.sickbeard.SickBeardController;


public class AddShowDialog extends DialogFragment {
    
    Handler messageHandler;
    
    public AddShowDialog(Handler messageHandler) {
        this.messageHandler = messageHandler;
    }
    
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        
        builder.setTitle(R.string.add_show_dialog_title);
        builder.setMessage(R.string.add_show_dialog_message);
        
        final EditText input = new EditText(getActivity());
        builder.setView(input);
        
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString();
                Toast.makeText(getActivity(), getActivity().getApplicationContext().getText(R.string.add_show_background_search), Toast.LENGTH_LONG).show();
                SickBeardController.searchShow(messageHandler, value);
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
