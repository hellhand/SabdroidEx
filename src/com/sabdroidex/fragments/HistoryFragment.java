package com.sabdroidex.fragments;

import java.util.ArrayList;

import org.json.JSONObject;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.sabdroidex.R;
import com.sabdroidex.activity.SABDroidEx;
import com.sabdroidex.adapters.HistoryListRowAdapter;
import com.sabdroidex.sabnzbd.SABnzbdController;
import com.sabdroidex.utils.Preferences;
import com.sabdroidex.utils.SABDFragment;
import com.sabdroidex.utils.SABDroidConstants;

public class HistoryFragment extends SABDFragment {

    private static JSONObject backupJsonObject;

    private static ArrayList<Object[]> rows;
    private ListView listView;

    // Instantiating the Handler associated with the main thread.
    private Handler messageHandler = new Handler() {

        @SuppressWarnings("unchecked")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SABnzbdController.MESSAGE_UPDATE_HISTORY:

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

                    // Updating the header
                    JSONObject jsonObject = (JSONObject) result[0];
                    backupJsonObject = jsonObject;

                    try {
                        ((SABDroidEx) mParent).updateLabels(jsonObject);
                        ((SABDroidEx) mParent).updateStatus("");
                    }
                    catch (Exception e) {
                        Log.w("ERROR", " " + e.getLocalizedMessage());
                    }
                    break;

                case SABnzbdController.MESSAGE_STATUS_UPDATE:
                    try {
                        ((SABDroidEx) mParent).updateStatus(msg.obj.toString());
                    }
                    catch (Exception e) {
                        Log.w("ERROR", " " + e.getLocalizedMessage());
                    }
                    break;

                default:
                    break;
            }
        }
    };

    private FragmentActivity mParent;

    public HistoryFragment() {
    }

    public HistoryFragment(FragmentActivity fragmentActivity) {
        mParent = fragmentActivity;
    }

    public HistoryFragment(SABDroidEx sabDroidEx, ArrayList<Object[]> historyRows) {
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
        return mParent.getString(R.string.tab_history);
    }

    /**
     * Refreshing the queue during startup or on user request. Asks to configure if still not done
     */
    public void manualRefreshHistory() {
        // First run setup
        if (!Preferences.isSet("server_url")) {
            mParent.showDialog(R.id.dialog_setup_prompt);
            return;
        }

        SABnzbdController.refreshHistory(messageHandler);
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

        listView.setAdapter(new HistoryListRowAdapter(mParent, rows));

        // Tries to fetch recoverable data
        Object data[] = (Object[]) mParent.getLastCustomNonConfigurationInstance();
        if (data != null && extracted(data, 1) != null) {
            rows = extracted(data, 1);
            backupJsonObject = (JSONObject) data[3];
            ((SABDroidEx) mParent).updateLabels(backupJsonObject);
        }

        if (rows.size() > 0) {
            ArrayAdapter<Object[]> adapter = getAdapter(listView);
            adapter.notifyDataSetChanged();
        }
        else {
            manualRefreshHistory();
        }

        return listView;
    }

    @Override
    public void onFragmentActivated() {
        manualRefreshHistory();
    }
}
