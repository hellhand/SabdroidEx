package com.sabdroidex.fragments;

import android.app.Fragment;
import android.os.Message;

import com.sabdroidex.adapters.ComingAdapter;
import com.sabdroidex.controllers.sickbeard.SickBeardController;
import com.sabdroidex.data.JSONBased;
import com.sabdroidex.data.sickbeard.FuturePeriod;
import com.sabdroidex.utils.SABHandler;


public class WantedFragment extends SABFragment {

    private static final String TAG = WantedFragment.class.getCanonicalName();
    
    private static FuturePeriod mFuturePeriod;
    private ComingAdapter mComingRowAdapter;
    
    /**
     * Instantiating the Handler associated with this {@link Fragment}.
     */
    private final SABHandler messageHandler = new SABHandler() {
        
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == SickBeardController.MESSAGE.FUTURE.hashCode()) {

            }
            if (msg.what == SickBeardController.MESSAGE.UPDATE.hashCode()) {

            }
        }
    };
    
    @Override
    public int getTitle() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public JSONBased getDataCache() {
        // TODO Auto-generated method stub
        return null;
    }

}
