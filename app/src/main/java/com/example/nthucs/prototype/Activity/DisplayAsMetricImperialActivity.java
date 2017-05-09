package com.example.nthucs.prototype.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.nthucs.prototype.R;

/**
 * Created by user on 2016/9/24.
 */


public class DisplayAsMetricImperialActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    // Back button
    private Button backButton;
    private RadioButton metric;
    private RadioButton imperial;
    private RadioGroup rgroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_as_metric_imperial);
        // custom view in action bar
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.display_as_metric_imperial_menu);
        // process back button
        processBackControllers();
        init();
        rgroup.setOnCheckedChangeListener(listener);

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
                back.setClass(DisplayAsMetricImperialActivity.this, SettingsActivity.class);
                startActivity(back);
                finish();

                // origin activity slide to right, new activity slide from left
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
    }

    private void init(){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        rgroup = (RadioGroup) findViewById(R.id.rgroup);
        metric = (RadioButton) findViewById(R.id.unit2metric);
        imperial = (RadioButton) findViewById(R.id.unit2imperial);
        int radioId = sharedPreferences.getInt("checkedIndex",0);
        if(radioId!=0){
            RadioButton rbtn=(RadioButton) findViewById(radioId);
            rbtn.setChecked(true);
        }
    }

    private RadioGroup.OnCheckedChangeListener listener = new RadioGroup.OnCheckedChangeListener(){

        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int i) {
            sharedPreferences.edit().putInt("checkedIndex", i).apply();
            switch (i){
                case R.id.unit2metric:
                    MainActivity.metricFlag = true;
                    break;
                case R.id.unit2imperial:
                    MainActivity.metricFlag = false;
                    break;
            }
        }
    };
}
