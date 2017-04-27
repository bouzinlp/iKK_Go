package com.example.nthucs.prototype.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.nthucs.prototype.R;
import com.example.nthucs.prototype.Settings.Health;
import com.example.nthucs.prototype.Settings.HealthDAO;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by user on 2016/9/24.
 */
public class MyCurrentExerciseActivity extends AppCompatActivity {

    // Back button
    private Button backButton;

    // Answer spinner
    private Spinner sectionZeroSpinner;
    private Spinner sectionOneSpinner;
    private Spinner sectionTwoSpinner;

    // answer list adapter
    private ArrayAdapter ansListAdapter, ansListAdapterForAns0;

    // store answer
    private String ansZero, ansOne, ansTwo;

    // data base for profile
    private HealthDAO healthDAO;

    // list of profile
    private List<Health> healthList = new ArrayList<>();

    // currently and temporary profile
    private Health curHealth, tempHealth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_current_exercise);
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
                if (healthList.get(i).getActivityFactor() != 0) {
                    curHealth = healthList.get(i);
                    cnt = 1;
                }
            }
            if (cnt == 0) curHealth = new Health();
        }

        // set new health profile for updated
        tempHealth = curHealth;

        // custom view in action bar
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.my_current_exercise_menu);

        // process back button
        processBackControllers();

        // process answer spinner
        processSectionControllers();
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
                back.setClass(MyCurrentExerciseActivity.this, SettingsActivity.class);
                startActivity(back);
                finish();

                // origin activity slide to right, new activity slide from left
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
    }

    // process answer spinner
    private void processSectionControllers() {
        // initialize spinner
        sectionZeroSpinner = (Spinner) findViewById(R.id.sectionZeroSpinner);
        sectionOneSpinner = (Spinner) findViewById(R.id.sectionOneSpinner);
        sectionTwoSpinner = (Spinner) findViewById(R.id.sectionTwoSpinner);

        // initialize adapter
        ansListAdapter = new ArrayAdapter(MyCurrentExerciseActivity.this, R.layout.spinner_layout, new String[]{"a", "b", "c", "d"});
        ansListAdapterForAns0 = new ArrayAdapter(MyCurrentExerciseActivity.this, R.layout.spinner_layout, new String[]{"a", "b", "c", "d", "e"});

        // set adapter to spinner
        sectionZeroSpinner.setAdapter(ansListAdapterForAns0);
        sectionOneSpinner.setAdapter(ansListAdapter);
        sectionTwoSpinner.setAdapter(ansListAdapter);

        // set adapter view's item selected listener
        AdapterView.OnItemSelectedListener spinnerListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView adapterView, View view, int position, long id) {
                if (adapterView.getId() == R.id.sectionZeroSpinner) {
                    switch (adapterView.getSelectedItemPosition()) {
                        case 0:
                            ansZero = Float.toString(1.2f);
                            insertHealthDAO();
                            break;
                        case 1:
                            ansZero = Float.toString(1.375f);
                            insertHealthDAO();
                            break;
                        case 2:
                            ansZero = Float.toString(1.55f);
                            insertHealthDAO();
                            break;
                        case 3:
                            ansZero = Float.toString(1.725f);
                            insertHealthDAO();
                            break;
                        case 4:
                            ansZero = Float.toString(1.9f);
                            insertHealthDAO();
                            break;
                    }
                } else if (adapterView.getId() == R.id.sectionOneSpinner) {
                    switch (adapterView.getSelectedItemPosition()) {
                        case 0:
                            ansOne = adapterView.getSelectedItem().toString();
                            break;
                        case 1:
                            ansOne = adapterView.getSelectedItem().toString();
                            break;
                        case 2:
                            ansOne = adapterView.getSelectedItem().toString();
                            break;
                        case 3:
                            ansOne = adapterView.getSelectedItem().toString();
                            break;
                    }
                } else if (adapterView.getId() == R.id.sectionTwoSpinner) {
                    switch (adapterView.getSelectedItemPosition()) {
                        case 0:
                            ansTwo = adapterView.getSelectedItem().toString();
                            break;
                        case 1:
                            ansTwo = adapterView.getSelectedItem().toString();
                            break;
                        case 2:
                            ansTwo = adapterView.getSelectedItem().toString();
                            break;
                        case 3:
                            ansTwo = adapterView.getSelectedItem().toString();
                            break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView arg0) {
            }
        };

        // register to spinner listener
        sectionZeroSpinner.setOnItemSelectedListener(spinnerListener);
        sectionOneSpinner.setOnItemSelectedListener(spinnerListener);
        sectionTwoSpinner.setOnItemSelectedListener(spinnerListener);

        // set text with answer if current profile not empty
        if (curHealth.getActivityFactor() == 1.2f) {
            sectionZeroSpinner.setSelection(0);
        } else if (curHealth.getActivityFactor() == 1.375f) {
            sectionZeroSpinner.setSelection(1);
        } else if (curHealth.getActivityFactor() == 1.55f) {
            sectionZeroSpinner.setSelection(2);
        } else if (curHealth.getActivityFactor() == 1.725f) {
            sectionZeroSpinner.setSelection(3);
        } else if (curHealth.getActivityFactor() == 1.9f) {
            sectionZeroSpinner.setSelection(4);
        }
    }

    private void insertHealthDAO() {
        tempHealth.setDatetime(new Date().getTime());
        tempHealth.setLastModify(new Date().getTime());

        // set user id
        tempHealth.setUserFBID(Long.parseLong(LoginActivity.facebookUserID));

        // set activity factor
        tempHealth.setActivityFactor(Float.parseFloat(ansZero));

        // store to health data base use update & insert
        if (healthDAO.isTableEmpty() == true) {
            healthDAO.insert(tempHealth);
        } else {
            healthDAO.insert(tempHealth);
        }
    }
}
