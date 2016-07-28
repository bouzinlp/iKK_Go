package com.example.nthucs.prototype.Activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.nthucs.prototype.Utility.FileUtil;
import com.example.nthucs.prototype.FoodList.Food;
import com.example.nthucs.prototype.R;

import java.io.File;
import java.util.Date;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;

public class FoodActivity extends AppCompatActivity {

    private EditText title_text, content_text, calorie_text, portions_text, grams_text;

    // food information
    private Food food;

    // picture information
    private String fileName;
    private ImageView picture;

    // pass Uri's toString if take photo from library
    private String picUriString;
    private Uri picUri;

    //facebook share dialog
    private ShareDialog shareDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food);

        FacebookSdk.sdkInitialize(getApplicationContext());
        shareDialog = new ShareDialog(this);

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
                    "com.example.nthucs.prototype.FoodList.Food");
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

        System.out.println("date time: "+food.getLocaleDatetime());

        if (food.getFileName() != null && food.getFileName().length() > 0) {
            // camera can access this statement
            if (food.isTakeFromCamera() == true) {
                // photo taken from camera display with config way
                File file = configFileName("P", ".jpg");
                if (file.exists()) {
                    // 顯示照片元件
                    picture.setVisibility(View.VISIBLE);
                    // 設定照片
                    FileUtil.fileToImageView(file.getAbsolutePath(), picture);
                }
            // gallery can access this statement
            } else {
                // photo taken from gallery display with parsing uri
                picUriString = food.getPicUriString();

                picUri = Uri.parse(picUriString);
                File file2 = new File(picUri.getPath());

                if (file2.exists()) {
                    // 顯示照片元件
                    picture.setVisibility(View.VISIBLE);
                    // 設定照片
                    FileUtil.fileToImageView(file2.getAbsolutePath(), picture);
                }
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

            // if add food with photo, then also record establish time
            if (getIntent().getAction().equals("com.example.nthucs.prototype.ADD_FOOD")) {
                food.setDatetime(new Date().getTime());
            }

            Intent result = getIntent();
            result.putExtra("com.example.nthucs.prototype.FoodList.Food", food);
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.food_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void shareToFB(MenuItem menuItem){
        try {

            File file = configFileName("P", ".jpg");
            Bitmap image = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(file));
            SharePhoto photo = new SharePhoto.Builder()
                    .setBitmap(image)
                    .build();
            SharePhotoContent content = new SharePhotoContent.Builder()
                    .addPhoto(photo)
                    .build();


            if(shareDialog.canShow(SharePhotoContent.class)){
                shareDialog.show(content);
                System.out.println("SET");
            }
            else{
                System.out.println("U CANT");
            }

        }
        catch (Exception e){
            System.out.println("EXCEPTION : "+e);
        }
    }
}
