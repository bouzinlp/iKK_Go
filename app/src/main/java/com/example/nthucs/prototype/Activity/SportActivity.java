package com.example.nthucs.prototype.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.nthucs.prototype.R;

/**
 * Created by user on 2016/7/27.
 */
public class SportActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sport);


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
