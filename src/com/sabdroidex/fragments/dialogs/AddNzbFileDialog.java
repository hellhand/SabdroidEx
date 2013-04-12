package com.sabdroidex.fragments.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;

import com.sabdroidex.R;
import com.sabdroidex.controllers.sabnzbd.SABnzbdController;
import com.utils.FileUtil;

public class AddNzbFileDialog extends DialogFragment {
    
    private Handler messageHandler;
    private String path;
    
    public AddNzbFileDialog(Handler messageHandler, String path) {
        this.messageHandler = messageHandler;
        this.path = path;
    }
    
    /**
     * This method creates the pop-up that is displayed when a Nzb file is
     * opened with SABDroidEx
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        OnClickListener clickListener = new DialogInterface.OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                if (whichButton == Dialog.BUTTON_POSITIVE) {
                    SABnzbdController.addFile(messageHandler, FileUtil.getFileName(path),
                            FileUtil.getFileAsCharArray(path));
                }
            }
        };
        
        String validation = getResources().getString(R.string.send_validation);
        String message = String.format("%s\n%s", validation, FileUtil.getFileName(path));
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
        builder.setTitle(R.string.send_file);
        builder.setPositiveButton(android.R.string.ok, clickListener);
        builder.setNegativeButton(android.R.string.cancel, clickListener);
        builder.setMessage(message);
        
        return builder.create();
    }
}
