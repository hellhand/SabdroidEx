package com.sabdroidex.activity;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.actionbarcompat.ActionBarActivity;
import com.sabdroidex.R;
import com.sabdroidex.adapters.SeasonEpisodeAdapater;
import com.sabdroidex.controllers.sickbeard.SickBeardController;
import com.sabdroidex.data.sickbeard.Episode;
import com.sabdroidex.data.sickbeard.Season;
import com.sabdroidex.data.sickbeard.Show;
import com.sabdroidex.fragments.dialogs.ShowEpisodeDialog;
import com.sabdroidex.utils.ImageUtils;
import com.sabdroidex.utils.ImageWorker.ImageType;

public class SeasonActivity extends ActionBarActivity {
    
    private static final String TAG = SeasonActivity.class.getCanonicalName();
    
    private Show mShow;
    private Integer mSeasonNr;
    private SeasonEpisodeAdapater seasonEpisodeAdapater;
    private Season mSeason;
    
    private TextView episodeCount;
    
    /**
     * Instantiating the Handler associated with this {@link Fragment}.
     */
    private final Handler messageHandler = new Handler() {
        
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == SickBeardController.MESSAGE.SHOW_SEASONS.ordinal() && msg.obj instanceof Season) {
                mSeason = (Season) msg.obj;
                seasonEpisodeAdapater.setSeason(mSeason);
                seasonEpisodeAdapater.notifyDataSetChanged();
                
                String count = new Integer(mSeason.getEpisodes().size()).toString();
                episodeCount.setText(count);
                
                String title = mShow.getShowName() + " - " + getString(R.string.show_season) + " " + mSeasonNr;
                setTitle(title);
            }
            if (msg.what == SickBeardController.MESSAGE.EPISODE_SETSTATUS.ordinal()) {
                try {
                    String text = getString(R.string.episode_status_set_to) + " : " + msg.obj;
                    Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
                }
                catch (Exception e) {
                    Log.w(TAG, e.getLocalizedMessage());
                }
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
            Episode episode = mSeason.getEpisodes().get(position);
            episode.setShowId(mShow.getTvdbId());
            episode.setSeasonNr(mSeason.getSeasonNr());
            ShowEpisodeDialog showEpisodeDialog = new ShowEpisodeDialog(messageHandler, mSeason.getEpisodes().get(position));
            showEpisodeDialog.show(getSupportFragmentManager(), "status");
            return true;
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
        setContentView(R.layout.episode_list);
        
        mShow = (Show) getIntent().getExtras().get("show");
        mSeasonNr = getIntent().getExtras().getInt("season");
        
        seasonEpisodeAdapater = new SeasonEpisodeAdapater(this, mSeason);
        
        ImageView header = (ImageView) findViewById(R.id.image_header);
        String imageKey = ImageType.SHOW_SEASON_POSTER.name() + mShow.getTvdbId() + mSeasonNr;
        ImageUtils.getImageWorker().loadImage(header, ImageType.SHOW_SEASON_POSTER, imageKey, mShow.getTvdbId(), mShow.getShowName(), mSeasonNr);
        
        GridView gridView = (GridView) findViewById(R.id.episodeGrid);
        gridView.setAdapter(seasonEpisodeAdapater);
        gridView.setOnItemLongClickListener(longClickListener);
        
        TextView textView = (TextView) findViewById(R.id.season_number);
        textView.setText(mSeasonNr.toString());
        
        episodeCount = (TextView) findViewById(R.id.season_episode_count);
        
        refreshSeason();
    }
    
    /**
     * This creates the menu items as they will be by default
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.refresh, menu);
        return super.onCreateOptionsMenu(menu);
    }
    
    /**
     * Handles item selections in the Menu
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                refreshSeason();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void refreshSeason() {
        SickBeardController.getSeason(messageHandler, mShow.getTvdbId().toString(), mSeasonNr.toString());
    }
}
