package com.sabdroidex.adapters;

import java.util.Vector;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.sabdroidex.utils.SABDFragment;
import com.viewpagerindicator.TitleProvider;

public class SABDroidExPagerAdapter extends FragmentPagerAdapter implements TitleProvider {

    private Vector<SABDFragment> fragments = new Vector<SABDFragment>();

    public SABDroidExPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public void addFragment(Fragment fragment) {
        fragments.add((SABDFragment) fragment);
    }

    public void removeFragment(Fragment fragment) {
        fragments.remove((SABDFragment) fragment);
    }

    public boolean contains(Fragment fragment) {
        return fragments.contains(fragment);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public String getTitle(int position) {
        return fragments.get(position).getTitle();
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }
}
