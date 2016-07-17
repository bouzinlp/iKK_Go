package com.example.nthucs.prototype.Activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.example.nthucs.prototype.Calendar.CompactCalendarView;
import com.example.nthucs.prototype.R;
import com.example.nthucs.prototype.Utility.Event;
import com.example.nthucs.prototype.TabsBar.ViewPagerAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by admin on 2016/7/1.
 */
public class CalendarActivity  extends AppCompatActivity {
    private static final String TAG = "Main_Calendar";
    private Calendar currentCalender = Calendar.getInstance(Locale.getDefault());
    private SimpleDateFormat dateFormatForMonth = new SimpleDateFormat("MMM - yyyy", Locale.getDefault());
    private boolean shouldShow = false;

    // action number for every activity
    private static final int SCAN_FOOD = 2;
    private static final int TAKE_PHOTO = 3;
    private static final int SETTINGS = 5;

    // element for the bottom of the tab content
    private ViewPager viewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        viewPager = (ViewPager)findViewById(R.id.viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        processTabLayout();

        selectTab(3);

        final ActionBar actionBar = getSupportActionBar();
        final List<String> mutableBookings = new ArrayList<>();

        final ListView bookingsListView = (ListView) findViewById(R.id.bookings_listview);
        final Button showPreviousMonthBut = (Button) findViewById(R.id.prev_button);
        final Button showNextMonthBut = (Button) findViewById(R.id.next_button);
        final Button slideCalendarBut = (Button) findViewById(R.id.slide_calendar);
        final Button showCalendarWithAnimationBut = (Button) findViewById(R.id.show_with_animation_calendar);

        final ArrayAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mutableBookings);
        bookingsListView.setAdapter(adapter);
        final CompactCalendarView compactCalendarView = (CompactCalendarView) findViewById(R.id.compactcalendar_view);
        compactCalendarView.setLocale(Locale.CHINESE);
        compactCalendarView.setUseThreeLetterAbbreviation(true);


        // below allows you to configure color for the current day in the month
        compactCalendarView.setCurrentDayBackgroundColor(getResources().getColor(R.color.black));
        // below allows you to configure colors for the current day the user has selected
        compactCalendarView.setCurrentSelectedDayBackgroundColor(getResources().getColor(R.color.dark_red));

        //test for put color on everyday.
        compactCalendarView.setGreenBackgroundColor(getResources().getColor(R.color.green));
        compactCalendarView.setBlueBackgroundColor(getResources().getColor(R.color.colorPrimary));
        addEvents(compactCalendarView, -1);
        addEvents(compactCalendarView, Calendar.DECEMBER);
        addEvents(compactCalendarView, Calendar.AUGUST);
        compactCalendarView.invalidate();

        // below line will display Sunday as the first day of the week
        // compactCalendarView.setShouldShowMondayAsFirstDay(false);

        //set initial title
        actionBar.setTitle(dateFormatForMonth.format(compactCalendarView.getFirstDayOfCurrentMonth()));

        //set title on calendar scroll
        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                List<Event> bookingsFromMap = compactCalendarView.getEvents(dateClicked);
                Log.d(TAG, "inside onclick " + dateClicked);
                if(bookingsFromMap != null){
                    Log.d(TAG, bookingsFromMap.toString());
                    mutableBookings.clear();
                    for(Event booking : bookingsFromMap){
                        mutableBookings.add((String)booking.getData());
                    }
                    adapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                actionBar.setTitle(dateFormatForMonth.format(firstDayOfNewMonth));
            }
        });

        showPreviousMonthBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                compactCalendarView.showPreviousMonth();
            }
        });

        showNextMonthBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                compactCalendarView.showNextMonth();
            }
        });

        slideCalendarBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shouldShow) {
                    compactCalendarView.showCalendar();
                } else {
                    compactCalendarView.hideCalendar();
                }
                shouldShow = !shouldShow;
            }
        });

        showCalendarWithAnimationBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shouldShow) {
                    compactCalendarView.showCalendarWithAnimation();
                } else {
                    compactCalendarView.hideCalendarWithAnimation();
                }
                shouldShow = !shouldShow;
            }
        });

    }

    private void addEvents(CompactCalendarView compactCalendarView, int month) {
        currentCalender.setTime(new Date());
        currentCalender.set(Calendar.DAY_OF_MONTH, 1);
        Date firstDayOfMonth = currentCalender.getTime();
        for (int i = 0; i < 6; i++) {
            currentCalender.setTime(firstDayOfMonth);
            if (month > -1) {
                currentCalender.set(Calendar.MONTH, month);
            }
            currentCalender.add(Calendar.DATE, i);
            setToMidnight(currentCalender);
            long timeInMillis = currentCalender.getTimeInMillis();

            List<Event> events = getEvents(timeInMillis, i);

            compactCalendarView.addEvents(events);
        }
    }

    private List<Event> getEvents(long timeInMillis, int day) {
        if (day < 2) {
            return Arrays.asList(new Event(Color.argb(255, 169, 68, 65), timeInMillis, "Event at " + new Date(timeInMillis)));
        } else if ( day > 2 && day <= 4) {
            return Arrays.asList(
                    new Event(Color.argb(255, 169, 68, 65), timeInMillis, "Event at " + new Date(timeInMillis)),
                    new Event(Color.argb(255, 100, 68, 65), timeInMillis, "Event 2 at " + new Date(timeInMillis)));
        } else {
            return Arrays.asList(
                    new Event(Color.argb(255, 169, 68, 65), timeInMillis, "Event at " + new Date(timeInMillis) ),
                    new Event(Color.argb(255, 100, 68, 65), timeInMillis, "Event 2 at " + new Date(timeInMillis)),
                    new Event(Color.argb(255, 70, 68, 65), timeInMillis, "Event 3 at " + new Date(timeInMillis)));
        }
    }

    private void setToMidnight(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }
    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
                        } else if (tab.getPosition() == 4) {
                            Intent intent_settings = new Intent("com.example.nthucs.prototype.SETTINGS");
                            startActivityForResult(intent_settings, SETTINGS);
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
