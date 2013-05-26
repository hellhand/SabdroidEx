package com.sabdroidex.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import com.sabdroidex.fragments.SABFragment;
import com.viewpagerindicator.TitleProvider;

import java.util.Vector;

public class SABDroidExPagerAdapter extends FragmentPagerAdapter implements TitleProvider {

    private Vector<SABFragment> mFragments = new Vector<SABFragment>();
    private Context mContext = null;
    
    public SABDroidExPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    public void addFragment(Fragment fragment) {
        mFragments.add((SABFragment) fragment);
    }

    public boolean contains(Fragment fragment) {
        return mFragments.contains(fragment);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public String getTitle(int position) {
        return mContext.getResources().getText(mFragments.get(position).getTitle()).toString();
    }
}
