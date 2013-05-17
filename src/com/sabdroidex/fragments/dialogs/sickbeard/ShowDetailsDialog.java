package com.sabdroidex.fragments.dialogs.sickbeard;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.sabdroidex.R;
import com.sabdroidex.activity.ShowActivity;
import com.sabdroidex.data.sickbeard.Show;
import com.sabdroidex.utils.ImageUtils;
import com.sabdroidex.utils.ImageWorker.ImageType;

public class ShowDetailsDialog extends DialogFragment {
    
    private static Show mShow;
    
    public static void setShow(Show mShow) {
        ShowDetailsDialog.mShow = mShow;
    }
    
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        
        LayoutInflater inflater = LayoutInflater.from(getActivity().getBaseContext());
        ScrollView mShowView = (ScrollView) inflater.inflate(R.layout.show_status, null);
        
        setupShowElements(mShowView, mShow);

        builder.setPositiveButton(R.string.more, new DialogInterface.OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                Intent intent = new Intent(getActivity().getBaseContext(), ShowActivity.class);
                intent.putExtra("tvdbid", mShow.getTvdbId());
                getActivity().startActivity(intent);
                dialog.dismiss();
            }
        });
        
        builder.setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });
        
        builder.setView(mShowView);
        
        return builder.create();
    }
    
    @Override
    public void onDismiss(DialogInterface dialog) {
        mShow = null;
        super.onDismiss(dialog);
    }
    
    public void setupShowElements(View view, Show show) {        
        ImageView showPoster = (ImageView) view.findViewById(R.id.showPoster);
        
        TextView showName = (TextView) view.findViewById(R.id.show_name);
        showName.setText(show.getShowName());
        
        TextView showStatus = (TextView) view.findViewById(R.id.show_status);
        showStatus.setText(show.getStatus());
        
        TextView showQuality = (TextView) view.findViewById(R.id.show_quality);
        showQuality.setText(show.getQuality());
        
        TextView showNextEpisode = (TextView) view.findViewById(R.id.show_next_episode);
        showNextEpisode.setText(show.getNextEpAirdate());
        
        TextView showNetwork = (TextView) view.findViewById(R.id.show_network);
        showNetwork.setText(show.getNetwork());
        
        TextView showLanguage = (TextView) view.findViewById(R.id.show_language);
        showLanguage.setText(show.getLanguage());
        
        String imageKey = ImageType.SHOW_POSTER.name() + show.getTvdbId();
        ImageUtils.getImageWorker().loadImage(showPoster, ImageType.SHOW_POSTER, imageKey, show.getTvdbId(), show.getShowName());
    }
}
