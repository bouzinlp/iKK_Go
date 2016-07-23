package com.example.nthucs.prototype.Activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.example.nthucs.prototype.R;
import com.example.nthucs.prototype.Setting.SettingAdapter;
import com.example.nthucs.prototype.TabsBar.TabsController;
import com.example.nthucs.prototype.TabsBar.ViewPagerAdapter;

import java.util.List;

/**
 * Created by user on 2016/7/16.
 */
public class SettingsActivity extends AppCompatActivity {

    // list view adapter for setting list
    private SettingAdapter settingApapter;

    // list view for including textView
    private ListView setting_list;

    // my profile entry view
    private TextView myProfile;

    // string list for every setting item's title
    private List<String> setting_title;

    // element for the bottom of the tab content
    private ViewPager viewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // initialize tabLayout and viewPager
        viewPager = (ViewPager)findViewById(R.id.viewPager);
        tabLayout = (TabLayout)findViewById(R.id.tabLayout);
        initializeTabLayout();

        // call function to active tabs listener
        TabsController tabsController = new TabsController(4, SettingsActivity.this, tabLayout, viewPager);
        tabsController.processTabLayout();

        // initialize setting list
        setting_list = (ListView)findViewById(R.id.setting_list);

        settingApapter = new SettingAdapter(this, R.layout.single_setting, setting_title);
        setting_list.setAdapter(settingApapter);

        // initialize my profile view
        myProfile = (TextView)findViewById(R.id.myProfile);

        selectTab(4);
    }

    public void clickSettingItem(View view) {
        int itemId = view.getId();

        switch (itemId) {
            case R.id.myProfile:
                break;
        }
    }

    // Initialize tab layout
    private void initializeTabLayout() {
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
    }

    // select specific tab
    private void selectTab(int index) {
        TabLayout.Tab tab = tabLayout.getTabAt(index);
        tab.select();
    }
}
