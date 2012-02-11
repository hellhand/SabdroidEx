package com.sabdroidex.fragments;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.sabdroidex.R;
import com.sabdroidex.activity.SABDroidEx;
import com.sabdroidex.activity.adapters.SickBeardShowsListRowAdapter;
import com.sabdroidex.sickbeard.SickBeardController;
import com.sabdroidex.utils.Preferences;
import com.sabdroidex.utils.SABDFragment;
import com.sabdroidex.utils.SABDroidConstants;

public class SickbeardShowsFragment extends SABDFragment implements OnItemLongClickListener {

    private static ArrayList<Object[]> rows;
    private ListView listView;

    // Instantiating the Handler associated with the main thread.
    private Handler messageHandler = new Handler() {

        @SuppressWarnings("unchecked")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SickBeardController.MESSAGE_UPDATE:

                    Object result[] = (Object[]) msg.obj;
                    // Updating rows
                    rows.clear();
                    rows.addAll((ArrayList<Object[]>) result[1]);

                    /**
                     * This might happens if a rotation occurs
                     */
                    if (listView != null || getAdapter(listView) != null) {
                        ArrayAdapter<Object[]> adapter = getAdapter(listView);
                        adapter.notifyDataSetChanged();
                    }

                    break;

                default:
                    break;
            }
        }
    };

    private FragmentActivity mParent;

    public SickbeardShowsFragment() {
    }

    public SickbeardShowsFragment(FragmentActivity fragmentActivity) {
        mParent = fragmentActivity;
    }

    public SickbeardShowsFragment(SABDroidEx sabDroidEx, ArrayList<Object[]> historyRows) {
        this(sabDroidEx);
        rows = historyRows;
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

        LinearLayout downloadView = (LinearLayout) inflater.inflate(R.layout.list, null);

        listView = (ListView) downloadView.findViewById(R.id.queueList);
        downloadView.removeAllViews();

        listView.setAdapter(new SickBeardShowsListRowAdapter(mParent, rows));
        listView.setOnItemLongClickListener(this);

        // Tries to fetch recoverable data
        Object data[] = (Object[]) mParent.getLastCustomNonConfigurationInstance();
        if (data != null && extracted(data, 2) != null) {
            rows = extracted(data, 2);
        }

        if (rows.size() > 0) {
            ArrayAdapter<Object[]> adapter = getAdapter(listView);
            adapter.notifyDataSetChanged();
        }
        else {
            manualRefreshShows();
        }

        return listView;
    }

    @Override
    public void onFragmentActivated() {
        manualRefreshShows();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

        AlertDialog dialog = null;
        OnClickListener noListener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        };
        LayoutInflater inflater = LayoutInflater.from(mParent);
        LinearLayout showView = (LinearLayout) inflater.inflate(R.layout.show_status, null);
        ImageView showPoster = (ImageView) showView.findViewById(R.id.showPoster);
        TextView showName = (TextView) showView.findViewById(R.id.show_name);

        showPoster.setImageResource(R.drawable.temp_poster);
        showName.setText((CharSequence) rows.get(position)[0]);

        AlertDialog.Builder builder = new AlertDialog.Builder(mParent);
        builder.setNegativeButton(android.R.string.cancel, noListener);
        builder.setView(showView);
        dialog = builder.create();
        dialog.show();
        return true;
    }
}
