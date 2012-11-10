package com.sabdroidex.fragments;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.sabdroidex.R;
import com.sabdroidex.activity.SABDroidEx;
import com.sabdroidex.adapters.ShowsListRowAdapter;
import com.sabdroidex.sickbeard.SickBeardController;
import com.sabdroidex.utils.AsyncImage;
import com.sabdroidex.utils.Preferences;
import com.sabdroidex.utils.SABDFragment;
import com.sabdroidex.utils.SABDroidConstants;

public class ShowsFragment extends SABDFragment implements OnItemClickListener, OnItemLongClickListener {

    private static final String TAG = "ShowsFragment";

    private static ArrayList<Object[]> rows;
    private static Bitmap mEmptyPoster;
    private ListView mListView;
    private ScrollView mShowView;
    private AsyncImage mAsyncImage;
    private ShowsListRowAdapter mShowsListRowAdapter;

    /**
     *  Instantiating the Handler associated with this {@link Fragment}.
     */
    private final Handler messageHandler = new Handler() {

        @Override
        @SuppressWarnings("unchecked")
        public void handleMessage(Message msg) {
            Object result[];
            if (msg.what == SickBeardController.MESSAGE.SHOWS.ordinal()) {
                result = (Object[]) msg.obj;
                // Updating rows
                rows.clear();
                rows.addAll((ArrayList<Object[]>) result[1]);

                /**
                 * This might happen if a rotation occurs
                 */
                if (mListView != null || getAdapter(mListView) != null) {
                    ArrayAdapter<Object[]> adapter = getAdapter(mListView);
                    adapter.notifyDataSetChanged();
                    ((SABDroidEx) mParent).updateStatus(true);
                }
            }
            if (msg.what == SickBeardController.MESSAGE.SB_SEARCHTVDB.ordinal()) {
                try {
                    result = (Object[]) msg.obj;
                    selectShowPrompt((ArrayList<Object[]>) result[1]);
                }
                catch (Exception e) {
                    Log.w(TAG, e.getLocalizedMessage());
                }
            }
            if (msg.what == SickBeardController.MESSAGE.SHOW_ADDNEW.ordinal()) {
                try {
                    Toast.makeText(mParent, getString(R.string.add_show_dialog_title) + " : " + msg.obj, Toast.LENGTH_LONG);
                }
                catch (Exception e) {
                    Log.w(TAG, e.getLocalizedMessage());
                }
            }
            if (msg.what == SickBeardController.MESSAGE.UPDATE.ordinal()) {
                try {
                    if (msg.obj instanceof String && !"".equals((String) msg.obj)) {
                        Toast.makeText(mParent, (String) msg.obj, Toast.LENGTH_LONG).show();
                    }
                }
                catch (Exception e) {
                    Log.w(TAG, e.getLocalizedMessage());
                }
            }
        }
    };

    /**
     * This {@link Fragment} parent {@link Activity}.
     */
    private FragmentActivity mParent;

    /**
     * Default Constructor.
     */
    public ShowsFragment() {}

    /**
     * Constructor with parent {@link Activity}
     * 
     * @param fragmentActivity the parent {@link Activity}.
     */
    public ShowsFragment(FragmentActivity fragmentActivity) {
        mParent = fragmentActivity;
        if (mEmptyPoster == null) {
            Options BgOptions = new Options();
            BgOptions.inPurgeable = true;
            BgOptions.inPreferredConfig = Config.RGB_565;
            if (Preferences.isEnabled(Preferences.SICKBEARD_LOWRES)) {
                BgOptions.inSampleSize = 2;
            }
            mEmptyPoster = BitmapFactory.decodeResource(mParent.getResources(), R.drawable.temp_poster, BgOptions);
        }
    }

    /**
     * Constructor with parent {@link Activity} and the rows to display.
     * 
     * @param sabDroidEx the parent {@link Activity}.
     * @param historyRows the rows to dispplay.
     */
    public ShowsFragment(FragmentActivity sabDroidEx, ArrayList<Object[]> historyRows) {
        this(sabDroidEx);
        rows = historyRows;
    }

