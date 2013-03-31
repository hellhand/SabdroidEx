package com.sabdroidex.activity;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.android.actionbarcompat.ActionBarActivity;
import com.sabdroidex.R;
import com.sabdroidex.adapters.SeasonEpisodeAdapater;
import com.sabdroidex.controllers.sickbeard.SickBeardController;
import com.sabdroidex.data.Season;

public class SeasonActivity extends ActionBarActivity {
    
    private static final String TAG = SeasonActivity.class.getCanonicalName();
    
    private Integer mShowId;
    private Integer mSeasonId;
    private SeasonEpisodeAdapater seasonEpisodeAdapater;
    private Season mSeason;
    
    /**
     * Instantiating the Handler associated with this {@link Fragment}.
     */
    private final Handler messageHandler = new Handler() {
        
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == SickBeardController.MESSAGE.SHOW_SEASONS.ordinal() && msg.obj instanceof Season) {
                mSeason = (Season) msg.obj;
                seasonEpisodeAdapater.setEpisodes(mSeason.getEpisodes());
                seasonEpisodeAdapater.notifyDataSetChanged();
            }
        }
    };
    
    /**
     * A {@link OnItemLongClickListener} listening the episode list. This will
     * have the duty to display a {@link DialogFragment} to allow to user to
     * view the episode info and to start a manual search.
     */
    OnItemLongClickListener longClickListener = new OnItemLongClickListener() {
        
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            // TODO Auto-generated method stub
            return false;
        }
    };
    
    /**
     * This method will create the adapter needed for the grid and query the
     * controller to retrieve the full season data.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.list);
        
        mShowId = getIntent().getExtras().getInt("tvdbid");
        mSeasonId = getIntent().getExtras().getInt("season");
        
        seasonEpisodeAdapater = new SeasonEpisodeAdapater(this, mSeason.getEpisodes());
        
        ListView listView = (ListView) findViewById(R.id.elementList);
        listView.setAdapter(seasonEpisodeAdapater);
        listView.setOnItemLongClickListener(longClickListener);
        
        SickBeardController.getSeason(messageHandler, mShowId.toString(), mSeasonId.toString());
    }
}
