package com.sabdroidex.activity;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.GridView;

import com.sabdroidex.R;
import com.sabdroidex.adapters.ShowSeasonAdapater;
import com.sabdroidex.controllers.sickbeard.SickBeardController;
import com.sabdroidex.data.Show;

public class ShowActivity extends Activity {
    
    private static final String TAG = ShowActivity.class.getCanonicalName();
    
    private ShowSeasonAdapater showSeasonAdapater;
    private Integer mShowId;
    private static Show mShow;
    
    /**
     * Instantiating the Handler associated with this {@link Fragment}.
     */
    private final Handler messageHandler = new Handler() {
        
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == SickBeardController.MESSAGE.SHOW.ordinal() && msg.obj instanceof Show) {
                mShow = (Show) msg.obj;
                mShow.setTvdbId(mShowId);
                showSeasonAdapater.setShow(mShow);
                showSeasonAdapater.notifyDataSetChanged();
            }
        }
    };
    
    /**
     * This method will create the adapter needed for the grid and query the
     * controller to retrieve the full show data.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.show_details);

        mShowId = getIntent().getExtras().getInt("tvdbid");
        showSeasonAdapater = new ShowSeasonAdapater(this, mShow);
        
        GridView gridView = (GridView) findViewById(R.id.show_seasons_grid);
        gridView.setAdapter(showSeasonAdapater);
        
        SickBeardController.getShow(messageHandler, mShowId.toString());
    }
    
    @Override
    protected void onDestroy() {        
        super.onDestroy();
    }
    
    @Override
    protected void onStop() {
        super.onStop();
    }
}