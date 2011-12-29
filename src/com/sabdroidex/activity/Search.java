package com.sabdroidex.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.sabdroidex.R;
import com.sabdroidex.SABDFragment;

public class Search extends SABDFragment {

    private FragmentActivity mParent;

    public Search(FragmentActivity fragmentActivity) {
        mParent = fragmentActivity;
    }

    @Override
    public String getTitle() {
        return mParent.getString(R.string.search);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        return new EditText(mParent);
    }
}
