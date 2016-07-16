package com.example.nthucs.prototype.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.nthucs.prototype.R;

/**
 * Created by user on 2016/7/16.
 */
public class SettingsActivity extends AppCompatActivity {

    // action number for every activity
    private static final int SCAN_FOOD = 2;
    private static final int TAKE_PHOTO = 3;
    private static final int CALENDAR = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }
}
