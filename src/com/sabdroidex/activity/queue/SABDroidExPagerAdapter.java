package com.sabdroidex.activity.queue;

import java.util.Vector;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.viewpagerindicator.TitleProvider;

public class SABDroidExPagerAdapter extends FragmentPagerAdapter implements TitleProvider {

    private Vector<Fragment> fragments = new Vector<Fragment>();
    private String[] titles = new String[] { "Queue", "History" };

    public SABDroidExPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public void addFragment(Fragment fragment) {
        fragments.add(fragment);
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public String getTitle(int position) {
        return titles[position].toUpperCase();
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }
}
