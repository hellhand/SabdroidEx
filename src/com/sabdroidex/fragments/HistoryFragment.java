package com.sabdroidex.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.sabdroidex.R;
import com.sabdroidex.adapters.HistoryAdapter;
import com.sabdroidex.controllers.SABController;
import com.sabdroidex.controllers.sabnzbd.SABnzbdController;
import com.sabdroidex.data.JSONBased;
import com.sabdroidex.data.sabnzbd.History;
import com.sabdroidex.fragments.dialogs.sabnzbd.HistoryRemoveDialog;
import com.sabdroidex.interfaces.UpdateableActivity;
import com.sabdroidex.utils.Preferences;
import com.sabdroidex.utils.SABHandler;

public class HistoryFragment extends SABFragment {

    private static final String TAG = HistoryFragment.class.getCanonicalName();

    private static History history;
    private HistoryAdapter historyAdapter;

    // Instantiating the Handler associated with the main thread.
    private final SABHandler messageHandler = new SABHandler() {

        @Override
        public void handleMessage(Message msg) {

            if (msg.what == SABnzbdController.MESSAGE.HISTORY.hashCode()) {
                try {
                    history = (History) msg.obj;

                    /**
                     * This might happens if a rotation occurs
                     */
                    if (historyAdapter != null || history != null) {
                        historyAdapter.clear();
                        historyAdapter.addAll(history.getHistoryElements());
                    }

                    ((UpdateableActivity) getParentActivity()).updateLabels(history);
                    ((UpdateableActivity) getParentActivity()).updateState(true);
                }
                catch (Exception e) {
                    Log.e(TAG, e.getLocalizedMessage() == null ? e.toString() : e.getLocalizedMessage());
                }
            }

            if (msg.what == SABController.MESSAGE.UPDATE.hashCode()) {
                try {
                    ((UpdateableActivity) getParentActivity()).updateState(false);
                    if (msg.obj instanceof String && !"".equals(msg.obj)) {
                        Toast.makeText(getParentActivity(), (String) msg.obj, Toast.LENGTH_LONG).show();
                    }
                }
                catch (Exception e) {
                    Log.e(TAG, e.getLocalizedMessage() == null ? e.toString() : e.getLocalizedMessage());
                }

            }
        }
    };

    /**
     * 
     */
    public HistoryFragment() {}

    /**
     *
     * @param historyRows
     */
    public HistoryFragment(History historyRows) {
        history = historyRows;
    }

    @Override
    public int getTitle() {
        return R.string.tab_history;
    }

    /**
     * Refreshing the queue during startup or on user request. Asks to configure
     * if still not done
     */
    public void manualRefreshHistory() {
        // First run setup
        if (!Preferences.isSet(Preferences.SABNZBD_URL)) {
            // The error dialog should be displayed by the QueueFragment
            return;
        }

        SABnzbdController.refreshHistory(messageHandler);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onStart() {
        super.onStart();

        messageHandler.setActivity(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        historyAdapter = new HistoryAdapter(getActivity(), history.getHistoryElements());

        LinearLayout historyView = (LinearLayout) inflater.inflate(R.layout.list, null);
        ListView historyList = (ListView) historyView.findViewById(R.id.elementList);

        historyView.removeAllViews();
        historyList.setAdapter(new HistoryAdapter(getActivity(), history.getHistoryElements()));
        historyList.setOnItemClickListener(new ListItemClickListener());

        manualRefreshHistory();

        return historyList;
    }

    @Override
    public JSONBased getDataCache() {
        return history;
    }

    /**
     * This class handles long click on an item in the history list. It displays
     * a pop-up allowing the user to remove the element from the history list.
     * 
     * @author Marc
     * 
     */
    private class ListItemClickListener implements OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            HistoryRemoveDialog historyRemoveDialog = new HistoryRemoveDialog();
            HistoryRemoveDialog.setMessageHandler(messageHandler);
            HistoryRemoveDialog.setHistoryElement(history.getHistoryElements().get(position));
            historyRemoveDialog.show(getActivity().getSupportFragmentManager(), "historyRemove");
        }
    }
}
