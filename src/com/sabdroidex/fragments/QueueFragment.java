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
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
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
import com.sabdroidex.utils.SABDFragment;
import com.sabdroidex.utils.SABDroidConstants;
import com.utils.FileUtil;

public class QueueFragment extends SABDFragment implements OnItemLongClickListener {

    private static final String TAG = "QueueFragment";

    private boolean paused = false;
    private static Queue queue;
    private Thread updater;
    private ListView mQueueList;
    private FragmentActivity mParent;
    
    // Instantiating the Handler associated with the main thread.
    private final Handler messageHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == SABnzbdController.MESSAGE.QUEUE.ordinal()) {

                queue = (Queue) msg.obj;

                /**
                 * This might happens if a rotation occurs
                 */
                if (mQueueList != null || getAdapter(mQueueList) != null) {
                    ArrayAdapter<Object> adapter = getAdapter(mQueueList);
                    adapter.clear();
                    adapter.addAll(queue.getQueueElements());
                    adapter.notifyDataSetChanged();
                }

                ((UpdateableActivity) mParent).updateLabels(queue);
                ((UpdateableActivity) mParent).updateState(true);
            }

            if (msg.what == SABnzbdController.MESSAGE.UPDATE.ordinal()) {
                ((UpdateableActivity) mParent).updateState(false);
                if (msg.obj instanceof String && !"".equals((String) msg.obj)) {
                    Toast.makeText(mParent, (String) msg.obj, Toast.LENGTH_LONG).show();
                }
            }
        }
    };

    /**
     * 
     */
    public QueueFragment() {

    }

    /**
     * 
     * @param fragmentActivity
     */
    public QueueFragment(FragmentActivity fragmentActivity) {
        mParent = fragmentActivity;
    }

    /**
     * 
     * @param sabDroidEx
     * @param downloadRows
     */
    public QueueFragment(FragmentActivity fragmentActivity, Queue downloadRows) {
        this(fragmentActivity);
        queue = downloadRows;
    }

    @SuppressWarnings("unchecked")
    private ArrayAdapter<Object> getAdapter(ListView listView) {
        return listView == null ? null : (ArrayAdapter<Object>) listView.getAdapter();
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
    @SuppressWarnings("deprecation")
    public void manualRefreshQueue() {
        // First run setup
        if (!Preferences.isSet(Preferences.SABNZBD_URL)) {
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

        mQueueList = (ListView) downloadView.findViewById(R.id.elementList);
        downloadView.removeAllViews();

        mQueueList.setAdapter(new QueueListRowAdapter(mParent, queue.getQueueElements()));
        mQueueList.setOnItemLongClickListener(this);

        // Tries to fetch recoverable data
//        Object data[] = (Object[]) mParent.getLastCustomNonConfigurationInstance();
//        if (data != null && extracted(data, 0) != null) {
//            rows = extracted(data, 0);
//            backupJsonObject = (JSONObject) data[4];
//            ((SABDroidEx) mParent).updateLabels(backupJsonObject);
//        }
//
//        if (rows.size() > 0) {
//            ArrayAdapter<Object[]> adapter = getAdapter(mQueueList);
//            adapter.notifyDataSetChanged();
//        }
//        else {
            manualRefreshQueue();
//        }

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
        
        final QueueElement element = queue.getQueueElements().get(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(mParent);
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
        return queue;
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
     * Displays the Props dialog when the user wants to add a download
     */
    @SuppressWarnings("deprecation")
    public void addDownloadPrompt() {
        /**
         * If nothing is configured we display the configuration pop-up
         */
        if (!Preferences.isSet(Preferences.SABNZBD_URL)) {
            mParent.showDialog(R.id.dialog_setup_prompt);
            return;
        }

        AlertDialog.Builder alert = new AlertDialog.Builder(mParent);

        alert.setTitle(R.string.add_nzb_dialog_title);
        alert.setMessage(R.string.add_nzb_dialog_message);

        final EditText input = new EditText(mParent);
        alert.setView(input);

        alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString();
                SABnzbdController.addByURL(getMessageHandler(), value);
                dialog.dismiss();
            }
        });

        alert.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });

        alert.show();
    }
    
    @Override
    public void onStart() {
        super.onStart();
        
        final Intent intent = getActivity().getIntent();
        if (intent != null)
        {
           final Uri data = intent.getData();
           if (data != null)
           {
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
    
    /**
     * This method creates the pop-up that is displayed when a Nzb file is opened with SABDroidEx
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
        
        AlertDialog.Builder builder = new AlertDialog.Builder(mParent);
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
}
