package com.sabdroidex.fragments;

import android.support.v4.app.Fragment;

import com.sabdroidex.data.JSONBased;

public abstract class SABFragment extends Fragment {

    public abstract int getTitle();
    
    public abstract JSONBased getDataCache();
}
