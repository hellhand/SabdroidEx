package com.sabdroidex;

import android.support.v4.app.Fragment;

public abstract class SABDFragment extends Fragment {

    public abstract String getTitle();

    public abstract void onFragmentActivated();
}
