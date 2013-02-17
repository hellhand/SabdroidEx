package com.sabdroidex.utils;

import android.app.Activity;
import android.os.Handler;


public class SABHandler extends Handler {
    
    private Activity mActivity = null;
    
    public void setActivity(Activity activity) {
        this.mActivity = activity;
    }
    
    public Activity getParentActivity() {
        return mActivity;
    }
}
