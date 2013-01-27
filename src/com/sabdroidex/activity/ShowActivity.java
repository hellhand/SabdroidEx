package com.sabdroidex.activity;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.GridView;

import com.android.actionbarcompat.ActionBarActivity;
import com.sabdroidex.R;
import com.sabdroidex.adapters.ShowSeasonAdapater;
import com.sabdroidex.controllers.sickbeard.SickBeardController;
import com.sabdroidex.data.Show;

public class ShowActivity extends ActionBarActivity {
    
    private static final String TAG = ShowActivity.class.getCanonicalName();
    
    private GridView mGridView;
    private ShowSeasonAdapater showSeasonAdapater;
    private Integer mShowId;
    private Show mShow;
    
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
        mShowId = getIntent().getExtras().getInt("tvrageid");
        
        mGridView = (GridView) findViewById(R.id.show_seasons_grid);
        showSeasonAdapater = new ShowSeasonAdapater(this);
        mGridView.setAdapter(showSeasonAdapater);
        
        SickBeardController.getShow(messageHandler, mShowId.toString());
    }
}