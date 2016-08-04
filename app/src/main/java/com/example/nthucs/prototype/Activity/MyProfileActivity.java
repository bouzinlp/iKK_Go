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
import android.widget.TextView;

import com.example.nthucs.prototype.R;
import com.example.nthucs.prototype.Settings.MyProfileDAO;
import com.example.nthucs.prototype.Settings.Profile;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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

    // data base for profile
    private MyProfileDAO myProfileDAO;

    // list of profile
    private List<Profile> profileList = new ArrayList<>();

    // currently and temporary profile
    private Profile curProfile, tempProfile;

    // BMI text view and value
    private TextView BMI_text;
    private float BMI;

    //BMR text view and value
    private TextView BMR_text;
    private  int sex_num , age_num;
    private float BMR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        // initialize data base
        myProfileDAO = new MyProfileDAO(getApplicationContext());

        // get all profile data from data base
        profileList = myProfileDAO.getAll();

        // get the last profile data in the list
        if (myProfileDAO.isTableEmpty() == true) {
            curProfile = new Profile();
        } else {
            curProfile = profileList.get(profileList.size()-1);
        }

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

        // process BMI text view
        processTextViewControllers();

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

        // set text with birthday to the button if not empty
        if (curProfile.getBirthDay() != 0) {
            // set time in millis to calendar
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

            // set text to button
            birthDayButton.setText(calendar.get(Calendar.MONTH)+1+" "+calendar.get(Calendar.DAY_OF_MONTH)+", "+calendar.get(Calendar.YEAR));
        }
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
                            sex_num = 1;
                            break;
                        case 1:
                            chosen_sex = adapterView.getSelectedItem().toString();
                            sex_num = 0;
                            break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView arg0) {}
        };

        // register to spinner listener
        genderSpinner.setOnItemSelectedListener(spinnerlistener);

        // set text with sex if current profile not empty
        if (curProfile.getSex().equals("null") == false) {
            // set specific position to spinner
            if (curProfile.getSex().equals("Male")) {
                genderSpinner.setSelection(0);
                sex_num = 1;
            } else if (curProfile.getSex().equals("Female")) {
                genderSpinner.setSelection(1);
                sex_num = 0;
            }

            // set to temporary storage
            chosen_sex = curProfile.getSex();

        }
    }

    // process relative edit text
    private void processEditTextControllers() {
        height_text = (EditText)findViewById(R.id.height_edit_text);
        weight_text = (EditText)findViewById(R.id.weight_edit_text);

        // set text to edit text if current profile not empty
        if (curProfile.getHeight() != 0 && curProfile.getWeight() != 0) {
            // set to edit text
            height_text.setText(Float.toString(curProfile.getHeight()));
            weight_text.setText(Float.toString(curProfile.getWeight()));
        }
    }

    // process BMI text view
    private void processTextViewControllers() {
        BMI_text =   (TextView)findViewById(R.id.BMI);
        BMR_text =   (TextView)findViewById(R.id.BMR);
        // display BMI if current profile not empty
        if (curProfile.getHeight() != 0 && curProfile.getWeight() != 0) {
            BMI = calculate_BMI(Float.toString(curProfile.getHeight()), Float.toString(curProfile.getWeight()));
            BMI_text.setText(Float.toString(BMI));


            BMR =calculate_BMR(Float.toString(curProfile.getHeight()), Float.toString(curProfile.getWeight()), sex_num , age_num);
            BMR_text.setText(Float.toString(BMR));
        }
    }

    private float calculate_BMI(String s_height, String s_weight){
        float height = Float.valueOf(s_height);       // 計算的時候，型別要一致才不會導致計算錯誤
        float weight = Float.valueOf(s_weight);      // 雖然某些計算值可以為 int 例如體重，但如果體重 weight 你給 int 型別會導致計算上的錯誤
        float bmi;
        height = height / 100 ;                                 // 將公分的身高轉為公尺單位
        bmi = weight / (height*height);
        return bmi;
    }

    private float calculate_BMR(String s_height, String s_weight, int sex, int age){
        float height = Float.valueOf(s_height);       // 計算的時候，型別要一致才不會導致計算錯誤
        float weight = Float.valueOf(s_weight);      // 雖然某些計算值可以為 int 例如體重，但如果體重 weight 你給 int 型別會導致計算上的錯誤
        float bmr;
        // 0 female  , 1 male
        if(sex == 0){
            bmr = (float)((9.6 * weight)+(1.8*height)-(4.7*age)+655);
        } else {
            bmr = (float)((13.7 * weight)+(5*height)-(6.8*age)+66);
        }
        return bmr;
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
            // set the update time and last modify time
            tempProfile.setDatetime(new Date().getTime());
            tempProfile.setLastModify(new Date().getTime());

            // set birthday to calendar class
            calendar.set(Calendar.YEAR, birth_year);
            calendar.set(Calendar.MONTH, birth_month - 1);
            calendar.set(Calendar.DAY_OF_MONTH, birth_day);

            // set time in millis
            tempProfile.setBirthDay(calendar.getTimeInMillis());

            // set gender, height, weight
            tempProfile.setSex(chosen_sex);
            tempProfile.setHeight(Float.parseFloat(height_text.getText().toString()));
            tempProfile.setWeight(Float.parseFloat(weight_text.getText().toString()));

            // store to my profile data base
            myProfileDAO.insert(tempProfile);

            // output test for birthday time in millis
            //System.out.println("birth-in-date " + String.format(Locale.getDefault(), "%tF  %<tR", new Date(calendar.getTimeInMillis())));

            // calculate BMI
            BMI = calculate_BMI(height_text.getText().toString(), weight_text.getText().toString());
            BMI_text.setText(Float.toString(BMI));

            //calculate BMR
            BMR =calculate_BMR(Float.toString(curProfile.getHeight()), Float.toString(curProfile.getWeight()), sex_num , age_num);
            BMR_text.setText(Float.toString(BMR));
        }
    }
}
