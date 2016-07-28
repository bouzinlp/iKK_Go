package com.example.nthucs.prototype.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.example.nthucs.prototype.R;
import com.example.nthucs.prototype.SportList.Sport;

/**
 * Created by user on 2016/7/27.
 */
public class SportActivity extends AppCompatActivity {

    private EditText title_text, content_text, calorie_text, totalTime_text;

    // sport information
    private Sport sport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sport);

        title_text = (EditText)findViewById(R.id.title_text);
        content_text = (EditText)findViewById(R.id.content_text);
        calorie_text = (EditText)findViewById(R.id.calorie_text);
        totalTime_text = (EditText)findViewById(R.id.total_time_text);

        Intent intent = getIntent();
        String action = intent.getAction();

        if (action.equals("com.example.nthucs.prototype.EDIT_SPORT")) {
            sport = (Sport)intent.getExtras().getSerializable(
                    "com.example.nthucs.prototype.SportList.Sport");

            title_text.setText(sport.getTitle());
            content_text.setText(sport.getContent());
            calorie_text.setText(Float.toString(sport.getCalorie()));
            //totalTime_text.setText(Integer.toString(sport.getTotalTime()));
        } else if (action.equals("com.example.nthucs.prototype.ADD_FOOD")) {
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

        }
        finish();
    }

}
