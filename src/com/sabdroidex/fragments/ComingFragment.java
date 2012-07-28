package com.sabdroidex.fragments;

import java.util.ArrayList;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.sabdroidex.R;
import com.sabdroidex.activity.SABDroidEx;
import com.sabdroidex.adapters.ComingListRowAdapter;
import com.sabdroidex.sickbeard.SickBeardController;
import com.sabdroidex.utils.Preferences;
import com.sabdroidex.utils.SABDFragment;
import com.sabdroidex.utils.SABDroidConstants;

public class ComingFragment extends SABDFragment {

	private static ArrayList<Object[]> rows;
	private static Bitmap mEmptyPoster;
	private ListView mListView;
	private ComingListRowAdapter mComingRowAdapter;

	// Instantiating the Handler associated with the main thread.
	private final Handler messageHandler = new Handler() {

		@Override
		@SuppressWarnings("unchecked")
		public void handleMessage(Message msg) {
			Object result[];
			if (msg.what == SickBeardController.MESSAGE.FUTURE.ordinal()) {
                result = (Object[]) msg.obj;

                rows.clear();
                rows.addAll((ArrayList<Object[]>) result[1]);
				
				/**
				 * This might happens if a rotation occurs
				 */
				if (mComingRowAdapter != null) {
				    mComingRowAdapter.notifyDataSetChanged();
					((SABDroidEx) mParent).updateStatus(true);
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

	public ComingFragment(FragmentActivity fragmentActivity) {
		mParent = fragmentActivity;
		if (mEmptyPoster == null) {
			mEmptyPoster = BitmapFactory.decodeResource(mParent.getResources(),R.drawable.temp_poster);
		}
	}

	public ComingFragment(FragmentActivity sabDroidEx,
			ArrayList<Object[]> comingRows) {
		this(sabDroidEx);
		rows = comingRows;
	}

	public Handler getMessageHandler() {
		return messageHandler;
	}

	@Override
	public String getTitle() {
		return mParent.getString(R.string.tab_coming);
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

		mListView = (ListView) showView.findViewById(R.id.queueList);
		mListView.setDividerHeight(0);
		showView.removeAllViews();

		mComingRowAdapter = new ComingListRowAdapter(mParent, rows);
		mListView.setAdapter(mComingRowAdapter);

		// Tries to fetch recoverable data
		Object data[] = (Object[]) mParent.getLastCustomNonConfigurationInstance();
		if (data != null && extracted(data, 3) != null) {
			rows = extracted(data, 3);
		}

		if (getRows().size() > 0) {
		    mComingRowAdapter.notifyDataSetChanged();
		} 
		else {
			manualRefreshComing();
		}

		return mListView;
	}

	@Override
	protected void clearAdapter() {
	    if (mComingRowAdapter != null)
            mComingRowAdapter.clearBitmaps();
	}
	
	@Override
	public void onFragmentActivated() {
		manualRefreshComing();
	}

	/**
	 * 
	 * @return
	 */
    public ArrayList<Object[]> getRows() {
        return rows;
    }
}
