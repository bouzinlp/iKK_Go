package com.example.nthucs.prototype.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewDebug;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nthucs.prototype.R;
import com.example.nthucs.prototype.Settings.Health;
import com.example.nthucs.prototype.Settings.HealthDAO;
import com.example.nthucs.prototype.Settings.MyProfileDAO;
import com.example.nthucs.prototype.Settings.Profile;

import java.util.ArrayList;
import java.util.Calendar;
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

    // Year, month, day
    private int birth_year, birth_month, birth_day;

    // data base for profile, health
    private MyProfileDAO myProfileDAO;
    private HealthDAO healthDAO;

    // list of profile, health
    private List<Profile> profileList = new ArrayList<>();
    private List<Health> healthList = new ArrayList<>();

    // currently profile, health
    private Profile curProfile;
    private Health curHealth;

    // temporary target weight and weekly target
    private Float tempTargetWeight, tempWeeklyTarget;

    //BMR
    private TextView BMR_text , BMR_mwl;
    private  int sex_num , age_num;
    private float BMR;

    //consume & absorb
    private TextView absorb_text , consume_text ;
    private int absorb_i;
    private float absorb ,consume;

    //consume suggest
    private TextView consume_suggest;

    // for chatbot use
    public static float absorb_chatbot, consume_chatbot;

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
            tempTargetWeight = curProfile.getWeightLossGoal();
            tempWeeklyTarget = curProfile.getWeeklyLossWeight();

            // find non-zero target weight value
            if (tempTargetWeight == 0) {
                for (int i = 0 ; i < profileList.size() ; i++) {
                    if (profileList.get(i).getWeightLossGoal() != 0) {
                        tempTargetWeight = profileList.get(i).getWeightLossGoal();
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

        // initialize data base
        healthDAO = new HealthDAO(getApplicationContext());

        // get all health data from data base
        healthList = healthDAO.getAll();

        // get the last health data in the list
        if (healthDAO.isTableEmpty() == true) {
            curHealth = new Health();
        } else {
            curHealth = healthList.get(healthList.size() - 1);
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

        // process BMI
        processTextViewControllers();
//
//        Intent i = new Intent();
//        i.setClass(MyWeightLossGoalActivity.this,ChatBotActivity.class);
//        Bundle b = new Bundle();
//        b.putFloat("absorb",absorb);
//        i.putExtras(b);

    }

    @Override
    protected void onResume() {
        super.onResume();
        // for chatbot use

        absorb_chatbot = absorb;
        consume_chatbot = consume;
        //
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
        if (curProfile.getWeightLossGoal() != 0 && curProfile.getWeeklyLossWeight() != 0) {
            // set to edit text
            target_weight_text.setText(Float.toString(curProfile.getWeightLossGoal()));
            weekly_target_text.setText(Float.toString(curProfile.getWeeklyLossWeight()));
            // set text to edit text if temporary float not-zero
        } else if (tempTargetWeight != 0 && tempWeeklyTarget != 0) {
            // set to edit text
            target_weight_text.setText(Float.toString(tempTargetWeight));
            weekly_target_text.setText(Float.toString(tempWeeklyTarget));
        }
    }

    // process BMI text view
    private void processTextViewControllers() {
        consume_suggest = (TextView)findViewById(R.id.consume_suggest);
        absorb_text = (TextView)findViewById(R.id.absorb);
        consume_text = (TextView)findViewById(R.id.consume);
        BMR_mwl = (TextView)findViewById(R.id.BMR_mwl);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(curProfile.getBirthDay());

        // set to temporary storage
        birth_year = calendar.get(Calendar.YEAR);
        birth_month = calendar.get(Calendar.MONTH)+1;
        birth_day = calendar.get(Calendar.DAY_OF_MONTH);

        //calculate age

        Calendar now_calendar = Calendar.getInstance();
        int now_year = now_calendar.get(Calendar.YEAR);                 //取出年
        int now_month = now_calendar.get(Calendar.MONTH) + 1;          //取出月，月份的編號是由0~11 故+1
        int now_day = now_calendar.get(Calendar.DAY_OF_MONTH);       //取出日

        if(birth_month>now_month){
            age_num = now_year-birth_year-1;
        }else if((birth_month==now_month)&&(birth_day>now_day)){
            age_num = now_year-birth_year-1;
        }else{
            age_num = now_year-birth_year;
        }

        //sex
        if (curProfile.getSex().equals("男")) {
            sex_num = 1;
        } else if (curProfile.getSex().equals("女")) {
            sex_num = 0;
        }

        BMR = calculate_BMR(Float.toString(curProfile.getHeight()), Float.toString(curProfile.getWeight()), sex_num , age_num);
        BMR_mwl.setText(Float.toString(BMR));
        absorb_i = (int)(BMR);

        if(BMR%100!=0){
            absorb = (absorb_i/100)*100+100;
        }else{
            absorb = (absorb_i/100)*100;
        }
        consume = absorb+tempWeeklyTarget*1100;

        if((int)(tempWeeklyTarget*10)==5){
            consume_suggest.setText("建議每日快走2小時");
        }else if((int)(tempWeeklyTarget*10)==6){
            consume_suggest.setText("建議每日慢跑1.5小時");
        }else if((int)(tempWeeklyTarget*10)==7){
            consume_suggest.setText("建議每日慢跑2小時");
        }else if((int)(tempWeeklyTarget*10)==8){
            consume_suggest.setText("建議每日游泳1.5小時");
        }else if((int)(tempWeeklyTarget*10)==9){
            consume_suggest.setText("建議每日游泳2小時");
        }else if((int)(tempWeeklyTarget*10)==10){
            consume_suggest.setText("建議每日快跑2小時");
        }


        absorb_text.setText(Float.toString(absorb));
        consume_text.setText(Float.toString(consume));
    }

    private float calculate_BMR(String s_height, String s_weight, int sex, int age){
        float height = Float.valueOf(s_height);       // 計算的時候，型別要一致才不會導致計算錯誤
        float weight = Float.valueOf(s_weight);      // 雖然某些計算值可以為 int 例如體重，但如果體重 weight 你給 int 型別會導致計算上的錯誤
        float bmr;
        float AR;

        if (curHealth.getActivityFactor() == 0.0f) AR = 1.0f;
        else AR = curHealth.getActivityFactor();

        // 0 female  , 1 male
        if (sex == 0) {
            bmr = (float)((9.6 * weight)+(1.8*height)-(4.7*age)+655);
        } else {
            bmr = (float)((13.7 * weight)+(5*height)-(6.8*age)+66);
        }
        return bmr*AR;
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
            // set target weight and weekly target
            if (target_weight_text.getText().toString().length() == 0) {
                updatable = false;
                Toast.makeText(getApplicationContext(), "目標體重不可為空", Toast.LENGTH_LONG).show();
            }
            else curProfile.setWeightLossGoal(Float.parseFloat(target_weight_text.getText().toString()));

            if (weekly_target_text.getText().toString().length() == 0) {
                updatable = false;
                Toast.makeText(getApplicationContext(), "每周目標不可為空", Toast.LENGTH_LONG).show();
            }

            if (updatable) {
                curProfile.setWeeklyLossWeight(Float.parseFloat(weekly_target_text.getText().toString()));

                // set last modify time
                curProfile.setLastModify(new Date().getTime());

                // update to my profile data base
                myProfileDAO.update(curProfile);

                consume = absorb + (Float.parseFloat(weekly_target_text.getText().toString())) * 1100;
                absorb_text.setText(Float.toString(absorb));
                consume_text.setText(Float.toString(consume));

                Toast.makeText(getApplicationContext(), "更新完成", Toast.LENGTH_LONG).show();
            }

        }
    }

}