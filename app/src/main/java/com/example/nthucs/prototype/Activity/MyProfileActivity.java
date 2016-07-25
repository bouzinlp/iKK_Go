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
import android.widget.Spinner;

import com.example.nthucs.prototype.R;

import java.util.Calendar;

/**
 * Created by user on 2016/7/23.
 */
public class MyProfileActivity extends AppCompatActivity {

    // Back button
    private Button backButton;

    // Birth date button
    private Button birthDayButton;

    // Calendar
    private Calendar calendar;

    // Year, month, day
    private int birth_year, birth_month, birth_day;

    // Gender choose spinner
    private Spinner genderSpinner;

    // Gender list adapter
    private ArrayAdapter genderListAdapter;

    // Temporary storage for gender before update
    private String choosen_sex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        // custom view in action bar
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.my_profile_menu);

        // initialize calendar
        calendar = Calendar.getInstance();

        // process back button
        processBackControllers();

        // process birth day button
        processBirthDayControllers();

        // process gender spinner
        processGenderControllers();

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
                    new DatePickerDialog(MyProfileActivity.this, dateSetListener, birth_year, (birth_month-1), birth_day).show();
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
                            choosen_sex = adapterView.getSelectedItem().toString();
                            break;
                        case 1:
                            choosen_sex = adapterView.getSelectedItem().toString();
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
}
