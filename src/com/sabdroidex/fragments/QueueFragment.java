package com.sabdroidex.fragments;

import java.util.ArrayList;

import org.json.JSONObject;

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
import android.widget.LinearLayout;
import android.widget.ListView;

import com.sabdroidex.R;
import com.sabdroidex.activity.SABDroidEx;
import com.sabdroidex.activity.adapters.QueueListRowAdapter;
import com.sabdroidex.sabnzbd.SABnzbdController;
import com.sabdroidex.utils.Preferences;
import com.sabdroidex.utils.SABDFragment;
import com.sabdroidex.utils.SABDroidConstants;

/**
 * Main SABDroid Activity
 */
public class QueueFragment extends SABDFragment implements OnItemLongClickListener {

    private static JSONObject backupJsonObject;

    private static ArrayList<String> rows = new ArrayList<String>();
    final static int DIALOG_SETUP_PROMPT = 999;
    private ListView listView;
    // Instantiating the Handler associated with the main thread.
    private Handler messageHandler = new Handler() {

        @SuppressWarnings("unchecked")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SABnzbdController.MESSAGE_UPDATE_QUEUE:

                    Object result[] = (Object[]) msg.obj;
                    // Updating rows
                    rows.clear();
                    rows.addAll((ArrayList<String>) result[1]);

                    ArrayAdapter<String> adapter = extracted(listView);
                    adapter.notifyDataSetChanged();

                    // Updating the header
                    JSONObject jsonObject = (JSONObject) result[0];
                    backupJsonObject = jsonObject;

                    ((SABDroidEx) mParent).updateLabels(jsonObject);
                    ((SABDroidEx) mParent).updateStatus("");
                    break;

                case SABnzbdController.MESSAGE_STATUS_UPDATE:
                    ((SABDroidEx) mParent).updateStatus(msg.obj.toString());
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

    public Handler getMessageHandler() {
        return messageHandler;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        SharedPreferences preferences = mParent.getSharedPreferences(SABDroidConstants.PREFERENCES_KEY, 0);
        Preferences.update(preferences);

        LinearLayout downloadView = (LinearLayout) inflater.inflate(R.layout.list, null);

        listView = (ListView) downloadView.findViewById(R.id.queueList);
        downloadView.removeAllViews();

        listView.setAdapter(new QueueListRowAdapter(mParent, rows));
        listView.setOnItemLongClickListener(this);

        // Tries to fetch recoverable data
        Object data[] = (Object[]) mParent.getLastCustomNonConfigurationInstance();
        if (data != null) {
            rows = extracted(data, 0);
            backupJsonObject = (JSONObject) data[2];
            ((SABDroidEx) mParent).updateLabels(backupJsonObject);
        }

        if (rows.size() > 0) {
            ArrayAdapter<String> adapter = extracted(listView);
            adapter.notifyDataSetChanged();
        }
        else {
            manualRefreshQueue();
        }

        startAutomaticUpdater();

        return listView;
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> arg0, View arg1, final int arg2, long arg3) {

        AlertDialog dialog = null;
        OnClickListener noListener = new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(mParent);
        builder.setNegativeButton(android.R.string.cancel, noListener);
        builder.setItems(R.array.optionsdialog, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        SABnzbdController.pauseResumeItem(messageHandler, rows.get(arg2));
                        break;
                    case 1:
                        SABnzbdController.removeQueueItem(messageHandler, rows.get(arg2));
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

    @SuppressWarnings("unchecked")
    private ArrayAdapter<String> extracted(ListView listView) {
        return (ArrayAdapter<String>) listView.getAdapter();
    }

    /**
     * Fires up a new Thread to update the queue every X minutes TODO add configuration to controll the auto updates
     */
    private void startAutomaticUpdater() {
        Thread t = new Thread() {

            public void run() {
                for (;;) {
                    try {
                        int rate = Integer.valueOf(Preferences.get("refresh_rate", "5000"));
                        Thread.sleep(rate);
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (!paused)
                        SABnzbdController.refreshQueue(messageHandler);
                }
            }
        };
        t.start();
    }

    @SuppressWarnings("unchecked")
    ArrayList<String> extracted(Object[] data, int position) {
        return (ArrayList<String>) data[position];
    }

    /**
     * Refreshing the queue during startup or on user request. Asks to configure if still not done
     */
    public void manualRefreshQueue() {
        // First run setup
        if (!Preferences.isSet("server_url")) {
            mParent.showDialog(DIALOG_SETUP_PROMPT);
            return;
        }

        SABnzbdController.refreshQueue(messageHandler);
    }

    @Override
    public String getTitle() {
        return mParent.getString(R.string.tab_queue);
    }

    @Override
    public void onFragmentActivated() {
        manualRefreshQueue();
    }
}
