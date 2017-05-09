package com.example.nthucs.prototype.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.nthucs.prototype.Calendar.CompactCalendarView;
import com.example.nthucs.prototype.FoodList.Food;
import com.example.nthucs.prototype.FoodList.FoodDAO;
import com.example.nthucs.prototype.R;
import com.example.nthucs.prototype.TabsBar.TabsController;
import com.example.nthucs.prototype.TabsBar.ViewPagerAdapter;
import com.example.nthucs.prototype.Utility.Event;
import com.facebook.login.widget.ProfilePictureView;

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
public class CalendarActivity  extends AppCompatActivity
            implements NavigationView.OnNavigationItemSelectedListener{
    private static final String TAG = "Main_Calendar";
    private Calendar currentCalender = Calendar.getInstance(Locale.getDefault());
    private SimpleDateFormat dateFormatForMonth = new SimpleDateFormat("MMM - yyyy", Locale.getDefault());
    private boolean shouldShow = false;
    private Activity activity = CalendarActivity.this;
    private int activityIndex = 2;
    private static final int CALENDAR_ACTIVITY = 2;
    private static final int SCAN_FOOD = 2;
    private static final int TAKE_PHOTO = 3;
    private static final String FROM_CAMERA = "scan_food";
    private static final String FROM_GALLERY = "take_photo";

    // element for the bottom of the tab content
    private ViewPager viewPager;
    private TabLayout tabLayout;

    //food list
    private List<Food> foods ;
    private FoodDAO foodDAO;

    //
    private int nowfoodlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_nav);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        TextView facebookUsername = (TextView) headerView.findViewById(R.id.Facebook_name);
        facebookUsername.setText("Hello, "+LoginActivity.facebookName);
        ProfilePictureView profilePictureView = (ProfilePictureView) headerView.findViewById(R.id.Facebook_profile_picture);
        profilePictureView.setProfileId(LoginActivity.facebookUserID);

        // initialize tabLayout and viewPager
        //viewPager = (ViewPager)findViewById(R.id.viewPager);
        //tabLayout = (TabLayout)findViewById(R.id.tabLayout);
        //initializeTabLayout();

        // call function to active tabs listener
        //TabsController tabsController = new TabsController(1, CalendarActivity.this, tabLayout, viewPager);
        //tabsController.processTabLayout();

        //selectTab(1);
        foodDAO = new FoodDAO(getApplicationContext());
        foods = foodDAO.getAll();

        nowfoodlist=0;

        //final ActionBar actionBar = getSupportActionBar();
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
        //actionBar.setTitle(dateFormatForMonth.format(compactCalendarView.getFirstDayOfCurrentMonth()));
        setTitle(dateFormatForMonth.format(compactCalendarView.getFirstDayOfCurrentMonth()));

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
                //actionBar.setTitle(dateFormatForMonth.format(firstDayOfNewMonth));
                setTitle(dateFormatForMonth.format(firstDayOfNewMonth));
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

        int i , j ;
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
            j=nowfoodlist;

            currentCalender.setTime(firstDayOfMonth);

            currentCalender.set(Calendar.MONTH, month);

            currentCalender.add(Calendar.DATE, i);
            setToMidnight(currentCalender);
            long timeInMillis = currentCalender.getTimeInMillis();
            int monthclicked =currentCalender.get(Calendar.MONTH);
            //System.out.println(".....month:" + monthclicked);
            int day = currentCalender.get(Calendar.DATE);
            //System.out.println(".....day:" + day);

            int []getday = new int [3];
            if(j==0&&j<foods.size()) {
                time = foods.get(j).getLocaleDatetime();
                String[] token = time.split("-");
                String[] a = token[2].split(" ");
                getday[0] = Integer.valueOf(token[0]);
                getday[1] = Integer.valueOf(token[1]);
                getday[2] = Integer.valueOf(a[0]);

                if (yearclicked == Integer.valueOf(token[0]) && monthclicked + 1 == Integer.valueOf(token[1]) && day == Integer.valueOf(a[0])) {
                    nowfoodlist = j;
                    name = foods.get(j).getTitle();
                    calorie = foods.get(j).getCalorie();
                    List<Event> events = getEvents(timeInMillis, name, calorie);
                    compactCalendarView.addEvents(events);
                    j++;
                    while (j < foods.size()) {
                        time = foods.get(j).getLocaleDatetime();
                        token = time.split("-");
                        a = token[2].split(" ");
                        getday[0] = Integer.valueOf(token[0]);
                        getday[1] = Integer.valueOf(token[1]);
                        getday[2] = Integer.valueOf(a[0]);

                        if (yearclicked == Integer.valueOf(token[0]) && monthclicked + 1 == Integer.valueOf(token[1]) && day == Integer.valueOf(a[0])) {
                            nowfoodlist = j;
                            name = foods.get(j).getTitle();
                            calorie = foods.get(j).getCalorie();
                            events = getEvents(timeInMillis, name, calorie);
                            compactCalendarView.addEvents(events);
                            j++;
                        }else{
                            break;
                        }
                    }
                }
            }
            else{
                while (j < foods.size()) {
                    time = foods.get(j).getLocaleDatetime();
                    // System.out.println("...............date time: "+time);
                    String[] token = time.split("-");
                    String[] a = token[2].split(" ");
                    getday[0] = Integer.valueOf(token[0]);
                    // System.out.println(".....ok 0 ");
                    getday[1] = Integer.valueOf(token[1]);
                    //System.out.println(".....ok 1 ");
                    getday[2] = Integer.valueOf(a[0]);
                    //System.out.println(".....ok 2 ");

                    //System.out.println("...............date time: "+token[0]+"...."+token[1]+"..."+a[0]);
                    //System.out.println(yearclicked+"" +monthclicked +""+ day );
                    if (yearclicked == Integer.valueOf(token[0]) && monthclicked + 1 == Integer.valueOf(token[1]) && day == Integer.valueOf(a[0])) {
                        nowfoodlist = j;
                        //System.out.println("GGGGGGGGGGGGGGGGGGGGG" );
                        name = foods.get(j).getTitle();
                        //System.out.println(name );
                        calorie = foods.get(j).getCalorie();
                        //System.out.println(calorie );
                        List<Event> events = getEvents(timeInMillis, name, calorie);
                        compactCalendarView.addEvents(events);
                        j++;
                    }else{
                        break;
                    }
                }
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

    @Override
    protected void onResume() {
        super.onResume();

        // Always select tab 1
        //selectTab(1);
    }

    // select specific tab
    private void selectTab(int index) {
        TabLayout.Tab tab = tabLayout.getTabAt(index);
        tab.select();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.home) {
            Intent intent_home = new Intent();
            intent_home.setClass(CalendarActivity.this, HomeActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("BACK", 1);
            intent_home.putExtras(bundle);
            startActivity(intent_home);
            finish();
        }
        else if (id == R.id.food_list) {
            // Handle the camera action
            Intent intent_main = new Intent();
            intent_main.setClass(CalendarActivity.this, MainActivity.class);
            startActivity(intent_main);
            finish();
            //Toast.makeText(this, "Open food list", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.calendar) {
            Intent intent_calendar = new Intent();
            intent_calendar.setClass(CalendarActivity.this, CalendarActivity.class);
            startActivity(intent_calendar);
            finish();
            //Toast.makeText(this, "Open calendar", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.Import) {
            selectImage();
            //Toast.makeText(this, "Import food", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.message) {
            Intent intent_message = new Intent();
            intent_message.setClass(CalendarActivity.this, MessageActivity.class);
            startActivity(intent_message);
            finish();
            //Toast.makeText(this, "Send message", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.setting_list) {
            Intent intent_setting = new Intent();
            intent_setting.setClass(CalendarActivity.this, SettingsActivity.class);
            startActivity(intent_setting);
            finish();
        } else if (id == R.id.blood_pressure){
            Intent intent_blood_pressure = new Intent();
            intent_blood_pressure.setClass(CalendarActivity.this, MyBloodPressure.class);
            startActivity(intent_blood_pressure);
            finish();
        } else if (id == R.id.mail){
            Intent intent_mail = new Intent();
            intent_mail.setClass(CalendarActivity.this, MailActivity.class);
            startActivity(intent_mail);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void selectImage(){
        final CharSequence[] items = { "Take with Camera", "Choose from Gallery", "Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Select Image");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int index) {
                if (items[index].equals("Take with Camera")) {
                    if (activityIndex == CALENDAR_ACTIVITY) {
                        Intent intent_camera = new Intent("com.example.nthucs.prototype.TAKE_PICT");

                        activity.startActivityForResult(intent_camera, SCAN_FOOD);
                    } else {
                        // back to setting activity
                        Intent result = new Intent();
                        result.putExtra(FROM_CAMERA, SCAN_FOOD);
                        result.setClass(activity, CalendarActivity.class);
                        activity.startActivity(result);
                        activity.finish();
                    }
                } else if (items[index].equals("Choose from Gallery")) {
                    if (activityIndex == CALENDAR_ACTIVITY) {
                        Intent intent_gallery = new Intent("com.example.nthucs.prototype.TAKE_PHOTO");
                        //intent_gallery.putParcelableArrayListExtra(calDATA, foodCalList);
                        activity.startActivityForResult(intent_gallery, TAKE_PHOTO);
                    } else {
                        // back to setting activity
                        Intent result = new Intent();
                        result.putExtra(FROM_GALLERY, TAKE_PHOTO);
                        result.setClass(activity, CalendarActivity.class);
                        activity.startActivity(result);
                        activity.finish();
                    }
                } else if (items[index].equals("Cancel")) {
                    dialog.dismiss();
                    Intent intent = new Intent();
                    intent.setClass(CalendarActivity.this, CalendarActivity.class);
                    startActivity(intent);
                }
            }
        });
        builder.show();
    }
}
