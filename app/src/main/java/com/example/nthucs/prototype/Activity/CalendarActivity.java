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
import com.example.nthucs.prototype.FoodList.Food;
import com.example.nthucs.prototype.FoodList.FoodDAO;
import com.example.nthucs.prototype.R;
import com.example.nthucs.prototype.TabsBar.TabsController;
import com.example.nthucs.prototype.TabsBar.ViewPagerAdapter;
import com.example.nthucs.prototype.Utility.Event;

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

    // element for the bottom of the tab content
    private ViewPager viewPager;
    private TabLayout tabLayout;
    //foodlist
    private List<Food> foods ;
    private FoodDAO foodDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        // initialize tabLayout and viewPager
        viewPager = (ViewPager)findViewById(R.id.viewPager);
        tabLayout = (TabLayout)findViewById(R.id.tabLayout);
        initializeTabLayout();

        // call function to active tabs listener
        TabsController tabsController = new TabsController(1, CalendarActivity.this, tabLayout, viewPager);
        tabsController.processTabLayout();

        selectTab(1);
        foodDAO = new FoodDAO(getApplicationContext());
        foods = foodDAO.getAll();

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

        // below allows you to configure color for the current day in the month
        compactCalendarView.setCurrentDayBackgroundColor(getResources().getColor(R.color.black));
        // below allows you to configure colors for the current day the user has selected
        compactCalendarView.setCurrentSelectedDayBackgroundColor(getResources().getColor(R.color.dark_red));
        compactCalendarView.setGreenBackgroundColor(getResources().getColor(R.color.green));
        compactCalendarView.setBlueBackgroundColor(getResources().getColor(R.color.colorPrimary));

        addEvents(compactCalendarView, Calendar.JANUARY);
        addEvents(compactCalendarView, Calendar.FEBRUARY);
        addEvents(compactCalendarView, Calendar.MARCH);
        addEvents(compactCalendarView, Calendar.APRIL);
        addEvents(compactCalendarView, Calendar.MAY);
        addEvents(compactCalendarView, Calendar.JUNE);
        addEvents(compactCalendarView, Calendar.JULY);
        addEvents(compactCalendarView, Calendar.SEPTEMBER);
        addEvents(compactCalendarView, Calendar.OCTOBER);
        addEvents(compactCalendarView, Calendar.NOVEMBER);
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

        currentCalender.setTime(firstDayOfMonth);
        if (month > -1) {
            currentCalender.set(Calendar.MONTH, month);
        }

        int yearclicked =currentCalender.get(Calendar.YEAR);

        int i , j=0 ;
        String time ;
        int monthday ;
        String name;
        float calorie = 0;


        if((month==1-1)||(month==3-1)||(month==5-1)||(month==7-1)||(month==8-1)||(month==10-1)||(month==12-1)){
            monthday =31;
        } else if(month==2-1){
            if(yearclicked%4 ==0)
                monthday = 29;
            else monthday = 28;
        } else {
            monthday = 30;
        }

        for( i=0; i<monthday ; i++){

            j=0;
            currentCalender.setTime(firstDayOfMonth);

            currentCalender.set(Calendar.MONTH, month);

            currentCalender.add(Calendar.DATE, i);
            setToMidnight(currentCalender);
            long timeInMillis = currentCalender.getTimeInMillis();
            int monthclicked =currentCalender.get(Calendar.MONTH);
            System.out.println(".....month:" + monthclicked);
            int day = currentCalender.get(Calendar.DATE);
            System.out.println(".....day:" + day);

            int []getday = new int [3];
            while(j<foods.size()){
                time = foods.get(j).getLocaleDatetime();
                System.out.println("...............date time: "+time);
                String[] token =time.split("-");
                String[] a = token[2].split(" ");
                getday[0] = Integer.valueOf(token[0]);
                System.out.println(".....ok 0 ");
                getday[1] = Integer.valueOf(token[1]);
                System.out.println(".....ok 1 ");
                getday[2] = Integer.valueOf(a[0]);
                System.out.println(".....ok 2 ");

                System.out.println("...............date time: "+token[0]+"...."+token[1]+"..."+a[0]);
                System.out.println(yearclicked+"" +monthclicked +""+ day );
                if(yearclicked==Integer.valueOf(token[0])&&monthclicked+1==Integer.valueOf(token[1])&&day==Integer.valueOf(a[0])){
                    System.out.println("GGGGGGGGGGGGGGGGGGGGG" );
                    name = foods.get(j).getTitle();
                    System.out.println(name );
                    calorie = foods.get(j).getCalorie();
                    System.out.println(calorie );
                    List<Event> events = getEvents( timeInMillis , name , calorie );
                    compactCalendarView.addEvents(events);
                }
                j++;
            }

        }

    }

    private List<Event> getEvents(long timeInMillis, String name , float calorie) {

        return Arrays.asList(
                new Event(Color.argb(255, 169, 68, 65), timeInMillis, name+" "+calorie  )
        );
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            //System.out.println(data.getExtras().isEmpty());
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