    /**
     * Getter for this {@link Fragment}'s message {@link Handler}
     * 
     * @return the message {@link Handler} for this {@link Activity}
     */
    public Handler getMessageHandler() {
        return messageHandler;
    }

    @SuppressWarnings("unchecked")
    private ArrayAdapter<Object[]> getAdapter(ListView listView) {
        return listView == null ? null : (ArrayAdapter<Object[]>) listView.getAdapter();
    }

    @Override
    public String getTitle() {
        return mParent.getString(R.string.tab_shows);
    }

    /**
     * Refreshing the queue during startup or on user request. Asks to configure if still not done
     */
    public void manualRefreshShows() {
        // First run setup
        if (!Preferences.isEnabled(Preferences.SICKBEARD)) {
            return;
        }
        SickBeardController.refreshShows(messageHandler);
    }

    @Override
    public void onAttach(Activity activity) {
        mParent = (FragmentActivity) activity;
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        SharedPreferences preferences = mParent.getSharedPreferences(SABDroidConstants.PREFERENCES_KEY, 0);
        Preferences.update(preferences);

        LinearLayout showView;
        if ((mParent.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE
                && mParent.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            showView = (LinearLayout) inflater.inflate(R.layout.show_list, null);
        }
        else {
            showView = (LinearLayout) inflater.inflate(R.layout.simplelist, null);
        }

        mListView = (ListView) showView.findViewById(R.id.queueList);

        mShowsListRowAdapter = new ShowsListRowAdapter(mParent, rows);
        mListView.setAdapter(mShowsListRowAdapter);
        if ((mParent.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE
                && mParent.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            mListView.setOnItemClickListener(this);
        }
        else {
            mListView.setOnItemLongClickListener(this);
        }
        // Tries to fetch recoverable data
        Object data[] = (Object[]) mParent.getLastCustomNonConfigurationInstance();
        if (data != null && extracted(data, 2) != null) {
            rows = extracted(data, 2);
        }

        if (rows.size() > 0) {
            ArrayAdapter<Object[]> adapter = getAdapter(mListView);
            adapter.notifyDataSetChanged();
        }
        else {
            manualRefreshShows();
        }

        return showView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        if ((mParent.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE
                && mParent.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (rows.size() > 0) {
                getView().findViewById(R.id.show_list_right_pane).setVisibility(View.VISIBLE);
                onItemClick(null, null, 0, 0);
            }
        }
        super.onActivityCreated(savedInstanceState);
    }
    
    @Override
    public void onDestroy() {
    	super.onDestroy();
    }

    @Override
    protected void clearAdapter() {
        if (mShowsListRowAdapter != null)
            mShowsListRowAdapter.clearBitmaps();
    }

    @Override
    public void onFragmentActivated() {
        manualRefreshShows();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mShowsListRowAdapter.setSelectedItem(position);

        ImageView showPoster = (ImageView) getView().findViewById(R.id.showPoster);
        showPoster.setImageBitmap(mEmptyPoster);

        TextView showName = (TextView) getView().findViewById(R.id.show_name);
        showName.setText((CharSequence) rows.get(position)[0]);

        GradientDrawable gradientDrawable = (GradientDrawable) getResources().getDrawable(R.drawable.rounded_edges);
        gradientDrawable.setColor(Color.TRANSPARENT);

        TextView showStatus = (TextView) getView().findViewById(R.id.show_status);
        showStatus.setText((CharSequence) rows.get(position)[1]);
        if ("Ended".equals(rows.get(position)[1])) {
            gradientDrawable = (GradientDrawable) getResources().getDrawable(R.drawable.rounded_edges);
            gradientDrawable.setColor(Color.rgb(255, 50, 50));
            showStatus.setBackgroundDrawable(gradientDrawable);
            showStatus.setTextColor(Color.WHITE);
        }
        else {
            gradientDrawable = (GradientDrawable) getResources().getDrawable(R.drawable.rounded_edges);
            gradientDrawable.setColor(Color.TRANSPARENT);
            showStatus.setBackgroundDrawable(gradientDrawable);
            showStatus.setTextColor(Color.BLACK);
        }

        TextView showQuality = (TextView) getView().findViewById(R.id.show_quality);
        showQuality.setText((CharSequence) rows.get(position)[2]);
        showQuality.setTextColor(Color.WHITE);
        gradientDrawable = (GradientDrawable) getResources().getDrawable(R.drawable.rounded_edges);
        if ("Any".equals(rows.get(position)[2])) {
            gradientDrawable.setColor(Color.rgb(68, 68, 68));
        }
        if ("SD".equals(rows.get(position)[2])) {
            gradientDrawable.setColor(Color.rgb(153, 68, 68));
        }
        if ("HD".equals(rows.get(position)[2])) {
            gradientDrawable.setColor(Color.rgb(68, 153, 68));
        }
        showQuality.setBackgroundDrawable(gradientDrawable);

        TextView showNextEpisode = (TextView) getView().findViewById(R.id.show_next_episode);
        showNextEpisode.setText((CharSequence) rows.get(position)[3]);

        TextView showNetwork = (TextView) getView().findViewById(R.id.show_network);
        showNetwork.setText((CharSequence) rows.get(position)[4]);

        TextView showLanguage = (TextView) getView().findViewById(R.id.show_language);
        showLanguage.setText((CharSequence) rows.get(position)[6]);

        if (mAsyncImage != null && mAsyncImage.getStatus() == (AsyncTask.Status.RUNNING)) {
            mAsyncImage.cancel(true);
        }
        mAsyncImage = new AsyncImage();
        mAsyncImage.execute(getActivity(), imageHandler, rows.get(position)[5], rows.get(position)[0], SickBeardController.MESSAGE.SHOW_GETPOSTER, 0);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

        AlertDialog dialog = null;
        OnClickListener onClickListener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                if (mAsyncImage != null && mAsyncImage.getStatus() == (AsyncTask.Status.RUNNING)) {
                    mAsyncImage.cancel(true);
                }
                mAsyncImage = null;
                dialog.dismiss();
            }
        };
        LayoutInflater inflater = LayoutInflater.from(mParent);
        mShowView = (ScrollView) inflater.inflate(R.layout.show_status, null);

        ImageView showPoster = (ImageView) mShowView.findViewById(R.id.showPoster);
        showPoster.setImageBitmap(mEmptyPoster);

        TextView showName = (TextView) mShowView.findViewById(R.id.show_name);
        showName.setText((CharSequence) rows.get(position)[0]);

        GradientDrawable gradientDrawable = (GradientDrawable) getResources().getDrawable(R.drawable.rounded_edges);
        gradientDrawable.setColor(Color.TRANSPARENT);

        TextView showStatus = (TextView) mShowView.findViewById(R.id.show_status);
        showStatus.setText((CharSequence) rows.get(position)[1]);
        if ("Ended".equals(rows.get(position)[1])) {
            gradientDrawable = (GradientDrawable) getResources().getDrawable(R.drawable.rounded_edges);
            gradientDrawable.setColor(Color.rgb(255, 50, 50));
            showStatus.setBackgroundDrawable(gradientDrawable);
            showStatus.setTextColor(Color.WHITE);
        }
        else {
            gradientDrawable = (GradientDrawable) getResources().getDrawable(R.drawable.rounded_edges);
            gradientDrawable.setColor(Color.TRANSPARENT);
            showStatus.setBackgroundDrawable(gradientDrawable);
            showStatus.setTextColor(Color.BLACK);
        }

        TextView showQuality = (TextView) mShowView.findViewById(R.id.show_quality);
        showQuality.setText((CharSequence) rows.get(position)[2]);
        showQuality.setTextColor(Color.WHITE);
        gradientDrawable = (GradientDrawable) getResources().getDrawable(R.drawable.rounded_edges);
        if ("Any".equals(rows.get(position)[2])) {
            gradientDrawable.setColor(Color.rgb(68, 68, 68));
        }
        if ("SD".equals(rows.get(position)[2])) {
            gradientDrawable.setColor(Color.rgb(153, 68, 68));
        }
        if ("HD".equals(rows.get(position)[2])) {
            gradientDrawable.setColor(Color.rgb(68, 153, 68));
        }
        showQuality.setBackgroundDrawable(gradientDrawable);

        TextView showNextEpisode = (TextView) mShowView.findViewById(R.id.show_next_episode);
        showNextEpisode.setText((CharSequence) rows.get(position)[3]);

        TextView showNetwork = (TextView) mShowView.findViewById(R.id.show_network);
        showNetwork.setText((CharSequence) rows.get(position)[4]);

        TextView showLanguage = (TextView) mShowView.findViewById(R.id.show_language);
        showLanguage.setText((CharSequence) rows.get(position)[6]);

        if (mAsyncImage != null && mAsyncImage.getStatus() == (AsyncTask.Status.RUNNING)) {
            mAsyncImage.cancel(true);
        }
        mAsyncImage = new AsyncImage();
        mAsyncImage.execute(getActivity(), imageHandler, rows.get(position)[5], rows.get(position)[0], SickBeardController.MESSAGE.SHOW_GETPOSTER, 0);

        AlertDialog.Builder builder = new AlertDialog.Builder(mParent);
        builder.setPositiveButton(R.string.more, onClickListener);
        builder.setNegativeButton(R.string.close, onClickListener);
        builder.setView(mShowView);
        dialog = builder.create();
        dialog.show();
        return true;
    }

    /**
     * Handler used to notify this Fragment that an image has been downloaded and that it should be refreshed to display it.
     */
    private final Handler imageHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            Bitmap bitmap = (Bitmap) msg.obj;
            if (bitmap != null) {
                ImageView showPoster;
                if ((mParent.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE
                        && mParent.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    showPoster = (ImageView) getView().findViewById(R.id.showPoster);
                }
                else {
                    showPoster = (ImageView) mShowView.findViewById(R.id.showPoster);
                }
                showPoster.setImageBitmap(bitmap);
                showPoster.invalidate();
            }
        }
    };

