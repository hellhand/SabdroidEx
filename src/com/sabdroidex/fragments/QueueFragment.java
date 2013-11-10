package com.sabdroidex.fragments;

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
import com.sabdroidex.adapters.QueueAdapter;
import com.sabdroidex.controllers.SABController;
import com.sabdroidex.controllers.sabnzbd.SABnzbdController;
import com.sabdroidex.data.JSONBased;
import com.sabdroidex.data.sabnzbd.Queue;
import com.sabdroidex.fragments.dialogs.sabnzbd.QueueElementActionDialog;
import com.sabdroidex.interfaces.DialogFragmentManagerHolder;
import com.sabdroidex.interfaces.UpdateableActivity;
import com.sabdroidex.utils.Preferences;
import com.sabdroidex.utils.SABHandler;

public class QueueFragment extends SABFragment {
    
    private static final String TAG = "QueueFragment";
    
    private boolean paused = false;
    private static Queue queue;
    private QueueAdapter queueAdapter;
    
    // Instantiating the Handler associated with the main thread.
    private final SABHandler messageHandler = new SABHandler() {
        
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == SABnzbdController.MESSAGE.QUEUE.hashCode()) {
                try {
                    queue = (Queue) msg.obj;
                    updateQueueList();
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
    public QueueFragment() {}
    
    /**
     * 
     * @param downloadRows
     */
    public QueueFragment(Queue downloadRows) {
        queue = downloadRows;
    }
    
    @Override
    public int getTitle() {
        return R.string.tab_queue;
    }
    
    /**
     * Refreshing the queue during startup or on user request. Asks to configure
     * if still not done
     */
    public void manualRefreshQueue() {
        if (!Preferences.isSet(Preferences.SABNZBD_URL)) {
            ((DialogFragmentManagerHolder)getActivity()).getDialogFragmentManager().showSetupDialog();
            return;
        }
        
        SABnzbdController.refreshQueue(messageHandler);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        queueAdapter = new QueueAdapter(getActivity(), queue.getQueueElements());
        
        LinearLayout downloadView = (LinearLayout) inflater.inflate(R.layout.list, null);
        ListView queueList = (ListView) downloadView.findViewById(R.id.elementList);

        downloadView.removeAllViews();        
        queueList.setAdapter(queueAdapter);
        queueList.setOnItemClickListener(new ListItemLongClickListener());
        
        return queueList;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        startAutomaticUpdater();
        messageHandler.setActivity(getActivity());
        super.onCreate(savedInstanceState);
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
    
    @Override
    public JSONBased getDataCache() {
        return queue;
    }
    
    /**
     * Fires up a new Thread to update the queue every X minutes
     */
    private void startAutomaticUpdater() {
        Thread updater = new Thread() {
            
            @Override
            public void run() {
                for (; !isInterrupted();) {
                    try {
                        int rate = Integer.valueOf(Preferences.get("refresh_rate", "5000"));
                        Thread.sleep(rate);
                    }
                    catch (Exception e) {
                        Log.e(TAG, "Could not sleep !!");
                    }
                    if (!paused)
                        SABnzbdController.refreshQueue(messageHandler);
                }
            }
        };
        updater.start();
    }

    /**
     * Updates the queue {@link android.widget.ListView}
     */
    private void updateQueueList() {
        /**
         * This might happens if a rotation occurs
         */
        if (queueAdapter != null || queue != null) {
            queueAdapter.setItems(queue.getQueueElements());
            queueAdapter.notifyDataSetChanged();
        }

        ((UpdateableActivity) getActivity()).updateLabels(queue);
        ((UpdateableActivity) getActivity()).updateState(true);
    }


    private class ListItemLongClickListener implements OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            try {
                QueueElementActionDialog queueElementActionDialog = new QueueElementActionDialog(messageHandler, queue.getQueueElements().get(position));
                queueElementActionDialog.show(getActivity().getSupportFragmentManager(), "queueaction");
            }
            catch (Exception e) {
                Log.e(TAG, e.getLocalizedMessage());
            }
        }
    }
    
    public void pauseResumeQueue() {
        SABnzbdController.pauseResumeQueue(messageHandler);
    }
}
