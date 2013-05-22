package com.sabdroidex.fragments.dialogs.sabnzbd;

import com.sabdroidex.R;
import com.sabdroidex.controllers.sabnzbd.SABnzbdController;
import com.sabdroidex.data.sabnzbd.HistoryElement;
import com.sabdroidex.utils.SABHandler;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * This dialog is displayed when a user makes a long press on an history list item.
 * It gives the opportunity to remove the given item from the list.
 * @author Marc
 *
 */
public class HistoryRemoveDialog extends DialogFragment {

    private static SABHandler messageHandler;
    private static HistoryElement historyElement;

    public static void setMessageHandler(SABHandler messageHandler) {
        HistoryRemoveDialog.messageHandler = messageHandler;
    }

    public static void setHistoryElement(HistoryElement historyElement) {
        HistoryRemoveDialog.historyElement = historyElement;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        
        AlertDialog dialog = null;
        OnClickListener onClickListener = new DialogInterface.OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        };
        
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setNegativeButton(android.R.string.cancel, onClickListener);
        builder.setTitle(historyElement.getName());
        String[] options = new String[1];
        options[0] = getActivity().getResources().getString(R.string.menu_delete);
        
        builder.setItems(options, new OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        SABnzbdController.removeHistoryItem(messageHandler, historyElement.getNzoId());
                        break;
                    default:
                        break;
                }
            }
        });
        
        return dialog;
    }
}
