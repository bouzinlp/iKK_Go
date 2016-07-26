package com.example.nthucs.prototype.Activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.nthucs.prototype.R;
import com.example.nthucs.prototype.Settings.MyProfileDAO;
import com.example.nthucs.prototype.Settings.Profile;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by user on 2016/7/23.
 */
public class MyProfileActivity extends AppCompatActivity {

    // Back, Birth date, Update button
    private Button backButton,  birthDayButton, updateButton;

    // Calendar
    private Calendar calendar;

    // Year, month, day
    private int birth_year, birth_month, birth_day;

    // Gender choose spinner
    private Spinner genderSpinner;

    // Gender list adapter
    private ArrayAdapter genderListAdapter;

    // Temporary storage for gender before update
    private String chosen_sex;

    // Edit text for height, weight
    private EditText height_text, weight_text;

    // Temporary storage for user's height and weight
    private float inputHeight, inputWeight;

    // data base for profile
    private MyProfileDAO myProfileDAO;

    // list of profile
    private List<Profile> profileList = new ArrayList<>();

    // currently and temporary profile
    private Profile curProfile, tempProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        // initialize data base
        myProfileDAO = new MyProfileDAO(getApplicationContext());

        // get all profile data from data base
        profileList = myProfileDAO.getAll();

        // get the last profile data in the list
        curProfile = profileList.get(profileList.size()-1);

        // set new profile for updated
        tempProfile = new Profile();

        // custom view in action bar
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.my_profile_menu);

        // process back button
        processBackControllers();

        // process birth day button
        processBirthDayControllers();

        // process gender spinner
        processGenderControllers();

        // process height and weight edit text
        processEditTextControllers();

        // process update button
        processUpdateButtonControllers();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    // process back button listener
    private void processBackControllers() {
        // initialize back button
        backButton = (Button)findViewById(R.id.back_button);

        // avoid all upper case
        backButton.setTransformationMethod(null);

        // set button listener
        backButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent back = new Intent();
                back.setClass(MyProfileActivity.this, SettingsActivity.class);
                startActivity(back);
                finish();

                // origin activity slide to right, new activity slide from left
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
    }

    // process birth day button listener
    private void processBirthDayControllers() {
        // initialize birth date button
        birthDayButton = (Button)findViewById(R.id.birth_day_button);

        // avoid all upper case
        birthDayButton.setTransformationMethod(null);

        // initialize calendar
        calendar = Calendar.getInstance();

        // set date picker listener
        final DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                birth_month = monthOfYear + 1;
                birth_day = dayOfMonth;
                birth_year = year;

                // set text for button
                birthDayButton.setText(birth_month+" "+birth_day+", "+birth_year);
            }
        };

        // set button listener
        birthDayButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (birth_day == 0 && birth_year == 0 && birth_month == 0) {
                    new DatePickerDialog(MyProfileActivity.this, dateSetListener, calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
                } else {
                    new DatePickerDialog(MyProfileActivity.this, dateSetListener, birth_year, (birth_month - 1), birth_day).show();
                }
            }
        });
    }

    // process gender spinner
    private void processGenderControllers() {
        // initialize spinner
        genderSpinner = (Spinner)findViewById(R.id.sex_spinner);

        // initialize adapter
        genderListAdapter = new ArrayAdapter(MyProfileActivity.this, R.layout.spinner_layout, new String[]{"Male", "Female"});

        // set adapter's drop down view
        genderListAdapter.setDropDownViewResource(R.layout.spinner_dropdown);

        // set adapter to spinner
        genderSpinner.setAdapter(genderListAdapter);

        // set adapter view's item selected listener
        AdapterView.OnItemSelectedListener spinnerlistener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView adapterView, View view, int position, long id) {
                if (adapterView.getId() == R.id.sex_spinner) {
                    switch (adapterView.getSelectedItemPosition()) {
                        case 0:
                            chosen_sex = adapterView.getSelectedItem().toString();
                            break;
                        case 1:
                            chosen_sex = adapterView.getSelectedItem().toString();
                            break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView arg0) {}
        };

        // register to spinner listener
        genderSpinner.setOnItemSelectedListener(spinnerlistener);
    }

    // process relative edit text
    private void processEditTextControllers() {
        height_text = (EditText)findViewById(R.id.height_edit_text);
        weight_text = (EditText)findViewById(R.id.weight_edit_text);

    }

    // process update button
    private void processUpdateButtonControllers() {
        // initialize button
        updateButton = (Button)findViewById(R.id.update_button);

        // avoid all upper case
        updateButton.setTransformationMethod(null);
    }

    public void onSubmit(View view) {
        if (view.getId() == R.id.update_button) {
            tempProfile.setDatetime(new Date().getTime());
            tempProfile.setLastModify(new Date().getTime());

            // set birthday to calendar class
            calendar.set(Calendar.YEAR, birth_year);
            calendar.set(Calendar.MONTH, birth_month - 1);
            calendar.set(Calendar.DAY_OF_MONTH, birth_day);

            tempProfile.setBirthDay(calendar.getTimeInMillis());

            //System.out.println("birth " + calendar.getTimeInMillis());
            //System.out.println("birth-in-date " + String.format(Locale.getDefault(), "%tF  %<tR", new Date(calendar.getTimeInMillis())));

            tempProfile.setSex(chosen_sex);
            tempProfile.setHeight(Float.parseFloat(height_text.getText().toString()));
            tempProfile.setWeight(Float.parseFloat(weight_text.getText().toString()));

            myProfileDAO.insert(tempProfile);

            System.out.println(myProfileDAO.isTableEmpty());
        }
    }
}
