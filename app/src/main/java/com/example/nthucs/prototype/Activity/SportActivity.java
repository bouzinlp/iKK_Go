package com.example.nthucs.prototype.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.nthucs.prototype.R;
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
    private Button dialogTitleButton;

    // text input
    //private EditText title_text;
    private EditText content_text, calorie_text, hour_text, minute_text;

    // sport information
    private Sport sport;

    // sport csv reader
    private CSVReader sportCalReader;

    // list of sportCal
    private List<SportCal> sportCalList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sport);

        // open sport csv
        try {
            openSportCalCsv();
        } catch (IOException e) {
            System.out.println("open sport cal: IO exception");
        }

        // process dialog button for title
        processDialogButtonControllers();

        //title_text = (EditText)findViewById(R.id.title_text);
        content_text = (EditText)findViewById(R.id.content_text);
        calorie_text = (EditText)findViewById(R.id.calorie_text);
        hour_text = (EditText)findViewById(R.id.hour_text);
        minute_text = (EditText)findViewById(R.id.minute_text);

        Intent intent = getIntent();
        String action = intent.getAction();

        if (action.equals("com.example.nthucs.prototype.EDIT_SPORT")) {
            sport = (Sport)intent.getExtras().getSerializable(
                    "com.example.nthucs.prototype.SportList.Sport");

            dialogTitleButton.setText(sport.getTitle());

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
            String titleText = dialogTitleButton.getText().toString();
            String contentText = content_text.getText().toString();
            String calorieText = calorie_text.getText().toString();
            String hourText = hour_text.getText().toString();
            String minuteText = minute_text.getText().toString();

            long totalTime = TimeUnit.HOURS.toMillis(Integer.parseInt(hourText))
                                + TimeUnit.MINUTES.toMillis(Integer.parseInt(minuteText));

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
        }
        finish();
    }

    // open sport calories csv
    private void openSportCalCsv() throws IOException {
        // Build reader instance
        sportCalReader = new CSVReader(new InputStreamReader(getAssets().open("sports_cal.csv")));

        // Read all rows at once
        ArrayList<String[]> allRows= (ArrayList)sportCalReader.readAll();

        // Read CSV line by line
        for (int i = 1; i < allRows.size() ; i++) {
            SportCal sportCal = new SportCal();

            sportCal.setSportName(allRows.get(i)[0]);
            sportCal.setConsumeHalfHouWith40(Float.parseFloat(allRows.get(i)[1]));
            sportCal.setConsumeHalfHouWith50(Float.parseFloat(allRows.get(i)[2]));
            sportCal.setConsumeHalfHouWith60(Float.parseFloat(allRows.get(i)[3]));
            sportCal.setConsumeHalfHouWith70(Float.parseFloat(allRows.get(i)[4]));

            sportCalList.add(sportCal);
        }
    }

    // process dialog button controllers
    private void processDialogButtonControllers() {
        // initialize dialog button
        dialogTitleButton = (Button)findViewById(R.id.dialog_button);

        // avoid all upper case
        dialogTitleButton.setTransformationMethod(null);

        // set button listener
        dialogTitleButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                // process custom dialog
                CustomDialogForSport customDialogForSport = new CustomDialogForSport(sportCalList, SportActivity.this);
                customDialogForSport.processDialogControllers();
            }
        });
    }
}
