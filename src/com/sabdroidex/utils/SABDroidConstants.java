package com.sabdroidex.utils;

import android.os.Build;


public class SABDroidConstants {
    
    public static final String PREFERENCES_KEY = "SABDroid";
    
    public static boolean hasHoneycombMR1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1;
    }
}
