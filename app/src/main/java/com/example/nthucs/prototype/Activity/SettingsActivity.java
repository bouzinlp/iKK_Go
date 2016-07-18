package com.example.nthucs.prototype.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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

                        if (tab.getPosition() == 0) {
                            // main activity
                            Intent result = new Intent();
                            result.setClass(SettingsActivity.this, MainActivity.class);
                            startActivity(result);
                        } else if (tab.getPosition() == 1) {
                            // calendar activity
                            Intent intent_calendar = new Intent("com.example.nthucs.prototype.CALENDAR");
                            intent_calendar.setClass(SettingsActivity.this, CalendarActivity.class);
                            startActivity(intent_calendar);
                        } else if (tab.getPosition() == 2) {
                            selectImage();
                        } else if (tab.getPosition() == 3) {
                            // message activity
                            Intent intent_mes = new Intent();
                            intent_mes.setClass(getApplicationContext(),MessageActivity.class);
                            startActivity(intent_mes);
                        } else if (tab.getPosition() == 4) {
                            // settings itself
                        }
                        //System.out.println("setting select: "+tab.getPosition());
                    }
                }
        );
    }

    // select specific tab
    private void selectTab(int index) {
        TabLayout.Tab tab = tabLayout.getTabAt(index);
        tab.select();
    }

    // select image with two way
    private void selectImage() {
        final CharSequence[] items = { "Take with Camera", "Choose from Gallery", "Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
        builder.setTitle("Select Image");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int index) {
                if (items[index].equals("Take with Camera")) {
                    Intent intent_camera = new Intent("com.example.nthucs.prototype.TAKE_PICT");
                    startActivityForResult(intent_camera, SCAN_FOOD);
                } else if (items[index].equals("Choose from Gallery")) {
                    Intent intent_gallery = new Intent("com.example.nthucs.prototype.TAKE_PHOTO");
                    startActivityForResult(intent_gallery, TAKE_PHOTO);
                } else if (items[index].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }
}
