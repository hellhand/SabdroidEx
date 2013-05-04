package com.sabdroidex.fragments.dialogs.couchpotato;

import com.sabdroidex.R;
import com.sabdroidex.controllers.couchpotato.CouchPotatoController;
import com.sabdroidex.data.couchpotato.Movie;

import android.app.Dialog;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class MovieReleaseDialog extends DialogFragment {

	private Movie movie;
	private Handler messageHandler;

	public MovieReleaseDialog(Movie movie, Handler messageHandler) {
		this.movie = movie;
		this.messageHandler = messageHandler;
	}

	public Dialog onCreateDialog(Bundle savedInstanceState) {
		final Dialog dialog = new Dialog(getActivity());

		dialog.setContentView(R.layout.list_movie_release);
		dialog.setTitle(R.string.movie_release_titel);

		TableLayout table = (TableLayout) dialog
				.findViewById(R.id.movie_release_table);
		LayoutInflater inflater = LayoutInflater.from(getActivity());

		View.OnClickListener onClickListener = new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				int releaseId;
				if (v.getParent().getClass() == TableRow.class) {
					TableRow curRow = (TableRow) v.getParent();
					releaseId = curRow.getId();
				} else {
					return;
				}
				if (R.id.movie_release_download == v.getId()) {
					CouchPotatoController.downloadRelease(messageHandler,
							releaseId);
				} else if (R.id.movie_release_ignore == v.getId()) {
					CouchPotatoController.ignoreRelease(messageHandler,
							releaseId);
				}
			}
		};
		//TODO: un-werk this
		Object[] releases = new Object[0];
		for (int d = 0; d < releases.length; d++) {
			Object[] curRelease = (Object[]) releases[d];
			TableRow row = (TableRow) inflater.inflate(
					R.layout.list_movie_release_row, null);
			if (((String) curRelease[2]).equals("Snatched")) {
				((TextView) row.findViewById(R.id.movie_release_ind))
						.setBackgroundColor(R.color.movie_release_current_release);
			} else if (((String) curRelease[2]).equals("Ignored")) {
				((TextView) row.findViewById(R.id.movie_release_ind))
						.setBackgroundColor(R.color.movie_release_ignored_release);
			} else {
				((TextView) row.findViewById(R.id.movie_release_ind))
						.setBackgroundColor(R.color.movie_release_default);
			}
			((TextView) row.findViewById(R.id.movie_release_name))
					.setText((String) curRelease[1]);
			((TextView) row.findViewById(R.id.movie_release_size))
					.setText((String) curRelease[4]);
			((ImageButton) row.findViewById(R.id.movie_release_download))
					.setOnClickListener(onClickListener);
			((ImageButton) row.findViewById(R.id.movie_release_ignore))
					.setOnClickListener(onClickListener);
			row.setId((Integer) curRelease[0]);

			if ((getActivity().getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE
					&& getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
				((TextView) row.findViewById(R.id.movie_release_status))
						.setText((String) curRelease[2]);
				((TextView) row.findViewById(R.id.movie_release_quality))
						.setText((String) curRelease[3]);
				((TextView) row.findViewById(R.id.movie_release_provider))
						.setText((String) curRelease[5]);
			}

			table.addView(row);
		}
		return dialog;
	}
}