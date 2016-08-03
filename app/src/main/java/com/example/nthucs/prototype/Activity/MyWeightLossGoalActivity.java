package com.example.nthucs.prototype.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.nthucs.prototype.R;
import android.widget.EditText;

import com.example.nthucs.prototype.R;
import com.example.nthucs.prototype.Settings.MyProfileDAO;
import com.example.nthucs.prototype.Settings.Profile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by user on 2016/7/27.
 */
public class MyWeightLossGoalActivity extends AppCompatActivity {

    // Back, Update button
    private Button backButton, updateButton;

    // Edit text for target weight, weekly target
    private EditText target_weight_text, weekly_target_text;

    // data base for profile
    private MyProfileDAO myProfileDAO;

    // list of profile
    private List<Profile> profileList = new ArrayList<>();

    // currently profile
    private Profile curProfile;

    // temporary target weight and weekly target
    private Float tempTargetWeight, tempWeeklyTarget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_weight_loss_goal);

        // initialize data base
        myProfileDAO = new MyProfileDAO(getApplicationContext());

        // get all profile data from data base
        profileList = myProfileDAO.getAll();

        // get the last profile data in the list
        if (myProfileDAO.isTableEmpty() == true) {
            curProfile = new Profile();
            tempTargetWeight = 0.0f;
            tempWeeklyTarget = 0.0f;
        } else {
            curProfile = profileList.get(profileList.size()-1);

            tempTargetWeight = curProfile.getWeightLossGaol();
            tempWeeklyTarget = curProfile.getWeeklyLossWeight();

            // find non-zero target weight value
            if (tempTargetWeight == 0) {
                for (int i = 0 ; i < profileList.size() ; i++) {
                    if (profileList.get(i).getWeightLossGaol() != 0) {
                        tempTargetWeight = profileList.get(i).getWeightLossGaol();
                    }
                }
            }

            // find non-zero weekly target value
            if (tempWeeklyTarget == 0) {
                for (int i = 0 ; i < profileList.size() ; i++) {
                    if (profileList.get(i).getWeeklyLossWeight() != 0) {
                        tempWeeklyTarget = profileList.get(i).getWeeklyLossWeight();
                    }
                }
            }
        }

        // custom view in action bar
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.my_weight_loss_goal_menu);

        // process back button
        processBackControllers();

        // process target weight and weekly target edit text
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
                back.setClass(MyWeightLossGoalActivity.this, SettingsActivity.class);
                startActivity(back);
                finish();

                // origin activity slide to right, new activity slide from left
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
    }

    // process relative edit text
    private void processEditTextControllers() {
        target_weight_text = (EditText)findViewById(R.id.target_weight_edit_text);
        weekly_target_text = (EditText)findViewById(R.id.weekly_target_edit_text);

        // set text to edit text if current profile not empty
        if (curProfile.getWeightLossGaol() != 0 && curProfile.getWeeklyLossWeight() != 0) {
            // set to edit text
            target_weight_text.setText(Float.toString(curProfile.getWeightLossGaol()));
            weekly_target_text.setText(Float.toString(curProfile.getWeeklyLossWeight()));
            // set text to edit text if temporary float not-zero
        } else if (tempTargetWeight != 0 && tempWeeklyTarget != 0) {
            // set to edit text
            target_weight_text.setText(Float.toString(tempTargetWeight));
            weekly_target_text.setText(Float.toString(tempWeeklyTarget));
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
            // set target weight and weekly target
            curProfile.setWeightLossGaol(Float.parseFloat(target_weight_text.getText().toString()));
            curProfile.setWeeklyLossWeight(Float.parseFloat(weekly_target_text.getText().toString()));

            // set last modify time
            curProfile.setLastModify(new Date().getTime());

            // update to my profile data base
            myProfileDAO.update(curProfile);
        }
    }
}