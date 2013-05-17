package com.sabdroidex.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sabdroidex.R;
import com.sabdroidex.activity.ShowActivity;
import com.sabdroidex.adapters.ShowsGridAdapter;
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
    private GridView showGrid;
    private ShowsGridAdapter mShowsGridAdapter;

    /**
     * Instantiating the Handler associated with this {@link Fragment}.
     */
    private final SABHandler messageHandler = new SABHandler() {

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == SickBeardController.MESSAGE.SHOWS.hashCode()) {

                showList = (ShowList) msg.obj;

                if (mShowsGridAdapter != null) {
                    mShowsGridAdapter.setDataSet(showList.getShowElements());
                    mShowsGridAdapter.notifyDataSetChanged();
                    if (showList.getShowElements().size() > 0) {
                        showGrid.performItemClick(showGrid, 0, showGrid.getItemIdAtPosition(0));
                    }
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

    /**
     * 
     */
    public ShowsFragment() {
        showList = new ShowList();
    }

    /**
     * 
     * @param showsRows
     */
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

        mShowsGridAdapter = new ShowsGridAdapter(getActivity().getApplicationContext(), showList.getShowElements());

        LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.show_list, null);
        showGrid = (GridView) linearLayout.findViewById(R.id.elementGrid);
        showGrid.setAdapter(mShowsGridAdapter);

        LinearLayout layout = (LinearLayout) linearLayout.findViewById(R.id.showStatus);
        if (layout != null) {
            showGrid.setOnItemClickListener(new GridItemClickListener());
            showGrid.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
            mShowsGridAdapter.showOverlay(true);
        }
        else {
            showGrid.setOnItemLongClickListener(new GridItemLongClickListener());
        }

        manualRefreshShows();

        return linearLayout;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        if (showList.getShowElements().size() > 0) {
            showGrid.performItemClick(showGrid, 0, showGrid.getItemIdAtPosition(0));
        }
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
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

        Button moreButton = (Button) view.findViewById(R.id.more_button);
        moreButton.setOnClickListener(new MoreButtonClickListener(show));
        
        String imageKey = ImageType.SHOW_POSTER.name() + show.getTvdbId();
        ImageUtils.getImageWorker().loadImage(showPoster, ImageType.SHOW_POSTER, imageKey, show.getTvdbId(), show.getShowName());
    }

    private class GridItemClickListener implements OnItemClickListener {

        /**
         * When an item is selected by a click the show details are displayed at
         * the side of the ListView
         */
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            setupShowElements(getView(), showList.getShowElements().get(position));
            showGrid.invalidateViews();
        }

    }

    private class GridItemLongClickListener implements OnItemLongClickListener {

        /**
         * When an item is selected by a long click a Dialog appears to display
         * the show details.
         */
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
            ShowDetailsDialog showDetailsDialog = new ShowDetailsDialog();
            ShowDetailsDialog.setShow(showList.getShowElements().get(position));
            showDetailsDialog.show(getActivity().getSupportFragmentManager(), "show");
            return true;
        }

    }
    
    private class MoreButtonClickListener implements OnClickListener {
        
        private Show show;
        
        public MoreButtonClickListener(Show show) {
            this.show = show;
        }
        
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity().getBaseContext(), ShowActivity.class);
            intent.putExtra("tvdbid", show.getTvdbId());
            getActivity().startActivity(intent);
        }
    }
}
