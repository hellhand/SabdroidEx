package com.sabdroidex.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sabdroidex.R;
import com.sabdroidex.adapters.QueueListRowAdapter;
import com.sabdroidex.controllers.sabnzbd.SABnzbdController;
import com.sabdroidex.data.Queue;
import com.sabdroidex.data.QueueElement;
import com.sabdroidex.interfaces.UpdateableActivity;
import com.sabdroidex.utils.Preferences;
import com.sabdroidex.utils.SABDroidConstants;
import com.sabdroidex.utils.SABHandler;
import com.utils.FileUtil;

public class QueueFragment extends SABFragment {
    
    private static final String TAG = "QueueFragment";
    
    private boolean paused = false;
    private static Queue mQueue;
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
    
    public QueueFragment(Activity activity) {
        //mActivity = new WeakReference<Activity>(activity);
    }
    
    public QueueFragment(Activity activity, Queue downloadRows) {
        this(activity);
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
    public void onFragmentActivated() {
        
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        startAutomaticUpdater();
        messageHandler.setActivity(getActivity());
        super.onCreate(savedInstanceState);
    }
    
    @Override
    public void onStart() {
        super.onStart();
        
        final Intent intent = getActivity().getIntent();
        if (intent != null) {
            final Uri data = intent.getData();
            if (data != null) {
                String path = "";
                if (data.getScheme().equalsIgnoreCase("content")) {
                    Cursor cursor = getActivity().getContentResolver().query(data, null, null, null, null);
                    cursor.moveToFirst();
                    int idx = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA);
                    path = cursor.getString(idx);
                }
                else {
                    path = data.getPath();
                }
                intent.setData(null);
                Log.v(TAG, "Data received : " + data.toString());
                openFilePopUp(path);
            }
        }
        return;
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
    
    /**
     * This method creates the pop-up that is displayed when a Nzb file is
     * opened with SABDroidEx
     */
    public void openFilePopUp(final String path) {
        OnClickListener clickListener = new DialogInterface.OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                if (whichButton == Dialog.BUTTON_POSITIVE) {
                    SABnzbdController.addFile(messageHandler, FileUtil.getFileName(path), FileUtil.getFileAsCharArray(path));
                }
            }
        };
        
        String message = getResources().getString(R.string.send_validation);
        message += FileUtil.getFileName(path);
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
        builder.setTitle(R.string.send_file);
        builder.setPositiveButton(android.R.string.ok, clickListener);
        builder.setNegativeButton(android.R.string.cancel, clickListener);
        builder.setMessage(message);
        
        AlertDialog dialog = null;
        dialog = builder.create();
        dialog.show();
        
        TextView messageView = (TextView) dialog.findViewById(android.R.id.message);
        messageView.setGravity(Gravity.LEFT);
    }
    
    @Override
    protected void clearAdapter() {
        
    }
    
    public Handler getMessageHandler() {
        return messageHandler;
    }
    
    private class ListItemLongClickListener implements OnItemLongClickListener {
        
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
            
            AlertDialog dialog = null;
            OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                
                @Override
                public void onClick(DialogInterface dialog, int whichButton) {
                    dialog.dismiss();
                }
            };
            
            final QueueElement element = mQueue.getQueueElements().get(position);
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setNegativeButton(android.R.string.cancel, onClickListener);
            builder.setTitle(element.getFilename());
            
            String[] options = new String[2];
            if ("Paused".equals(element.getStatus())) {
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
                            SABnzbdController.pauseResumeItem(messageHandler, element);
                            break;
                        case 1:
                            SABnzbdController.removeQueueItem(messageHandler, element);
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
    }
}
