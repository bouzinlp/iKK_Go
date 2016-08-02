package com.example.nthucs.prototype.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.nthucs.prototype.R;

/**
 * Created by user on 2016/8/2.
 */
public class CalorieConsumptionActivity extends AppCompatActivity {

    // Back button
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calorie_consumption);

        // custom view in action bar
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.calorie_consumption_menu);

        // process back button
        processBackControllers();
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
                back.setClass(CalorieConsumptionActivity.this, SettingsActivity.class);
                startActivity(back);
                finish();

                // origin activity slide to right, new activity slide from left
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
    }
}
