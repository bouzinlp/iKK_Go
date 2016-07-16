package com.example.nthucs.prototype.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.example.nthucs.prototype.R;
import com.example.nthucs.prototype.TabsBar.ViewPagerAdapter;

/**
 * Created by user on 2016/7/16.
 */
public class SettingsActivity extends AppCompatActivity {
    // element for the bottom of the tab content
    private ViewPager viewPager;
    private TabLayout tabLayout;

    // action number for every activity
    private static final int SCAN_FOOD = 2;
    private static final int TAKE_PHOTO = 3;
    private static final int CALENDAR = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        viewPager = (ViewPager)findViewById(R.id.viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);

        processTabLayout();

        selectTab(4);
    }

    // Initialize tab layout and listener
    private void processTabLayout() {
        ViewPagerAdapter pagerAdapter =
                new ViewPagerAdapter(getSupportFragmentManager(), this);

        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        // set custom icon for every tab
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {
                tab.setCustomView(pagerAdapter.getTabView(i));
            }
        }

        // enable tab selected listener
        tabLayout.setOnTabSelectedListener(
                new TabLayout.ViewPagerOnTabSelectedListener(viewPager) {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        super.onTabSelected(tab);

                        // temporary added for return food list
                        if (tab.getPosition() == 0) {
                            Intent result = getIntent();

                            setResult(Activity.RESULT_OK, result);
                            finish();
                        } else if (tab.getPosition() == 1) {
                            Intent intent_gallery = new Intent("com.example.nthucs.prototype.TAKE_PHOTO");
                            startActivityForResult(intent_gallery, TAKE_PHOTO);
                        } else if (tab.getPosition() == 2) {
                            Intent intent_camera = new Intent("com.example.nthucs.prototype.TAKE_PICT");
                            startActivityForResult(intent_camera, SCAN_FOOD);
                        } else if (tab.getPosition() == 3) {
                            Intent intent_calendar = new Intent("com.example.nthucs.prototype.CALENDAR");
                            startActivityForResult(intent_calendar, CALENDAR);
                        } else if (tab.getPosition() == 4) {
                        }
                        //System.out.println(tab.getPosition());
                    }
                }
        );
    }

    // select specific tab
    private void selectTab(int index) {
        TabLayout.Tab tab = tabLayout.getTabAt(index);
        tab.select();
    }
}
