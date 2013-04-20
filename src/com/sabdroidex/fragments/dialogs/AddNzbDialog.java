package com.sabdroidex.fragments.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.widget.EditText;

import com.sabdroidex.R;
import com.sabdroidex.controllers.sabnzbd.SABnzbdController;

public class AddNzbDialog extends DialogFragment {

	private static Handler messageHandler;

	public static void setMessageHandler(Handler messageHandler) {
		AddNzbDialog.messageHandler = messageHandler;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		AlertDialog.Builder builder = new AlertDialog.Builder(
				this.getActivity());

		builder.setTitle(R.string.add_nzb_dialog_title);
		builder.setMessage(R.string.add_nzb_dialog_message);

		final EditText input = new EditText(this.getActivity());
		builder.setView(input);

		builder.setPositiveButton(android.R.string.ok,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int whichButton) {
						String value = input.getText().toString();
						SABnzbdController.addByURL(messageHandler, value);
						dialog.dismiss();
					}
				});

		builder.setNegativeButton(android.R.string.cancel,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.dismiss();
					}
				});

		return builder.create();
	}
}
