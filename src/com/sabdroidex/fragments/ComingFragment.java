package com.sabdroidex.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.sabdroidex.R;
import com.sabdroidex.adapters.ComingAdapter;
import com.sabdroidex.controllers.sickbeard.SickBeardController;
import com.sabdroidex.data.JSONBased;
import com.sabdroidex.data.sickbeard.FuturePeriod;
import com.sabdroidex.utils.Preferences;
import com.sabdroidex.utils.SABHandler;

public class ComingFragment extends SABFragment {
    
    private static final String TAG = ComingFragment.class.getCanonicalName();
    
    private static FuturePeriod mFuturePeriod;
    private ComingAdapter mComingRowAdapter;
    
    /**
     * Instantiating the Handler associated with this {@link Fragment}.
     */
    private final SABHandler messageHandler = new SABHandler() {
        
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == SickBeardController.MESSAGE.FUTURE.hashCode()) {
                try {
                    mFuturePeriod = (FuturePeriod) msg.obj;
                    
                    if (mComingRowAdapter != null && mFuturePeriod != null) {
                        mComingRowAdapter.setDataSet(mFuturePeriod);
                        mComingRowAdapter.notifyDataSetChanged();
                    }
                }
                catch (Exception e) {
                    Log.e(TAG, e.getLocalizedMessage());
                }
            }
            if (msg.what == SickBeardController.MESSAGE.UPDATE.hashCode()) {
                if (msg.obj instanceof String && !"".equals(msg.obj)) {
                    Toast.makeText(getParentActivity(), (String) msg.obj, Toast.LENGTH_LONG).show();
                }
            }
        }
    };
    
    /**
     * 
     */
    public ComingFragment() {}
    
    public ComingFragment(FuturePeriod futurePeriod) {
        mFuturePeriod = futurePeriod;
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
        if (!Preferences.isEnabled(Preferences.SICKBEARD)) {
            return;
        }
        SickBeardController.refreshFuture(messageHandler);
    }
    
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        messageHandler.setActivity(getActivity());
        super.onCreate(savedInstanceState);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        
        LinearLayout comingView = (LinearLayout) inflater.inflate(R.layout.pinned_header_list, null);
        ListView mListView = (ListView) comingView.findViewById(R.id.simpleList);
        
        mComingRowAdapter = new ComingAdapter(getActivity().getApplicationContext(), mFuturePeriod);
        mListView.setAdapter(mComingRowAdapter);
        comingView.removeAllViews();
        
        manualRefreshComing();
        
        return mListView;
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
    
    @Override
    public JSONBased getDataCache() {
        return mFuturePeriod;
    }
}
