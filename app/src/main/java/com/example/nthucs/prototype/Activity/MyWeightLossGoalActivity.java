package com.example.nthucs.prototype.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.nthucs.prototype.R;

/**
 * Created by user on 2016/7/27.
 */
public class MyWeightLossGoalActivity extends AppCompatActivity {

    // Back, Update button
    private Button backButton, updateButton;

    // Edit text for target weight, weekly target
    private EditText target_weight_text, weekly_target_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_weight_loss_goal);

        // custom view in action bar
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.my_weight_loss_goal_menu);

        // process back button
        processBackControllers();

        // process update button
        processUpdateButtonControllers();

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
                back.setClass(MyWeightLossGoalActivity.this, SettingsActivity.class);
                startActivity(back);
                finish();

                // origin activity slide to right, new activity slide from left
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
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
