package com.example.nthucs.prototype.TabsBar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import com.example.nthucs.prototype.Activity.CalendarActivity;
import com.example.nthucs.prototype.Activity.MainActivity;
import com.example.nthucs.prototype.Activity.MessageActivity;
import com.example.nthucs.prototype.Activity.SettingsActivity;
import com.example.nthucs.prototype.FoodList.FoodCal;
import com.example.nthucs.prototype.R;

import java.util.ArrayList;

/**
 * Created by user on 2016/7/18.
 */

public class TabsController {
    private int activityIndex;
    private Activity activity;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    // food cal list, only from main activity
    private ArrayList<FoodCal> foodCalList;

    // action number
    private static final int SCAN_FOOD = 2;
    private static final int TAKE_PHOTO = 3;

    // activity string
    private static final String FROM_CAMERA = "scan_food";
    private static final String FROM_GALLERY = "take_photo";

    // activity number
    private static final int MAIN_ACTIVITY = 0;
    private static final int CALENDAR_ACTIVITY = 1;
    private static final int MESSAGE_ACTIVITY = 3;
    private static final int SETTING_ACTIVITY = 4;

    // csv reader called
    //private static final String csvReader = "alreadyCall";

    // cal list data
    private static final String calDATA = "foodCalList";

    public TabsController(int activityIndex, Activity activity, TabLayout tabLayout, ViewPager viewPager) {
        this.activityIndex = activityIndex;
        this.activity = activity;
        this.tabLayout = tabLayout;
        this.viewPager = viewPager;
    }

    public TabsController(int activityIndex, Activity activity, TabLayout tabLayout, ViewPager viewPager, ArrayList<FoodCal> foodCalList) {
        this.activityIndex = activityIndex;
        this.activity = activity;
        this.tabLayout = tabLayout;
        this.viewPager = viewPager;
        this.foodCalList = foodCalList;
    }

    public void processTabLayout() {
        // enable tab selected listener
        tabLayout.setOnTabSelectedListener(
                new TabLayout.ViewPagerOnTabSelectedListener(viewPager) {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        super.onTabSelected(tab);
                        if (tab.getPosition() == 0) {
                            // main activity
                            if (activityIndex != MAIN_ACTIVITY) {
                                Intent result = new Intent();
                                result.setClass(activity, MainActivity.class);
                                activity.startActivity(result);
                                activity.finish();
                                toggleAnimation(tab.getPosition(), activityIndex);
                            }
                        } else if (tab.getPosition() == 1) {
                            // calendar activity
                            if (activityIndex != CALENDAR_ACTIVITY) {
                                Intent intent_calendar = new Intent("com.example.nthucs.prototype.CALENDAR");
                                intent_calendar.setClass(activity, CalendarActivity.class);
                                activity.startActivity(intent_calendar);
                                activity.finish();
                                toggleAnimation(tab.getPosition(), activityIndex);
                            }
                        } else if (tab.getPosition() == 2) {
                            // start activity for result
                            selectImage();
                        } else if (tab.getPosition() == 3) {
                            // message activity
                            if (activityIndex != MESSAGE_ACTIVITY) {
                                Intent intent_mes = new Intent();
                                intent_mes.setClass(activity, MessageActivity.class);
                                activity.startActivity(intent_mes);
                                activity.finish();
                                toggleAnimation(tab.getPosition(), activityIndex);
                            }
                        } else if (tab.getPosition() == 4) {
                            // setting activity
                            if (activityIndex != SETTING_ACTIVITY) {
                                Intent intent_settings = new Intent("com.example.nthucs.prototype.SETTINGS");
                                intent_settings.setClass(activity, SettingsActivity.class);
                                activity.startActivity(intent_settings);
                                activity.finish();
                                toggleAnimation(tab.getPosition(), activityIndex);
                            }
                        }
                        //System.out.println("main select: "+tab.getPosition());
                    }
                }
        );
    }

    // select image with two way
    private void selectImage() {
        final CharSequence[] items = { "Take with Camera", "Choose from Gallery", "Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Select Image");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int index) {
                if (items[index].equals("Take with Camera")) {
                    if (activityIndex == MAIN_ACTIVITY) {
                        Intent intent_camera = new Intent("com.example.nthucs.prototype.TAKE_PICT");

                        activity.startActivityForResult(intent_camera, SCAN_FOOD);
                    } else {
                        // back to main activity
                        Intent result = new Intent();
                        result.putExtra(FROM_CAMERA, SCAN_FOOD);
                        result.setClass(activity, MainActivity.class);
                        activity.startActivity(result);
                        activity.finish();
                    }
                } else if (items[index].equals("Choose from Gallery")) {
                    if (activityIndex == MAIN_ACTIVITY) {
                        Intent intent_gallery = new Intent("com.example.nthucs.prototype.TAKE_PHOTO");
                        //intent_gallery.putParcelableArrayListExtra(calDATA, foodCalList);
                        activity.startActivityForResult(intent_gallery, TAKE_PHOTO);
                    } else {
                        // back to main activity
                        Intent result = new Intent();
                        result.putExtra(FROM_GALLERY, TAKE_PHOTO);
                        result.setClass(activity, MainActivity.class);
                        activity.startActivity(result);
                        activity.finish();
                    }
                } else if (items[index].equals("Cancel")) {
                    dialog.dismiss();
                    selectTab(activityIndex);
                }
            }
        });
        builder.show();
    }

    // select specific tab
    private void selectTab(int index) {
        TabLayout.Tab tab = tabLayout.getTabAt(index);
        tab.select();
    }

    // activity toggle animation
    private void toggleAnimation(int position, int activityIndex) {
        if (position < activityIndex) {
            // origin activity from right to left
            this.activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        } else if (position > activityIndex) {
            // origin activity from left to right
            this.activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }
}
