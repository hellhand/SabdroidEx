package com.sabdroidex.fragments;

import java.util.ArrayList;

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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.sabdroidex.R;
import com.sabdroidex.activity.SABDroidEx;
import com.sabdroidex.adapters.ComingListRowAdapter;
import com.sabdroidex.controllers.sickbeard.SickBeardController;
import com.sabdroidex.utils.Preferences;
import com.sabdroidex.utils.SABDroidConstants;

public class ComingFragment extends SABFragment {

    private static final String TAG = ComingFragment.class.getCanonicalName();
    
	private static ArrayList<Object[]> rows;
	private ComingListRowAdapter mComingRowAdapter;

	// Instantiating the Handler associated with the main thread.
	private final Handler messageHandler = new Handler() {

		@Override
		@SuppressWarnings("unchecked")
		public void handleMessage(Message msg) {
			Object result[];
			if (msg.what == SickBeardController.MESSAGE.FUTURE.ordinal()) {
			    try {
                    result = (Object[]) msg.obj;
    
                    rows.clear();
                    rows.addAll((ArrayList<Object[]>) result[1]);
    				
    				/**
    				 * This might happens if a rotation occurs
    				 */
    				if (mComingRowAdapter != null) {
    				    mComingRowAdapter.notifyDataSetChanged();
    					((SABDroidEx) mParent).updateState(true);
    				}
			    }
			    catch (Exception e) {
			        Log.e(TAG, e.getLocalizedMessage());
			    }
			}
            if (msg.what == SickBeardController.MESSAGE.UPDATE.ordinal()) {
                if (msg.obj instanceof String && !"".equals((String)msg.obj)) {
                    Toast.makeText(mParent, (String) msg.obj, Toast.LENGTH_LONG).show();
                }
            }
		}
	};

	private FragmentActivity mParent;

    public ComingFragment() {

    }
	
	public ComingFragment(ArrayList<Object[]> comingRows) {
		rows = comingRows;
	}

    @Override
    public Object getDataCache() {
        return rows;
    }

	@Override
	public int getTitle() {
		return R.string.tab_coming;
	}
	
	/**
	 * Refreshing the queue during startup or on user request. Asks to configure
	 * if still not done
	 */
	public void manualRefreshComing() {
		// First run setup
		if (!Preferences.isEnabled(Preferences.SICKBEARD)) {
			return;
		}
		SickBeardController.refreshFuture(messageHandler);
	}

	@Override
	public void onAttach(Activity activity) {
		mParent = (FragmentActivity) activity;
		super.onAttach(activity);
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		SharedPreferences preferences = mParent.getSharedPreferences(
				SABDroidConstants.PREFERENCES_KEY, 0);
		Preferences.update(preferences);

		LinearLayout showView = (LinearLayout) inflater.inflate(R.layout.simplelist,
				null);

		ListView mListView = (ListView) showView.findViewById(R.id.queueList);
		mListView.setDividerHeight(0);
		showView.removeAllViews();

		mComingRowAdapter = new ComingListRowAdapter(mParent, rows);
		mListView.setAdapter(mComingRowAdapter);

		// Tries to fetch recoverable data
		Object data[] = (Object[]) mParent.getLastCustomNonConfigurationInstance();
		if (data != null && extracted(data, 3) != null) {
			rows = extracted(data, 3);
		}

		if (rows.size() > 0) {
		    mComingRowAdapter.notifyDataSetChanged();
		} 
		else {
			manualRefreshComing();
		}

		return mListView;
	}

	@Override
	protected void clearAdapter() {

	}
	
	@Override
	public void onFragmentActivated() {
		manualRefreshComing();
	}
}
