package com.example.nthucs.prototype.Utility;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;

import com.example.nthucs.prototype.R;

/**
 * Created by user on 2016/7/12.
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {

    private final String[] tabTitles = { "Tab1", "Tab2", "Tab3", "Tab4"};
    private Context context;

    public ViewPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    public View getTabView(int position) {
        View v = null;
        if (position == 0) {
            v = LayoutInflater.from(context).inflate(R.layout.foodlist_tab, null);
        } else if (position == 1) {
            v = LayoutInflater.from(context).inflate(R.layout.calendar_tab, null);
        } else if (position == 2) {
            v = LayoutInflater.from(context).inflate(R.layout.library_tab, null);
        } else if (position == 3) {
            v = LayoutInflater.from(context).inflate(R.layout.camera_tab, null);
        }
        return v;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

    @Override
    public int getCount() {
        return tabTitles.length;
    }

    @Override
    public Fragment getItem(int position) {
        return TabFragment.newInstance(position);
    }
}