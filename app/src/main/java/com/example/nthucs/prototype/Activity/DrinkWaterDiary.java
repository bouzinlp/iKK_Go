package com.example.nthucs.prototype.Activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.example.nthucs.prototype.R;
import com.example.nthucs.prototype.Settings.MyProfileDAO;
import com.example.nthucs.prototype.Settings.Profile;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DrinkWaterDiary extends AppCompatActivity {
    // Back, Update button
    private Button backButton,  selectDayButton ,selectTimerButton , updateButton;

    // Calendar
    private Calendar calendar;

    // Temporary storage before update
    private int select_hour , select_min;

    // Edit text
    private EditText waterDrinked_text;

    // data base for profile
    private MyProfileDAO myProfileDAO;

    // list of profile
    private List<Profile> profileList = new ArrayList<>();

    // currently and temporary profile
    private Profile curProfile, tempProfile;

    //
    private int select_day , select_year , select_month;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drink_water_diary);
        myProfileDAO = new MyProfileDAO(getApplicationContext());

        // get all profile data from data base
        profileList = myProfileDAO.getAll();

        // get the last profile data in the list
        curProfile = new Profile();
        // custom view in action bar
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.drink_water_diary_menu);

        // process back button
        processBackControllers();
        // process day button
        processSelectDayControllers();
        //
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
                back.setClass(DrinkWaterDiary.this, SettingsActivity.class);
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
                    new DatePickerDialog(DrinkWaterDiary.this, dateSetListener, calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
                } else {
                    new DatePickerDialog(DrinkWaterDiary.this, dateSetListener, select_year, (select_month - 1), select_day).show();
                }
            }
        });

        // set text with birthday to the button if not empty


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
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(DrinkWaterDiary.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        selectTimerButton.setText( selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute,true);
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

    }
    private void processEditTextControllers() {
        waterDrinked_text = (EditText)findViewById(R.id.water_text);
        // set text to edit text if current profile not empty
        if (curProfile.getHeight() != 0 && curProfile.getWeight() != 0) {
            // set to edit text

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

        }
    }
}
