package com.example.nthucs.prototype.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.nthucs.prototype.R;
import com.example.nthucs.prototype.Settings.MyProfileDAO;
import com.example.nthucs.prototype.Settings.Profile;
import com.example.nthucs.prototype.SpinnerWheel.CustomDialogForSport;
import com.example.nthucs.prototype.SportList.Sport;
import com.example.nthucs.prototype.SportList.SportCal;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import au.com.bytecode.opencsv.CSVReader;

/**
 * Created by user on 2016/7/27.
 */
public class SportActivity extends AppCompatActivity {

    // dialog for choosing sport within spinner wheel
    private EditText dialogTitleEditText;

    // text input
    //private EditText title_text;
    private EditText content_text, calorie_text, hour_text, minute_text;

    // sport information
    private Sport sport;

    // sport csv reader
    private CSVReader sportCalReader;

    // list of sportCal
    private List<SportCal> sportCalList = new ArrayList<>();

    // data base for profile
    private MyProfileDAO myProfileDAO;

    // list of profile & current weight
    private List<Profile> profileList = new ArrayList<>();
    private float currentWeight;

    private ArrayList<String[]> allRows;
    private String beforeInput = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sport);

        // initialize data base
        myProfileDAO = new MyProfileDAO(getApplicationContext());

        // get all profile data from data base
        profileList = myProfileDAO.getAll();

        // ask user to finish profile if table is empty
        if (myProfileDAO.isTableEmpty() == true) {
            AlertDialog.Builder builder = new AlertDialog.Builder(SportActivity.this);
            builder.setTitle("Information").setMessage("Please fill profile first");
            builder.setPositiveButton(android.R.string.yes, null);
            builder.show();
        } else {
            currentWeight = profileList.get(profileList.size()-1).getWeight();
        }

        // open sport csv
        try {
            openSportCalCsv();
        } catch (IOException e) {
            System.out.println("open sport cal: IO exception");
        }

        // process dialog button for title
        processDialogButtonControllers();

        // process edit text controllers
        processEditTextControllers();

        Intent intent = getIntent();
        String action = intent.getAction();

        if (action.equals("com.example.nthucs.prototype.EDIT_SPORT")) {
            sport = (Sport)intent.getExtras().getSerializable(
                    "com.example.nthucs.prototype.SportList.Sport");

            dialogTitleEditText.setText(sport.getTitle());

            //title_text.setText(sport.getTitle());
            content_text.setText(sport.getContent());
            calorie_text.setText(Float.toString(sport.getCalorie()));
            hour_text.setText(String.valueOf(sport.getTotalTime()
                                            / (1000 * 60 * 60) % 24));
            minute_text.setText(String.valueOf(sport.getTotalTime()
                                            / (1000 * 60) % 60));

        } else if (action.equals("com.example.nthucs.prototype.ADD_SPORT")) {
            sport = new Sport();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
        }
    }

    public void onSubmit(View view) {
        if (view.getId() == R.id.ok_item) {
            String titleText = dialogTitleEditText.getText().toString();
            String contentText = content_text.getText().toString();
            String calorieText = calorie_text.getText().toString();
            String hourText = hour_text.getText().toString();
            String minuteText = minute_text.getText().toString();

            long totalTime = TimeUnit.HOURS.toMillis(Integer.parseInt(hourText))
                                + TimeUnit.MINUTES.toMillis(Integer.parseInt(minuteText));
            sport.setId(System.currentTimeMillis());
            sport.setUserID(Long.parseLong(LoginActivity.facebookUserID));
            sport.setTitle(titleText);
            sport.setContent(contentText);
            sport.setCalorie(Float.parseFloat(calorieText));
            sport.setTotalTime(totalTime);
            // add sport event first time
            if (getIntent().getAction().equals("com.example.nthucs.prototype.ADD_SPORT")) {
                sport.setDatetime(new Date().getTime());
            }

            // output test
            //System.out.println("hour: "+(totalTime/(1000*60*60)%24)+" minute: "+(totalTime/(1000*60)%60));

            Intent result = getIntent();
            result.putExtra("com.example.nthucs.prototype.SportList.Sport", sport);
            setResult(Activity.RESULT_OK, result);
            finish();
        }
        else if(view.getId() == R.id.go_search){
            sportCalList.clear();
            try{
                // Read CSV line by line
                for (int index = 1; index < allRows.size() ; index++) {
                    if(allRows.get(index)[0].contains(dialogTitleEditText.getText().toString()) ||
                            allRows.get(index)[6].contains(dialogTitleEditText.getText().toString())){
                        SportCal sportCal = new SportCal();
                        sportCal.setSportName(allRows.get(index)[0]);
                        sportCal.setConsumeHalfHouWith40(Float.parseFloat(allRows.get(index)[1]));
                        sportCal.setConsumeHalfHouWith50(Float.parseFloat(allRows.get(index)[2]));
                        sportCal.setConsumeHalfHouWith60(Float.parseFloat(allRows.get(index)[3]));
                        sportCal.setConsumeHalfHouWith70(Float.parseFloat(allRows.get(index)[4]));
                        sportCal.setClassification((allRows.get(index)[5]));
                        sportCal.setActivityEN((allRows.get(index)[6]));
                        sportCal.setGroup((allRows.get(index)[7]));
                        sportCal.setConsumeUnit(Float.parseFloat(allRows.get(index)[8]));
                        sportCalList.add(sportCal);
                    }
                }
            }catch (Exception e){
                System.out.println(e);
            }
            if(sportCalList.size()!=0){
                CustomDialogForSport customDialogForSport = new CustomDialogForSport(sportCalList, SportActivity.this, currentWeight);
                customDialogForSport.processDialogControllers();
            }
        }
        else if(view.getId()==R.id.cancel_item)
            finish();
        //
    }

    // open sport calories csv
    private void openSportCalCsv() throws IOException {
        String next[] = {};
        List<String[]> list = new ArrayList<>();

        // Build reader instance
        sportCalReader = new CSVReader(new InputStreamReader(getAssets().open("sports_cal.csv")));

        // Read all rows at once
        allRows= (ArrayList)sportCalReader.readAll();
        // Read CSV line by line
        for (int i = 1; i < allRows.size() ; i++) {
            SportCal sportCal = new SportCal();
            sportCal.setSportName(allRows.get(i)[0]);
            sportCal.setConsumeHalfHouWith40(Float.parseFloat(allRows.get(i)[1]));
            sportCal.setConsumeHalfHouWith50(Float.parseFloat(allRows.get(i)[2]));
            sportCal.setConsumeHalfHouWith60(Float.parseFloat(allRows.get(i)[3]));
            sportCal.setConsumeHalfHouWith70(Float.parseFloat(allRows.get(i)[4]));
            sportCal.setClassification((allRows.get(i)[5]));
            sportCal.setActivityEN((allRows.get(i)[6]));
            sportCal.setGroup((allRows.get(i)[7]));
            sportCal.setConsumeUnit(Float.parseFloat(allRows.get(i)[8]));
            sportCalList.add(sportCal);
        }
    }

    // process dialog button controllers
    private void processDialogButtonControllers() {
        // initialize dialog button
        dialogTitleEditText = (EditText)findViewById(R.id.dialog_edittext);
        // initialize go button
        Button goSearchButton = (Button) findViewById(R.id.go_search);
        // avoid all upper case
        dialogTitleEditText.setTransformationMethod(null);
    }

    // process edit text controllers
    private void processEditTextControllers() {
        content_text = (EditText)findViewById(R.id.content_text);
        calorie_text = (EditText)findViewById(R.id.calorie_text);
        hour_text = (EditText)findViewById(R.id.hour_text);
        minute_text = (EditText)findViewById(R.id.minute_text);

    }

    // get dialog title EditText public
    public EditText getDialogTitleEditText() {
        return this.dialogTitleEditText;
    }

    // get calorie edit text public
    public EditText getCalorieText() {
        return this.calorie_text;
    }

    // get hour edit text public
    public EditText getHourText() {
        return this.hour_text;
    }

    // get minute edit text public
    public EditText getMinuteText() {
        return this.minute_text;
    }

    // get total time from hour & minute text
    public long getCurrentTime() {
        long currentTime;

        // in add event
        if (hour_text.getText().toString().isEmpty() == true && minute_text.getText().toString().isEmpty() == true) {
            currentTime = 0;
        } else {
            currentTime = TimeUnit.HOURS.toMillis(Integer.parseInt(hour_text.getText().toString()))
                    + TimeUnit.MINUTES.toMillis(Integer.parseInt(minute_text.getText().toString()));
        }

        return currentTime;
    }
}