    /**
     * Displays the Props dialog when the user wants to add a download
     */
    @SuppressWarnings("deprecation")
    public void addShowPrompt() {
        /**
         * If nothing is configured we display the configuration pop-up
         */
        if (!Preferences.isSet(Preferences.SICKBEARD_URL)) {
            mParent.showDialog(R.id.dialog_setup_prompt);
            return;
        }

        AlertDialog.Builder alert = new AlertDialog.Builder(mParent);

        alert.setTitle(R.string.add_show_dialog_title);
        alert.setMessage(R.string.add_show_dialog_message);

        final EditText input = new EditText(mParent);
        alert.setView(input);

        alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString();
                Toast.makeText(mParent, mParent.getText(R.string.add_show_background_search), Toast.LENGTH_LONG).show();
                SickBeardController.searchShow(getMessageHandler(), value);
            }
        });

        alert.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.show();
    }

    /**
     * Displays the propositions dialog with the resulting show names found after a user search to add a show to Sickbeard.
     * 
     * @param result
     *            The result of the search query
     */
    private void selectShowPrompt(final ArrayList<Object[]> result) {

        AlertDialog.Builder alert = new AlertDialog.Builder(mParent);

        ArrayList<String> shows = new ArrayList<String>();
        for (Object[] show : result) {
            shows.add(show[1] + "");
        }

        if (shows.size() > 0) {
            alert.setTitle(R.string.add_show_selection_dialog_title);
        }
        else {
            alert.setTitle(R.string.add_show_not_found);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mParent, android.R.layout.simple_list_item_1, shows);
        alert.setAdapter(adapter, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Object[] selected = result.get(which);
                SickBeardController.addShow(messageHandler, ((String) selected[2]));
                dialog.dismiss();
            }
        });

        alert.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.show();
    }
}
