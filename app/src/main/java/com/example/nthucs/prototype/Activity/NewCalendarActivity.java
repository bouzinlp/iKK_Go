package com.example.nthucs.prototype.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.CalendarView;
import android.widget.TextView;

import com.example.nthucs.prototype.R;
import com.facebook.FacebookSdk;
import com.facebook.login.widget.ProfilePictureView;

import java.util.Date;

/**
 * Created by Kelvin on 2017/6/27.
 */

public class NewCalendarActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{
    private Activity activity = NewCalendarActivity.this;
    private int activityIndex = 7;
    private static final int NEW_CALENDAR_ACTIVITY = 7;
    private static final int SCAN_FOOD = 2;
    private static final int TAKE_PHOTO = 3;
    private static final String FROM_CAMERA = "scan_food";
    private static final String FROM_GALLERY = "take_photo";
    private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_calendar_new);
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

        final CalendarView calendarView = (CalendarView) findViewById(R.id.calendar1);
        textView = (TextView) findViewById(R.id.date);

        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy/M/dd");
        String today = sdf.format(new Date(calendarView.getDate()));
        textView.setText(today);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                String date = year + "/" + (month+1) + "/" + dayOfMonth;
                textView.setText(date);
                Intent intent = new Intent(NewCalendarActivity.this, MainActivity.class);
                intent.putExtra("date", date);
                intent.putExtra("year", year);
                intent.putExtra("month", month+1);
                intent.putExtra("day", dayOfMonth);
                startActivity(intent);
            }
        });

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.home) {
            Intent intent_home = new Intent();
            intent_home.setClass(NewCalendarActivity.this, HomeActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("BACK", 1);
            intent_home.putExtras(bundle);
            startActivity(intent_home);
            finish();
        }
        else if (id == R.id.food_list) {
            // Handle the camera action
            Intent intent_main = new Intent();
            intent_main.setClass(NewCalendarActivity.this, MainActivity.class);
            startActivity(intent_main);
            finish();
            //Toast.makeText(this, "Open food list", Toast.LENGTH_SHORT).show();
        }
//        else if (id == R.id.calendar) {
//            Intent intent_calendar = new Intent();
//            intent_calendar.setClass(NewCalendarActivity.this, CalendarActivity.class);
//            startActivity(intent_calendar);
//            finish();
            //Toast.makeText(this, "Open calendar", Toast.LENGTH_SHORT).show();
        //}
        else if (id == R.id.Import) {
            selectImage();
            //Toast.makeText(this, "Import food", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.message) {
            Intent intent_message = new Intent();
            intent_message.setClass(NewCalendarActivity.this, MessageActivity.class);
            startActivity(intent_message);
            finish();
            //Toast.makeText(this, "Send message", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.setting_list) {
            Intent intent_setting = new Intent();
            intent_setting.setClass(NewCalendarActivity.this, SettingsActivity.class);
            startActivity(intent_setting);
            finish();
        } else if (id == R.id.blood_pressure){
            Intent intent_blood_pressure = new Intent();
            intent_blood_pressure.setClass(NewCalendarActivity.this, MyBloodPressure.class);
            startActivity(intent_blood_pressure);
            finish();
        } else if (id == R.id.mail){
            Intent intent_mail = new Intent();
            intent_mail.setClass(NewCalendarActivity.this, MailActivity.class);
            startActivity(intent_mail);
            finish();
        } else if (id == R.id.new_calendar){
            Intent intent_new_calendar = new Intent();
            intent_new_calendar.setClass(NewCalendarActivity.this, NewCalendarActivity.class);
            startActivity(intent_new_calendar);
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
                    if (activityIndex == NEW_CALENDAR_ACTIVITY) {
                        Intent intent_camera = new Intent("com.example.nthucs.prototype.TAKE_PICT");

                        activity.startActivityForResult(intent_camera, SCAN_FOOD);
                    } else {
                        // back to home activity
                        Intent result = new Intent();
                        Bundle bundle = new Bundle();
                        bundle.putInt("BACK", 1);
                        result.putExtra(FROM_CAMERA, SCAN_FOOD);
                        result.putExtras(bundle);
                        result.setClass(activity, NewCalendarActivity.class);
                        activity.startActivity(result);
                        activity.finish();
                    }
                } else if (items[index].equals("Choose from Gallery")) {
                    if (activityIndex == NEW_CALENDAR_ACTIVITY) {
                        Intent intent_gallery = new Intent("com.example.nthucs.prototype.TAKE_PHOTO");
                        //intent_gallery.putParcelableArrayListExtra(calDATA, foodCalList);
                        activity.startActivityForResult(intent_gallery, TAKE_PHOTO);
                    } else {
                        // back to home activity
                        Intent result = new Intent();
                        Bundle bundle = new Bundle();
                        bundle.putInt("BACK", 1);
                        result.putExtra(FROM_GALLERY, TAKE_PHOTO);
                        result.putExtras(bundle);
                        result.setClass(activity, NewCalendarActivity.class);
                        activity.startActivity(result);
                        activity.finish();
                    }
                } else if (items[index].equals("Cancel")) {
                    dialog.dismiss();
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putInt("BACK", 1);
                    intent.putExtras(bundle);
                    intent.setClass(NewCalendarActivity.this, NewCalendarActivity.class);
                    startActivity(intent);
                }
            }
        });
        builder.show();
    }
}
