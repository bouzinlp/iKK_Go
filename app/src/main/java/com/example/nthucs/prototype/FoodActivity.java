package com.example.nthucs.prototype;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

public class FoodActivity extends AppCompatActivity {

    private EditText calorie, protions, grams, title_text, content_text;

    private Food food;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    }
}
