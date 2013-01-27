package com.sabdroidex.utils;

import java.util.ArrayList;

import android.content.res.Configuration;
import android.support.v4.app.Fragment;

public abstract class SABDFragment extends Fragment {

    public abstract String getTitle();

    public abstract void onFragmentActivated();
    
    public abstract Object getDataCache();
    
    /**
     * This function will serve as a retriever to get back the wanted data from the serialized object
     * 
     * @param data
     *            The previously Serialized cache Object[]
     * @param osition
     *            The position of the object in the array to recover
     * @return The object in the array to recover
     */
    @SuppressWarnings("unchecked")
    public static ArrayList<Object[]> extracted(Object[] data, int position) {
        return data == null ? null : (ArrayList<Object[]>) data[position];
    }
    
    @Override
    protected void finalize() throws Throwable {
        clearAdapter();
        super.finalize();
    }

    @Override
    public void onDestroyView() {
        clearAdapter();
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        clearAdapter();
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        clearAdapter();
        super.onDetach();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        clearAdapter();
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onPause() {
        clearAdapter();
        super.onPause();
    }
    
    abstract protected void clearAdapter();
}
