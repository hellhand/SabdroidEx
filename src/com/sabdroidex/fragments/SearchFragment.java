package com.sabdroidex.fragments;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.sabdroidex.R;
import com.sabdroidex.adapters.SearchListRowAdapter;
import com.sabdroidex.utils.SABDFragment;

public class SearchFragment extends SABDFragment {

    private FragmentActivity mParent;

    private static ArrayList<String> rows = new ArrayList<String>();

    private ListView listView;

    public SearchFragment(FragmentActivity fragmentActivity) {
        mParent = fragmentActivity;
    }

    @Override
    public String getTitle() {
        return mParent.getString(R.string.tab_search);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        LinearLayout searchView = (LinearLayout) inflater.inflate(R.layout.list, null);

        listView = (ListView) searchView.findViewById(R.id.queueList);
        searchView.removeAllViews();
        listView.setAdapter(new SearchListRowAdapter(mParent, rows));

        return searchView;
    }

    @Override
    public void onFragmentActivated() {

    }

    @Override
    protected void clearAdapter() {
        // TODO Auto-generated method stub
        
    }
}
