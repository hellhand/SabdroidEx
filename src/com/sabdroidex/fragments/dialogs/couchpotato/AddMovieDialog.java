package com.sabdroidex.fragments.dialogs.couchpotato;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.widget.EditText;
import android.widget.Toast;

import com.sabdroidex.R;
import com.sabdroidex.controllers.couchpotato.CouchPotatoController;

public class AddMovieDialog extends DialogFragment {

    private static Handler messageHandler;

    public static void setMessageHandler(Handler messageHandler) {
        AddMovieDialog.messageHandler = messageHandler;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.add_movie_dialog_title);
        builder.setMessage(R.string.add_movie_dialog_message);

        final EditText input = new EditText(getActivity());
        builder.setView(input);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString();
                Toast.makeText(getActivity(), getActivity().getApplicationContext().getText(R.string.add_show_background_search), Toast.LENGTH_LONG).show();
                CouchPotatoController.searchMovie(messageHandler, value);
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
