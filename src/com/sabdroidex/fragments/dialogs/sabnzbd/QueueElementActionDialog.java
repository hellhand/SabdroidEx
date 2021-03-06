package com.sabdroidex.fragments.dialogs.sabnzbd;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;

import com.sabdroidex.R;
import com.sabdroidex.activity.QueueItemEditActivity;
import com.sabdroidex.controllers.sabnzbd.SABnzbdController;
import com.sabdroidex.data.sabnzbd.QueueElement;

public class QueueElementActionDialog extends DialogFragment {

    private QueueElement element;
    private Handler messageHandler;
    
    public QueueElementActionDialog(Handler messageHandler ,QueueElement element) {
        this.messageHandler = messageHandler;
        this.element = element;
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
        builder.setTitle(element.getFilename());
        
        String[] options = new String[3];
        if ("Paused".equals(element.getStatus())) {
            options[0] = getActivity().getResources().getString(R.string.menu_resume);
        }
        else {
            options[0] = getActivity().getResources().getString(R.string.menu_pause);
        }
        options[1] = getActivity().getResources().getString(R.string.menu_delete);
        options[2] = getActivity().getResources().getString(R.string.queue_item_edit);
        
        builder.setItems(options, new OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        SABnzbdController.pauseResumeItem(messageHandler, element);
                        dialog.dismiss();
                        break;
                    case 1:
                        SABnzbdController.removeQueueItem(messageHandler, element);
                        dialog.dismiss();
                        break;
                    case 2:
                        Intent intent = new Intent(getActivity().getBaseContext(), QueueItemEditActivity.class);
                        intent.putExtra("element", element);
                        getActivity().startActivity(intent);
                        dialog.dismiss();
                        break;
                    default:
                        break;
                }
            }
        });
        
        return builder.create();
    }
}
