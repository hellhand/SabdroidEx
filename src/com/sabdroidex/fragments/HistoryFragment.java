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
import android.widget.Toast;

import com.sabdroidex.R;
import com.sabdroidex.activity.SABDroidEx;
import com.sabdroidex.adapters.HistoryListRowAdapter;
import com.sabdroidex.controllers.sabnzbd.SABnzbdController;
import com.sabdroidex.interfaces.UpdateableActivity;
import com.sabdroidex.utils.Preferences;
import com.sabdroidex.utils.SABDFragment;
import com.sabdroidex.utils.SABDroidConstants;

public class HistoryFragment extends SABDFragment implements OnItemLongClickListener {
    
    private static JSONObject backupJsonObject;
    
    private static ArrayList<Object[]> rows;
    private ListView mHistoryList;
    
    // Instantiating the Handler associated with the main thread.
    private final Handler messageHandler = new Handler() {
        
        @Override
        @SuppressWarnings("unchecked")
        public void handleMessage(Message msg) {
            if (msg.what == SABnzbdController.MESSAGE.HISTORY.ordinal()) {
                
                Object result[] = (Object[]) msg.obj;
                // Updating rows
                rows.clear();
                rows.addAll((ArrayList<Object[]>) result[1]);
                
                /**
                 * This might happens if a rotation occurs
                 */
                if (mHistoryList != null || getAdapter(mHistoryList) != null) {
                    ArrayAdapter<Object[]> adapter = getAdapter(mHistoryList);
                    adapter.notifyDataSetChanged();
                }
                
                // Updating the header
                JSONObject jsonObject = (JSONObject) result[0];
                backupJsonObject = jsonObject;
                
                try {
                    // ((UpdateableActivity) mParent).updateLabels(jsonObject);
                    ((UpdateableActivity) mParent).updateState(true);
                }
                catch (Exception e) {
                    Log.w("ERROR", " " + e.getLocalizedMessage());
                }
            }
            
            if (msg.what == SABnzbdController.MESSAGE.UPDATE.ordinal()) {
                try {
                    ((SABDroidEx) mParent).updateState(false);
                    if (msg.obj instanceof String && !"".equals((String) msg.obj)) {
                        Toast.makeText(mParent, (String) msg.obj, Toast.LENGTH_LONG).show();
                    }
                }
                catch (Exception e) {
                    Log.w("ERROR", " " + e.getLocalizedMessage());
                }
            }
        }
    };
    
    private FragmentActivity mParent;
    
    /**
     * 
     */
    public HistoryFragment() {}
    
    /**
     * 
     * @param fragmentActivity
     */
    public HistoryFragment(FragmentActivity fragmentActivity) {
        mParent = fragmentActivity;
    }
    
    /**
     * 
     * @param sabDroidEx
     * @param historyRows
     */
    public HistoryFragment(FragmentActivity sabDroidEx, ArrayList<Object[]> historyRows) {
        this(sabDroidEx);
        rows = historyRows;
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
     * Refreshing the queue during startup or on user request. Asks to configure
     * if still not done
     */
    @SuppressWarnings("deprecation")
    public void manualRefreshHistory() {
        // First run setup
        if (!Preferences.isSet(Preferences.SABNZBD_URL)) {
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
        
        LinearLayout historyView = (LinearLayout) inflater.inflate(R.layout.list, null);
        
        mHistoryList = (ListView) historyView.findViewById(R.id.elementList);
        historyView.removeAllViews();
        
        mHistoryList.setAdapter(new HistoryListRowAdapter(mParent, rows));
        mHistoryList.setOnItemLongClickListener(this);
        
        // Tries to fetch recoverable data
        Object data[] = (Object[]) mParent.getLastCustomNonConfigurationInstance();
        if (data != null && extracted(data, 1) != null) {
            rows = extracted(data, 1);
            backupJsonObject = (JSONObject) data[4];
            // ((SABDroidEx) mParent).updateLabels(backupJsonObject);
        }
        
        if (rows.size() > 0) {
            ArrayAdapter<Object[]> adapter = getAdapter(mHistoryList);
            adapter.notifyDataSetChanged();
        }
        else {
            manualRefreshHistory();
        }
        
        return mHistoryList;
    }
    
    @Override
    public void onFragmentActivated() {
        manualRefreshHistory();
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
        builder.setTitle((String) rows.get(position)[0]);
        String[] options = new String[1];
        options[0] = getActivity().getResources().getString(R.string.menu_delete);
        
        builder.setItems(options, new OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        SABnzbdController.removeHistoryItem(messageHandler, rows.get(position));
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
    protected void clearAdapter() {
        
    }
    
    @Override
    public Object getDataCache() {
        return rows;
    }
}
