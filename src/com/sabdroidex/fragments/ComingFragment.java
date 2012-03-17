package com.sabdroidex.fragments;

import java.util.ArrayList;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.sabdroidex.R;
import com.sabdroidex.activity.SABDroidEx;
import com.sabdroidex.adapters.ComingListRowAdapter;
import com.sabdroidex.sickbeard.SickBeardController;
import com.sabdroidex.utils.Preferences;
import com.sabdroidex.utils.SABDFragment;
import com.sabdroidex.utils.SABDroidConstants;

public class ComingFragment extends SABDFragment {

    private static ArrayList<Object[]> rows;
    private static Bitmap mEmptyPoster;
    private ListView mListView;
    private ComingListRowAdapter mComingRowAdapter;

    // Instantiating the Handler associated with the main thread.
    private final Handler messageHandler = new Handler() {

        @Override
        @SuppressWarnings("unchecked")
        public void handleMessage(Message msg) {
            Object result[];
            if (msg.what == SickBeardController.MESSAGE.FUTURE.ordinal()) {
                result = (Object[]) msg.obj;
                // Updating rows
                rows.clear();
                rows.addAll((ArrayList<Object[]>) result[1]);

                /**
                 * This might happens if a rotation occurs
                 */
                if (mListView != null || getAdapter(mListView) != null) {
                    ArrayAdapter<Object[]> adapter = getAdapter(mListView);
                    adapter.notifyDataSetChanged();
                    ((SABDroidEx) mParent).updateStatus(true);
                }
            }
        }
    };

    private FragmentActivity mParent;

    public ComingFragment() {
    }

    public ComingFragment(FragmentActivity fragmentActivity) {
        mParent = fragmentActivity;
        if (mEmptyPoster == null) {
            mEmptyPoster = BitmapFactory.decodeResource(mParent.getResources(), R.drawable.temp_poster);
        }
    }

    public ComingFragment(FragmentActivity sabDroidEx, ArrayList<Object[]> historyRows) {
        this(sabDroidEx);
        rows = historyRows;
    }

    public Handler getMessageHandler() {
        return messageHandler;
    }

    @SuppressWarnings("unchecked")
    ArrayList<Object[]> extracted(Object[] data, int position) {
        return data == null ? null : (ArrayList<Object[]>) data[position];
    }

    @SuppressWarnings("unchecked")
    private ArrayAdapter<Object[]> getAdapter(ListView listView) {
        return listView == null ? null : (ArrayAdapter<Object[]>) listView.getAdapter();
    }

    @Override
    public String getTitle() {
        return mParent.getString(R.string.tab_coming);
    }

    /**
     * Refreshing the queue during startup or on user request. Asks to configure if still not done
     */
    public void manualRefreshComing() {
        // First run setup
        if (!Preferences.isEnabled(Preferences.SICKBEARD)) {
            return;
        }
        SickBeardController.refreshFuture(messageHandler);
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

        LinearLayout showView = (LinearLayout) inflater.inflate(R.layout.list, null);

        mListView = (ListView) showView.findViewById(R.id.queueList);
        mListView.setDividerHeight(0);
        showView.removeAllViews();

        mComingRowAdapter = new ComingListRowAdapter(mParent, rows);
        mListView.setAdapter(mComingRowAdapter);

        // Tries to fetch recoverable data
        Object data[] = (Object[]) mParent.getLastCustomNonConfigurationInstance();
        if (data != null && extracted(data, 3) != null) {
            rows = extracted(data, 3);
        }

        if (rows.size() > 0) {
            ArrayAdapter<Object[]> adapter = getAdapter(mListView);
            adapter.notifyDataSetChanged();
        }
        else {
            manualRefreshComing();
        }

        return mListView;
    }

    @Override
    protected void finalize() throws Throwable {
        mComingRowAdapter.clearBitmaps();
        super.finalize();
    }

    @Override
    public void onDestroyView() {
        mComingRowAdapter.clearBitmaps();
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        mComingRowAdapter.clearBitmaps();
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        mComingRowAdapter.clearBitmaps();
        super.onDetach();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        mComingRowAdapter.clearBitmaps();
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onPause() {
        mComingRowAdapter.clearBitmaps();
        super.onPause();
    }

    @Override
    public void onFragmentActivated() {
        manualRefreshComing();
    }
}
