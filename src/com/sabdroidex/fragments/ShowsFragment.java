package com.sabdroidex.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sabdroidex.R;
import com.sabdroidex.adapters.ShowsListRowAdapter;
import com.sabdroidex.controllers.sickbeard.SickBeardController;
import com.sabdroidex.data.JSONBased;
import com.sabdroidex.data.sickbeard.Show;
import com.sabdroidex.data.sickbeard.ShowList;
import com.sabdroidex.fragments.dialogs.sickbeard.ShowDetailsDialog;
import com.sabdroidex.utils.ImageUtils;
import com.sabdroidex.utils.ImageWorker.ImageType;
import com.sabdroidex.utils.Preferences;
import com.sabdroidex.utils.SABHandler;

public class ShowsFragment extends SABFragment {
    
    private static final String TAG = ShowsFragment.class.getCanonicalName();
    
    private static ShowList showList;
    private ShowsListRowAdapter mShowsListRowAdapter;
    
    /**
     * Instantiating the Handler associated with this {@link Fragment}.
     */
    private final SABHandler messageHandler = new SABHandler() {
        
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == SickBeardController.MESSAGE.SHOWS.hashCode()) {
                try {
                    showList = (ShowList) msg.obj;
                    
                    if (mShowsListRowAdapter != null) {
                        mShowsListRowAdapter.clear();
                        mShowsListRowAdapter.addAll(showList.getShowElements());
                        mShowsListRowAdapter.notifyDataSetChanged();
                    }
                }
                catch (Exception e) {
                    Log.e(TAG, e.getLocalizedMessage());
                }
            }
            if (msg.what == SickBeardController.MESSAGE.UPDATE.hashCode()) {
                try {
                    if (msg.obj instanceof String && !"".equals((String) msg.obj)) {
                        Toast.makeText(getParentActivity(), (String) msg.obj, Toast.LENGTH_LONG).show();
                    }
                }
                catch (Exception e) {
                    Log.w(TAG, e.getLocalizedMessage());
                }
            }
        }
    };
    
    public ShowsFragment() {
        showList = new ShowList();
    }
    
    public ShowsFragment(ShowList showsRows) {
        showList = showsRows;
    }
    
    @Override
    public int getTitle() {
        return R.string.tab_shows;
    }
    
    /**
     * Refreshing the shows during startup or on user request. Asks to configure
     * if still not done
     */
    public void manualRefreshShows() {
        if (!Preferences.isEnabled(Preferences.SICKBEARD)) {
            return;
        }
        SickBeardController.refreshShows(messageHandler);
    }
    
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        messageHandler.setActivity(getActivity());
        super.onCreate(savedInstanceState);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        
        LinearLayout showView;
        if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE && getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            showView = (LinearLayout) inflater.inflate(R.layout.show_list, null);
        }
        else {
            showView = (LinearLayout) inflater.inflate(R.layout.simplelist, null);
        }
        
        ListView listView = (ListView) showView.findViewById(R.id.queueList);
        
        mShowsListRowAdapter = new ShowsListRowAdapter(getActivity().getApplicationContext(), showList.getShowElements());
        listView.setAdapter(mShowsListRowAdapter);
        ListItemClickListener listItemClickListener = new ListItemClickListener();
        if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE && getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            listView.setOnItemClickListener(listItemClickListener);
            listView.setItemChecked(0, true);
        }
        else {
            listView.setOnItemLongClickListener(new ListItemLongClickListener());
        }
        
        manualRefreshShows();
        
        return showView;
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        if ((getActivity().getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE
                && getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (showList.getShowElements().size() > 0) {
                getView().findViewById(R.id.show_list_right_pane).setVisibility(View.VISIBLE);
            }
        }
        super.onActivityCreated(savedInstanceState);
    }
    
    @Override
    public JSONBased getDataCache() {
        return showList;
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
    
    private class ListItemClickListener implements OnItemClickListener {
        
        /**
         * When an item is selected by a click the show details are displayed at
         * the side of the ListView
         */
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // Setup of the show details
            setupShowElements(getView(), showList.getShowElements().get(position));
            mShowsListRowAdapter.notifyDataSetInvalidated();
        }
        
    }
    
    private class ListItemLongClickListener implements OnItemLongClickListener {
        
        /**
         * When an item is selected by a long click a Dialog appears to display
         * the show details.
         */
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
            ShowDetailsDialog showDetailsDialog = new ShowDetailsDialog(showList.getShowElements().get(position));
            showDetailsDialog.show(getActivity().getSupportFragmentManager(), "show");
            return true;
        }
        
    }
}
