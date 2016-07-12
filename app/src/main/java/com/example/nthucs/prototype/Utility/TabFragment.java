package com.example.nthucs.prototype.Utility;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.nthucs.prototype.Activity.CalendarActivity;
import com.example.nthucs.prototype.Activity.MainActivity;
import com.example.nthucs.prototype.R;

/**
 * Created by user on 2016/7/12.
 */
public class TabFragment extends Fragment {

    private static final String ARG_POSITION = "position";

    private static final int SCAN_FOOD = 2;
    private static final int TAKE_PHOTO = 3;

    private int position;

    public static TabFragment newInstance(int position) {
        TabFragment f = new TabFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        position = getArguments().getInt(ARG_POSITION);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        AbsListView.LayoutParams params = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, DrawerLayout.LayoutParams.MATCH_PARENT);

        FrameLayout fl = new FrameLayout(getActivity());
        fl.setLayoutParams(params);

        TextView v = new TextView(getActivity());
        v.setLayoutParams(params);
        v.setLayoutParams(params);
        v.setGravity(Gravity.CENTER);
        v.setBackgroundResource(R.color.white);
        v.setText("Tabs " + (position + 1));

        System.out.println(position);

        if (position == 0) {

        } else if (position == 1) {

        } else if (position == 2) {
            //Intent intent = new Intent("com.example.nthucs.prototype.TAKE_PHOTO");
            //startActivityForResult(intent, TAKE_PHOTO);
        } else if (position == 3) {
            //Intent intent2 = new Intent("com.example.nthucs.prototype.TAKE_PICT");
            //startActivityForResult(intent2, SCAN_FOOD);
        }

        fl.addView(v);
        return fl;
    }
}