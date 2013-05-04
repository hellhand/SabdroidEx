package com.sabdroidex.activity;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.android.actionbarcompat.ActionBarActivity;
import com.sabdroidex.R;
import com.sabdroidex.adapters.ShowSeasonAdapater;
import com.sabdroidex.controllers.sickbeard.SickBeardController;
import com.sabdroidex.data.sickbeard.Show;

public class ShowActivity extends ActionBarActivity {
    
    private static final String TAG = ShowActivity.class.getCanonicalName();
    
    private ShowSeasonAdapater showSeasonAdapater;
    private Integer mShowId;
    private Show mShow;
    
    /**
     * Instantiating the Handler associated with this {@link Fragment}. It will
     * be notified when the request to retrieve the show data is successful
     */
    private final Handler messageHandler = new Handler() {
        
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == SickBeardController.MESSAGE.SHOW.hashCode() && msg.obj instanceof Show) {
                mShow = (Show) msg.obj;
                mShow.setTvdbId(mShowId);
                showSeasonAdapater.setShow(mShow);
                showSeasonAdapater.notifyDataSetChanged();
                
                setTitle(mShow.getShowName());
            }
        }
    };
    
    /**
     * A {@link OnItemClickListener} listening the season grid. This will have
     * the duty to display a new {@link FragmentActivity} with the apisodes of
     * the selected season
     */
    private OnItemClickListener itemClickListener = new OnItemClickListener() {
        
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(getBaseContext(), SeasonActivity.class);
            intent.putExtra("show", mShow);
            intent.putExtra("season", mShow.getSeasonList().get(position));
            startActivity(intent);
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
        setContentView(R.layout.grid);
        
        mShowId = getIntent().getExtras().getInt("tvdbid");
        showSeasonAdapater = new ShowSeasonAdapater(this, mShow);
        
        GridView gridView = (GridView) findViewById(R.id.elementGrid);
        gridView.setAdapter(showSeasonAdapater);
        gridView.setOnItemClickListener(itemClickListener);
        
        refresh();
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
                refresh();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void refresh() {
        SickBeardController.getShow(messageHandler, mShowId.toString());
    }
}