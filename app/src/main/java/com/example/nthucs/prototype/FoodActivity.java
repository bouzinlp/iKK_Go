package com.example.nthucs.prototype;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.File;

public class FoodActivity extends AppCompatActivity {

    private EditText title_text, content_text, calorie_text, portions_text, grams_text;

    // food information
    private Food food;

    // picture information
    private String fileName;
    private ImageView picture;

    // pass Uri's toString if take photo from library
    private String picUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food);

        title_text = (EditText)findViewById(R.id.title_text);
        content_text = (EditText)findViewById(R.id.content_text);
        calorie_text = (EditText)findViewById(R.id.calorie_text);
        portions_text = (EditText)findViewById(R.id.portions_text);
        grams_text = (EditText)findViewById(R.id.grams_text);

        picture = (ImageView) findViewById(R.id.picture_food);

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
        } else if (action.equals("com.example.nthucs.prototype.ADD_FOOD")) {
            food = new Food();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        fileName = food.getFileName();

        if (food.getFileName() != null && food.getFileName().length() > 0) {

            File file = configFileName("P", ".jpg");
            System.out.println("!!! "+file.getName());
            System.out.println("### "+fileName);

            // camera can access this statement
            if (file.exists()) {
                System.out.println("@@@ "+fileName);
                // 顯示照片元件
                picture.setVisibility(View.VISIBLE);
                // 設定照片
                FileUtil.fileToImageView(file.getAbsolutePath(), picture);
            // temporary
            } else {
                picUri = food.getPicUri();

                /*file = new File(Uri.parse(picUri));
                if (file.exists()) {
                    System.out.println("@@@ "+fileName);
                }*/
            }
        }
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

    private File configFileName(String prefix, String extension) {
        if (fileName == null) {
            fileName = FileUtil.getUniqueFileName();
        }

        return new File(FileUtil.getExternalStorageDir(FileUtil.APP_DIR),
                prefix + fileName + extension);
    }

    public void clickFunction(View view) {

    }
}
