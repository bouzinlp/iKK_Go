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

import java.util.ArrayList;

/**
 * Created by user on 2016/9/24.
 */
public class MyCurrentExerciseActivity extends AppCompatActivity {

    // Back button
    private Button backButton;

    // Answer spinner
    private Spinner sectionOneSpinner;
    private Spinner sectionTwoSpinner;

    // answer list adapter
    private ArrayAdapter ansListAdapter;

    // store answer
    private String ansOne, ansTwo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_current_exercise);

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
        backButton = (Button)findViewById(R.id.back_button);

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
        sectionOneSpinner = (Spinner)findViewById(R.id.sectionOneSpinner);
        sectionTwoSpinner = (Spinner)findViewById(R.id.sectionTwoSpinner);

        // initialize adapter
        ansListAdapter = new ArrayAdapter(MyCurrentExerciseActivity.this, R.layout.spinner_layout, new String[]{"a", "b", "c", "d"});

        // set adapter to spinner
        sectionOneSpinner.setAdapter(ansListAdapter);
        sectionTwoSpinner.setAdapter(ansListAdapter);

        // set adapter view's item selected listener
        AdapterView.OnItemSelectedListener spinnerListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView adapterView, View view, int position, long id) {
                if (adapterView.getId() == R.id.sectionOneSpinner) {
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
                }
            }

            @Override
            public void onNothingSelected(AdapterView arg0) {}
        };

        // register to spinner listener
        sectionOneSpinner.setOnItemSelectedListener(spinnerListener);
        sectionTwoSpinner.setOnItemSelectedListener(spinnerListener);

        // set text with answer if current profile not empty

    }
}
