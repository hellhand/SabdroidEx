package com.sabdroidex.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.sabdroidex.R;
import com.sabdroidex.adapters.QueueListRowAdapter;
import com.sabdroidex.controllers.sabnzbd.SABnzbdController;
import com.sabdroidex.data.sabnzbd.Queue;
import com.sabdroidex.fragments.dialogs.QueueElementActionDialog;
import com.sabdroidex.interfaces.UpdateableActivity;
import com.sabdroidex.utils.Preferences;
import com.sabdroidex.utils.SABDroidConstants;
import com.sabdroidex.utils.SABHandler;

public class QueueFragment extends SABFragment {
    
    private static final String TAG = "QueueFragment";
    
    private boolean paused = false;
    private Queue mQueue;
    private Thread updater;
    private ListView mQueueList;
    
    // Instantiating the Handler associated with the main thread.
    private final SABHandler messageHandler = new SABHandler() {
        
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == SABnzbdController.MESSAGE.QUEUE.ordinal()) {
                try {
                    mQueue = (Queue) msg.obj;
                    
                    /**
                     * This might happens if a rotation occurs
                     */
                    if (mQueueList != null || getAdapter(mQueueList) != null) {
                        ArrayAdapter<Object> adapter = getAdapter(mQueueList);
                        adapter.clear();
                        adapter.addAll(mQueue.getQueueElements());
                        adapter.notifyDataSetChanged();
                    }
                    
                    ((UpdateableActivity) getParentActivity()).updateLabels(mQueue);
                    ((UpdateableActivity) getParentActivity()).updateState(true);
                }
                catch (Exception e) {
                    Log.e(TAG, e.getLocalizedMessage());
                }
            }
            
            if (msg.what == SABnzbdController.MESSAGE.UPDATE.ordinal()) {
                try {
                    ((UpdateableActivity) getParentActivity()).updateState(false);
                    if (msg.obj instanceof String && !"".equals((String) msg.obj)) {
                        Toast.makeText(getParentActivity(), (String) msg.obj, Toast.LENGTH_LONG).show();
                    }
                }
                catch (Exception e) {
                    Log.e(TAG, e.getLocalizedMessage());
                }
            }
        }
    };
    
    /**
     * 
     */
    public QueueFragment() {
        
    }
    
    public QueueFragment(Queue downloadRows) {
        mQueue = downloadRows;
    }
    
    @Override
    public int getTitle() {
        return R.string.tab_queue;
    }
    
    /**
     * Refreshing the queue during startup or on user request. Asks to configure
     * if still not done
     */
    @SuppressWarnings("deprecation")
    public void manualRefreshQueue() {
        if (!Preferences.isSet(Preferences.SABNZBD_URL)) {
            this.getActivity().showDialog(R.id.dialog_setup_prompt);
            return;
        }
        
        SABnzbdController.refreshQueue(messageHandler);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        SharedPreferences preferences = getActivity().getSharedPreferences(SABDroidConstants.PREFERENCES_KEY, 0);
        Preferences.update(preferences);
        
        LinearLayout downloadView = (LinearLayout) inflater.inflate(R.layout.list, null);
        
        mQueueList = (ListView) downloadView.findViewById(R.id.elementList);
        downloadView.removeAllViews();
        
        mQueueList.setAdapter(new QueueListRowAdapter(getActivity(), mQueue.getQueueElements()));
        mQueueList.setOnItemLongClickListener(new ListItemLongClickListener());
        
        return mQueueList;
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
    public Object getDataCache() {
        return mQueue;
    }
    
    /**
     * Fires up a new Thread to update the queue every X minutes
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
                    catch (Exception e) {
                        Log.w(TAG, e.getLocalizedMessage());
                    }
                    if (!paused)
                        SABnzbdController.refreshQueue(messageHandler);
                }
            }
        };
        updater.start();
    }
    
    private class ListItemLongClickListener implements OnItemLongClickListener {
        
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
            QueueElementActionDialog queueElementActionDialog = new QueueElementActionDialog(messageHandler, mQueue
                    .getQueueElements().get(position));
            queueElementActionDialog.show(getChildFragmentManager(), "queueaction");
            return true;
        }
    }
    
    public void pauseResumeQueue() {
        SABnzbdController.pauseResumeQueue(messageHandler);
    }
}
