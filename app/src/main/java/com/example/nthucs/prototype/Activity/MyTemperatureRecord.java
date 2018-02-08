package com.example.nthucs.prototype.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.nthucs.prototype.R;
import com.example.nthucs.prototype.Settings.Health;
import com.example.nthucs.prototype.Settings.HealthDAO;
import com.facebook.login.widget.ProfilePictureView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MyTemperatureRecord extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{
    // Back, Update button
    private Button backButton,  selectDayButton ,selectTimerButton , updateButton;

    // Calendar
    private Calendar calendar;

    // Temporary storage before update
    private int select_hour , select_min;
    private int select_day , select_year , select_month;

    // Edit text
    private EditText my_Temperature_Record_text;

    // data base for profile
    private HealthDAO healthDAO;

    // list of profile
    private List<Health> healthList = new ArrayList<>();

    // currently and temporary profile
    private Health curHealth, tempHealth;

    private Activity activity = MyTemperatureRecord.this;
    private int activityIndex = 8;
    private static final int MYTEMPERATURE_ACTIVITY = 8;
    private static final int SCAN_FOOD = 2;
    private static final int TAKE_PHOTO = 3;
    private static final String FROM_CAMERA = "scan_food";
    private static final String FROM_GALLERY = "take_photo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_temperature_nav);
        setTitle("體溫紀錄");
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
        // initialize data base
        healthDAO = new HealthDAO(getApplicationContext());

        // get all health data from data base
        healthList = healthDAO.getAll();

        // get the last health data in the list
        if (healthDAO.isTableEmpty() == true) {
            curHealth = new Health();
        } else {
            int cnt = 0;
            for (int i = 0 ; i < healthList.size() ; i++) {
                if (healthList.get(i).getTemperature() != 0) {
                    curHealth = healthList.get(i);
                    cnt = 1;
                }
            }
            if (cnt == 0) curHealth = new Health();
        }

        // set new health profile for updated
        tempHealth = curHealth;

        // custom view in action bar
        //getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        //getSupportActionBar().setCustomView(R.layout.my_temperature_record_menu);

        // process back button
        //processBackControllers();

        // process day button
        processSelectDayControllers();

        // process hour & min time controller
        processSelectTimerControllers();

        // process update button
        processUpdateButtonControllers();

        // process height and weight edit text
        processEditTextControllers();
    }
    @Override
    protected void onResume() {
        super.onResume();
    }

    // process back button listener
    private void processBackControllers() {
        // initialize back button
        backButton = (Button) findViewById(R.id.back_button);

        // avoid all upper case
        backButton.setTransformationMethod(null);

        // set button listener
        backButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent back = new Intent();
                back.setClass(MyTemperatureRecord.this, SettingsActivity.class);
                startActivity(back);
                finish();

                // origin activity slide to right, new activity slide from left
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
    }
    private void processSelectDayControllers() {
        // initialize birth date button
        selectDayButton = (Button)findViewById(R.id.select_day_button);

        // avoid all upper case
        selectDayButton.setTransformationMethod(null);

        // initialize calendar
        calendar = Calendar.getInstance();

        // set date picker listener
        final DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                select_month = monthOfYear + 1;
                select_day = dayOfMonth;
                select_year = year;

                // set text for button
                selectDayButton.setText(select_month+" "+select_day+", "+select_year);
            }
        };

        // set button listener
        selectDayButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (select_day == 0 && select_year == 0 && select_month == 0) {
                    new DatePickerDialog(MyTemperatureRecord.this, dateSetListener, calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
                } else {
                    new DatePickerDialog(MyTemperatureRecord.this, dateSetListener, select_year, (select_month - 1), select_day).show();
                }
            }
        });

        // set text with birthday to the button if not empty
        if (curHealth.getDatetime() != 0) {
            // set time in millis to calendar
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(curHealth.getDatetime());

            // set to temporary storage
            select_year = calendar.get(Calendar.YEAR);
            select_month = calendar.get(Calendar.MONTH)+1;
            select_day = calendar.get(Calendar.DAY_OF_MONTH);

            // set text to button
            selectDayButton.setText(calendar.get(Calendar.MONTH)+1+" "+calendar.get(Calendar.DAY_OF_MONTH)+", "+calendar.get(Calendar.YEAR));
        }
    }

    private void processSelectTimerControllers() {
        // initialize birth date button
        selectTimerButton = (Button) findViewById(R.id.select_time_button);

        // avoid all upper case
        selectTimerButton.setTransformationMethod(null);

        // initialize calendar
        calendar = Calendar.getInstance();


        // set date picker listener
        final TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int shour, int smin) {
                select_hour = shour;
                select_min = smin;

                // set text for button
                selectTimerButton.setText( select_hour + ":" + select_min);
            }
        };

        // set button listener
        selectTimerButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mCurrentTime = Calendar.getInstance();
                int hour = mCurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mCurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                if (select_hour == 0 && select_min == 0) {
                    mTimePicker = new TimePickerDialog(MyTemperatureRecord.this, timeSetListener, hour, minute, true);
                } else {
                    mTimePicker = new TimePickerDialog(MyTemperatureRecord.this, timeSetListener, select_hour, select_min, true);
                }
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        // set text with date time to the button if not empty
        if (curHealth.getDatetime() != 0) {
            // set time in millis to calendar
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(curHealth.getDatetime());

            // set to temporary storage
            select_hour = calendar.get(Calendar.HOUR_OF_DAY);
            select_min = calendar.get(Calendar.MINUTE);

            // set text to button
            selectTimerButton.setText(calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE));
        }


    }
    private void processEditTextControllers() {
        my_Temperature_Record_text = (EditText)findViewById(R.id.temperature_text);

        // set text to edit text if current profile not empty
        if (curHealth.getTemperature() != 0) {
            // set to edit text
            my_Temperature_Record_text.setText(Float.toString(curHealth.getTemperature()));
        }
    }

    // process update button
    private void processUpdateButtonControllers() {
        // initialize button
        updateButton = (Button)findViewById(R.id.update_button);

        // avoid all upper case
        updateButton.setTransformationMethod(null);
    }

    public void onSubmit(View view) {
        // if user updated the profile
        if (view.getId() == R.id.update_button) {
            boolean updatable = true;
            ImageView TempSign = (ImageView) findViewById(R.id.temp_sign);
            TextView TempComment = (TextView) findViewById(R.id.temp_comment);
            // convert integer time to calendar
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, select_year);
            calendar.set(Calendar.MONTH, select_month - 1);
            calendar.set(Calendar.DAY_OF_MONTH, select_day);
            calendar.set(Calendar.HOUR_OF_DAY, select_hour);
            calendar.set(Calendar.MINUTE, select_min);

            // set temperature
            if (my_Temperature_Record_text.getText().toString().length() == 0) {
                updatable = false;
                Toast.makeText(getApplicationContext(), "體溫不可為空", Toast.LENGTH_LONG).show();
            }
            else tempHealth.setTemperature(Float.parseFloat(my_Temperature_Record_text.getText().toString()));

            if (updatable) {
                // set the chosen date time and last modify time
                tempHealth.setDatetime(calendar.getTimeInMillis());
                tempHealth.setLastModify(new Date().getTime());

                // set user id
                tempHealth.setUserFBID(Long.parseLong(LoginActivity.facebookUserID));

                // store to health data base use update & insert
                if (healthDAO.isTableEmpty() == true) {
                    healthDAO.insert(tempHealth);
                } else {
                    healthDAO.insert(tempHealth);
                }

                float T=Float.parseFloat(my_Temperature_Record_text.getText().toString());

                if (T >= 40) {
                    TempSign.setImageResource(R.drawable.temp5);
                    TempComment.setText("高燒建議就醫");
                } else if (T >= 37.5 && T < 40) {
                    TempSign.setImageResource(R.drawable.temp5);
                    TempComment.setText("可能有發燒症狀");
                } else if (T < 37.5 && T >= 36) {
                    TempSign.setImageResource(R.drawable.temp4);
                    TempComment.setText("您的體溫正常");
                } else if (T >= 35 && T < 36){
                    TempSign.setImageResource(R.drawable.temp2);
                    TempComment.setText("注意保暖");
                } else {
                    TempSign.setImageResource(R.drawable.temp2);
                    TempComment.setText("失溫請速就醫");
                }
                Toast.makeText(getApplicationContext(), "更新完成", Toast.LENGTH_LONG).show();
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.home) {
            Intent intent_home = new Intent();
            intent_home.setClass(MyTemperatureRecord.this, HomeActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("BACK", 1);
            intent_home.putExtras(bundle);
            startActivity(intent_home);
            finish();
        } else if (id == R.id.food_list) {
            Intent intent_main = new Intent();
            intent_main.setClass(MyTemperatureRecord.this, MainActivity.class);
            startActivity(intent_main);
            finish();
            //Toast.makeText(this, "Open food list", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.Import) {
            selectImage();
            //Toast.makeText(this, "Import food", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.chat) {
            Intent intent_chat_bot = new Intent();
            intent_chat_bot.setClass(MyTemperatureRecord.this, ChatBotActivity.class);
            startActivity(intent_chat_bot);
            finish();
        } else if (id == R.id.new_calendar){
            Intent intent_new_calendar = new Intent();
            intent_new_calendar.setClass(MyTemperatureRecord.this, NewCalendarActivity.class);
            startActivity(intent_new_calendar);
            finish();
        } else if (id == R.id.blood_pressure){
            Intent intent_blood_pressure = new Intent();
            intent_blood_pressure.setClass(MyTemperatureRecord.this, MyBloodPressure.class);
            startActivity(intent_blood_pressure);
            finish();
        } else if (id == R.id.temp_record){
            Intent intent_temp_record = new Intent();
            intent_temp_record.setClass(MyTemperatureRecord.this, MyTemperatureRecord.class);
            startActivity(intent_temp_record);
            finish();
        } else if (id == R.id.water_record){
            Intent intent_water_record = new Intent();
            intent_water_record.setClass(MyTemperatureRecord.this, DrinkWaterDiary.class);
            startActivity(intent_water_record);
            finish();
        } else if (id == R.id.message) {
            Intent intent_message = new Intent();
            intent_message.setClass(MyTemperatureRecord.this, MessageActivity.class);
            startActivity(intent_message);
            finish();
            //Toast.makeText(this, "Send message", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.mail){
            Intent intent_mail = new Intent();
            intent_mail.setClass(MyTemperatureRecord.this, MailActivity.class);
            startActivity(intent_mail);
            finish();
        } else if (id == R.id.setting_list) {
            Intent intent_setting = new Intent();
            intent_setting.setClass(MyTemperatureRecord.this, SettingsActivity.class);
            startActivity(intent_setting);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void selectImage(){
        final CharSequence[] items = { "照相", "從相簿中選取", "取消" };
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("新增食物");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int index) {
                if (items[index].equals("照相")) {
                    if (activityIndex == MYTEMPERATURE_ACTIVITY) {
                        /*Intent intent_camera = new Intent("com.example.nthucs.prototype.TAKE_PICT");
                        activity.startActivityForResult(intent_camera, SCAN_FOOD);*/
                        Intent result = new Intent();
                        result.putExtra(FROM_CAMERA, SCAN_FOOD);
                        result.setClass(activity, MainActivity.class);
                        activity.startActivity(result);
                        activity.finish();
                    } /*else {
                        // back to setting activity
                        Intent result = new Intent();
                        result.putExtra(FROM_CAMERA, SCAN_FOOD);
                        result.setClass(activity, MainActivity.class);
                        activity.startActivity(result);
                        activity.finish();
                    }*/
                } else if (items[index].equals("從相簿中選取")) {
                    if (activityIndex == MYTEMPERATURE_ACTIVITY) {
                        /*Intent intent_gallery = new Intent("com.example.nthucs.prototype.TAKE_PHOTO");
                        activity.startActivityForResult(intent_gallery, TAKE_PHOTO);*/
                        Intent result = new Intent();
                        result.putExtra(FROM_GALLERY, TAKE_PHOTO);
                        result.setClass(activity, MainActivity.class);
                        activity.startActivity(result);
                        activity.finish();
                    } /*else {
                        // back to setting activity
                        Intent result = new Intent();
                        result.putExtra(FROM_GALLERY, TAKE_PHOTO);
                        result.setClass(activity, MailActivity.class);
                        activity.startActivity(result);
                        activity.finish();
                    }*/
                } else if (items[index].equals("取消")) {
                    dialog.dismiss();
//                    Intent intent = new Intent();
//                    intent.setClass(MyBloodPressure.this, MyBloodPressure.class);
//                    startActivity(intent);
                }
            }
        });
        builder.show();
    }
}
