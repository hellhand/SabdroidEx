package com.sabdroidex.fragments;

import java.util.ArrayList;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.sabdroidex.R;
import com.sabdroidex.activity.SABDroidEx;
import com.sabdroidex.adapters.QueueListRowAdapter;
import com.sabdroidex.sabnzbd.SABnzbdController;
import com.sabdroidex.utils.Preferences;
import com.sabdroidex.utils.SABDFragment;
import com.sabdroidex.utils.SABDroidConstants;

/**
 * Main SABDroid Activity
 */
public class QueueFragment extends SABDFragment implements OnItemLongClickListener {

    private static JSONObject backupJsonObject;

    private static ArrayList<Object[]> rows;
    private Thread updater;
    private ListView mQueueList;

    // Instantiating the Handler associated with the main thread.
    private Handler messageHandler = new Handler() {

        @Override
        @SuppressWarnings("unchecked")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SABnzbdController.MESSAGE_UPDATE_QUEUE:

                    Object result[] = (Object[]) msg.obj;
                    // Updating rows
                    rows.clear();
                    rows.addAll((ArrayList<Object[]>) result[1]);

                    if (mQueueList != null || getAdapter(mQueueList) != null) {
                        ArrayAdapter<Object[]> adapter = getAdapter(mQueueList);
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

    protected boolean paused = false;

    public QueueFragment(FragmentActivity fragmentActivity) {
        mParent = fragmentActivity;
    }

    public QueueFragment(SABDroidEx sabDroidEx, ArrayList<Object[]> downloadRows) {
        this(sabDroidEx);
        rows = downloadRows;
    }

    @SuppressWarnings("unchecked")
    ArrayList<Object[]> extracted(Object[] data, int position) {
        return data == null ? null : (ArrayList<Object[]>) data[position];
    }

    @SuppressWarnings("unchecked")
    private ArrayAdapter<Object[]> getAdapter(ListView listView) {
        return listView == null ? null : (ArrayAdapter<Object[]>) listView.getAdapter();
    }

    public Handler getMessageHandler() {
        return messageHandler;
    }

    @Override
    public String getTitle() {
        return mParent.getString(R.string.tab_queue);
    }

    /**
     * Refreshing the queue during startup or on user request. Asks to configure if still not done
     */
    public void manualRefreshQueue() {
        // First run setup
        if (!Preferences.isSet("server_url")) {
            mParent.showDialog(R.id.dialog_setup_prompt);
            return;
        }

        SABnzbdController.refreshQueue(messageHandler);
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

        mQueueList = (ListView) downloadView.findViewById(R.id.queueList);
        downloadView.removeAllViews();

        mQueueList.setAdapter(new QueueListRowAdapter(mParent, rows));
        mQueueList.setOnItemLongClickListener(this);

        // Tries to fetch recoverable data
        Object data[] = (Object[]) mParent.getLastCustomNonConfigurationInstance();
        if (data != null && extracted(data, 0) != null) {
            rows = extracted(data, 0);
            backupJsonObject = (JSONObject) data[3];
            ((SABDroidEx) mParent).updateLabels(backupJsonObject);
        }

        if (rows.size() > 0) {
            ArrayAdapter<Object[]> adapter = getAdapter(mQueueList);
            adapter.notifyDataSetChanged();
        }
        else {
            manualRefreshQueue();
        }

        if (updater == null || updater.isInterrupted())
            startAutomaticUpdater();

        return mQueueList;
    }

    @Override
    public void onFragmentActivated() {
        manualRefreshQueue();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

        AlertDialog dialog = null;
        OnClickListener onClickListener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(mParent);
        builder.setNegativeButton(android.R.string.cancel, onClickListener);

        String[] options = new String[2];
        if ("Paused".equals(rows.get(position)[3])) {
            options[0] = getActivity().getResources().getString(R.string.menu_resume);
        }
        else {
            options[0] = getActivity().getResources().getString(R.string.menu_pause);
        }
        options[1] = getActivity().getResources().getString(R.string.menu_delete);

        builder.setItems(options, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        SABnzbdController.pauseResumeItem(messageHandler, rows.get(position));
                        break;
                    case 1:
                        SABnzbdController.removeQueueItem(messageHandler, rows.get(position));
                        break;
                    default:
                        break;
                }
            }
        });
        dialog = builder.create();
        dialog.show();
        return true;
    }

    @Override
    public void onPause() {
        super.onPause();
        paused = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        paused = false;
    }

    /**
     * Fires up a new Thread to update the queue every X minutes TODO add configuration to controll the auto updates
     */
    private void startAutomaticUpdater() {
        updater = new Thread() {

            @Override
            public void run() {
                for (; !isInterrupted();) {
                    try {
                        int rate = Integer.valueOf(Preferences.get("refresh_rate", "5000"));
                        Thread.sleep(rate);
                    }
                    catch (InterruptedException e) {
                        Log.w("ERROR", e.getLocalizedMessage());
                    }
                    if (!paused)
                        SABnzbdController.refreshQueue(messageHandler);
                }
            }
        };
        updater.start();
    }
}
