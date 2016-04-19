package com.example.nthucs.prototype;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

public class FoodActivity extends AppCompatActivity {

    private EditText title_text, content_text, calorie_text, portions_text, grams_text;

    private Food food;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food);

        title_text = (EditText)findViewById(R.id.title_text);
        content_text = (EditText)findViewById(R.id.content_text);
        calorie_text = (EditText)findViewById(R.id.calorie_text);
        portions_text = (EditText)findViewById(R.id.portions_text);
        grams_text = (EditText)findViewById(R.id.grams_text);

        Intent intent = getIntent();
        String action = intent.getAction();

        if (action.equals("com.example.nthucs.prototype.EDIT_FOOD")) {
            food = (Food)intent.getExtras().getSerializable(
                    "com.example.nthucs.prototype.Food");
            title_text.setText(food.getTitle());
            content_text.setText(food.getContent());
            calorie_text.setText(Float.toString(food.getCalorie()));
            portions_text.setText(Float.toString(food.getPortions()));
            grams_text.setText(Float.toString(food.getGrams()));
        } else
            food = new Food();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {


        }
    }

    public void onSubmit(View view) {
        if (view.getId() == R.id.ok_item) {
            String titleText = title_text.getText().toString();
            String contentText = content_text.getText().toString();
            String calorieText = calorie_text.getText().toString();
            String portionsText = portions_text.getText().toString();
            String gramsText = grams_text.getText().toString();

            food.setTitle(titleText);
            food.setContent(contentText);
            food.setCalorie(Float.parseFloat(calorieText));
            food.setPortions(Float.parseFloat(portionsText));
            food.setGrams(Float.parseFloat(gramsText));

            Intent result = getIntent();
            result.putExtra("com.example.nthucs.prototype.Food", food);
            setResult(Activity.RESULT_OK, result);
        }
        finish();
    }

    public void clickFunction(View view) {

    }
}
