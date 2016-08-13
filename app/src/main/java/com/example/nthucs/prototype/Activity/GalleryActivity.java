package com.example.nthucs.prototype.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.nthucs.prototype.AsyncTask.AsyncTaskConnect;
import com.example.nthucs.prototype.AsyncTask.AsyncTaskJsoup;
import com.example.nthucs.prototype.FoodList.CalorieDAO;
import com.example.nthucs.prototype.FoodList.FoodCal;
import com.example.nthucs.prototype.SpinnerWheel.SpinnerWheelAdapter;
import com.example.nthucs.prototype.Utility.FileUtil;
import com.example.nthucs.prototype.FoodList.Food;
import com.example.nthucs.prototype.R;
import com.example.nthucs.prototype.antistatic.spinnerwheel.AbstractWheel;
import com.example.nthucs.prototype.antistatic.spinnerwheel.adapters.NumericWheelAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by NTHUCS on 2016/7/1.
 */

public class GalleryActivity extends AppCompatActivity {

    // 讀取外部儲存設備授權請求代碼
    private static final int REQUEST_READ_EXTERNAL_STORAGE_PERMISSION = 101;
    private static final int SELECT_FILE = 1;

    // Picture's original name and image view
    private String fileName;
    private ImageView picture;

    // Picture's file, uri, urlLink;
    private File picFile;
    private Uri picUri;
    private String imageUrl;
    private String picUriString;

    // Search by word
    private String resultText;

    // Food storage
    private Food food;

    // food cal list, only from main activity
    private List<FoodCal> foodCalList = new ArrayList<>();

    // cal list data
    private static final String calDATA = "foodCalList";

    // data base for storing calorie data
    private CalorieDAO calorieDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_gallery);

        Intent intent = getIntent();
        String action = intent.getAction();

        // 取得顯示照片的ImageView元件
        picture = (ImageView) findViewById(R.id.picture);

        food = new Food(resultText, fileName, picUriString, false);

        if (action.equals("com.example.nthucs.prototype.TAKE_PHOTO")) {
            // new food
            requestStoragePermission();

            // calorie data base
            calorieDAO = new CalorieDAO(getApplicationContext());

            // get all data
            foodCalList = calorieDAO.getAll();

            // original pass way, already deprecated
            //foodCalList = getIntent().getParcelableArrayListExtra(calDATA);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE) {
                onSelectFromGalleryResult(data);
            }
        }
    }

    // 覆寫請求授權後執行的方法
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_READ_EXTERNAL_STORAGE_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                galleryIntent();
            } else {
                Toast.makeText(this, R.string.read_external_storage_denied,
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void onSubmit(View view) {
        if (view.getId() == R.id.search_item) {
            // Use Async Task to open httpUrlConnection for upload picture
            String responseString = new String();

            // Use Async Task
            try{
                AsyncTaskConnect asyncTaskConnect = new AsyncTaskConnect(picFile, getImagePath(picUri), GalleryActivity.this);
                responseString =  asyncTaskConnect.execute().get();
            } catch (InterruptedException e) {
                System.out.println("Interrupted exception");
            } catch (ExecutionException e) {
                System.out.println("Execution exception");
            }

            // Parse response string
            imageUrl = getParseString(responseString, "data", "img_url");

            // output test
            System.out.println(imageUrl);

            // Use Async Task to retrieve data from google image search result with Jsoup
            String resultString = new String();

            // Use Async Task
            try{
                AsyncTaskJsoup asyncTaskJsoup = new AsyncTaskJsoup(imageUrl);
                resultString = asyncTaskJsoup.execute().get();
            } catch (InterruptedException e) {
                System.out.println("Interrupted exception");
            } catch (ExecutionException e) {
                System.out.println("Execution exception");
            }

            // Get the result text from the response string
            resultText = resultString;

            // Compare Food Cal DAO to get calorie
            int[] compare_result = compareFoodCalDB(resultText);

            // Process dialog with spinner wheel
            processDialogControllers(compare_result);

            // output test
            System.out.println("Suggested result: " + resultText);
        } else if (view.getId() == R.id.cancel_item) {
            finish();
        }
    }

    private void requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int hasPermission = checkSelfPermission(
                    Manifest.permission.READ_EXTERNAL_STORAGE);

            if (hasPermission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_READ_EXTERNAL_STORAGE_PERMISSION);
                return;
            }
        }

        galleryIntent();
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    private void onSelectFromGalleryResult(Intent data) {
        Bitmap bitmap = null;
        if (data != null) {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // the address of the image on the SD card
        Uri uri = data.getData();

        // test for different storage
        //System.out.println(uri);
        //System.out.println(getRealPathFromURI(uri));

        // uri is from external media
        if (uri.getPath().toLowerCase().contains("external")) {
            // fix bug with invalid extension from passing true picUri
            String realPath = getRealPathFromURI(uri);
            picUri = Uri.parse(realPath);
            picFile = new File(realPath);
            fileName = FileUtil.getUniqueFileName();
        // uri is from real path, like: sdcard
        } else {
            picUri = uri;
            picFile = new File(uri.getPath());
            fileName = picFile.getName().substring(1, 15);
        }

        // assign variable: picUri.toString
        picUriString = picUri.toString();

        // set bitmap to imageView
        picture.setImageBitmap(bitmap);
        picture.setVisibility(View.VISIBLE);
    }

    private String getImagePath(Uri paramUri) {
        return paramUri.getPath();
    }

    private String getParseString(String jsonStr, String target1, String target2) {
        String imageUrl = new String();
        try {
            // Get Json object twice with two target
            JSONObject jsonObject = new JSONObject(jsonStr);
            JSONObject data = jsonObject.getJSONObject(target1);

            // add /t/ to get tinny picture link
            String originUrl = data.getString(target2);
            int idx = 0;
            for (int  i = 0 ; i < originUrl.length()-1 ; i++) {
                if (originUrl.charAt(i) == '/' && originUrl.charAt(i+1) != '/')
                    idx = i;
            }
            imageUrl = originUrl.substring(0, idx) + "/t/" + originUrl.substring(idx+1, originUrl.length());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return imageUrl;
    }

    // if uri is media external format, get the real path from this uri
    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        // Source is Dropbox or other similar local file path
        if (cursor == null) {
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    // find food title in food calorie data base
    private int[] compareFoodCalDB(String resultText) {

        // return with integer array
        int[] compare_result = new int[]{};

        // temporary test
        //String[] compare_result = {"", Float.toString(0.0f)};

        // if origin result text is null
        if (resultText == null || resultText.isEmpty() == true) {
            return null;
        }

        // split result with space
        String[] splitText = resultText.split("\\s+");

        // whether the string is english
        boolean isEnglishString = true;

        // string for build chinese character
        String chineseResultText = new String();

        // record the number of result to integer array
        int[] arrayCount = new int[splitText.length];

        for (int i = 0 ; i < splitText.length ; i++) {

            // traversal split string
            for (int j = 0 ; j < splitText[i].length() ; j++) {
                if ((splitText[i].charAt(j) >= 65 && splitText[i].charAt(j) <= 90)
                        || (splitText[i].charAt(j) >= 97 && splitText[i].charAt(j) <= 122)) {
                    isEnglishString = true;
                } else {
                    isEnglishString = false;
                    break;
                }
            }

            // compare every split english string
            if (isEnglishString == true) {
                for (int j = 0 ; j < foodCalList.size() ; j++) {
                    if (splitText[i].toLowerCase().contains(foodCalList.get(j).getEnglishName().toLowerCase())
                            && foodCalList.get(j).getEnglishName().isEmpty() == false) {
                        // count total number
                        arrayCount[i]++;
                    }
                }

                if (arrayCount[i] != 0) {
                    compare_result = new int[arrayCount[i]];
                }
                arrayCount[i] = 0;

                for (int j = 0 ; j < foodCalList.size() ; j++) {
                    if (splitText[i].toLowerCase().contains(foodCalList.get(j).getEnglishName().toLowerCase())
                            && foodCalList.get(j).getEnglishName().isEmpty() == false) {
                        compare_result[arrayCount[i]] = j;
                        arrayCount[i]++;
                    }
                }
            // merge chinese sub-string
            } else {
                chineseResultText += splitText[i];
            }
        }

        // compare merged chinese string with food cal
        if (isEnglishString == false) {
            for (int i = 0 ; i < foodCalList.size() ; i++) {
                if (foodCalList.get(i).getChineseName().contains(chineseResultText)) {
                    // count total number
                    arrayCount[0]++;
                }
            }

            compare_result = new int[arrayCount[0]];
            arrayCount[0] = 0;

            for (int i = 0 ; i < foodCalList.size() ; i++) {
                if (foodCalList.get(i).getChineseName().contains(chineseResultText)) {
                    compare_result[arrayCount[0]] = i;
                    arrayCount[0]++;
                }
            }
        }

        // if still not result, return original text
        /*if (compare_result[0].isEmpty() == true) {
            compare_result[0] = resultText;
            compare_result[1] = Float.toString(0.0f);
        }*/

        for (int i = 0 ; i < compare_result.length ; i++) {
            System.out.println("name: " + foodCalList.get(compare_result[i]).getChineseName()
                    + " calorie: " + foodCalList.get(compare_result[i]).getCalorie());
        }

        return compare_result;
    }

    // dialog with spinner wheel to choose food name & calorie
    private void processDialogControllers(final int[] compare_result) {

        // combine result string with chinese name & food calorie according to food calorie data base's index
        String[] compare_string = new String[compare_result.length];
        for (int i = 0 ; i < compare_result.length ; i++) {
            compare_string[i] = foodCalList.get(compare_result[i]).getChineseName() + ": " +
                    Float.toString(foodCalList.get(compare_result[i]).getCalorie());
        }

        // custom dialog
        final Dialog dialog = new Dialog(GalleryActivity.this);
        dialog.setCancelable(false);
        dialog.setTitle("Choose the food");
        dialog.setContentView(R.layout.custom_dialog);

        // set the custom dialog components
        AbstractWheel dialogSpinner = (AbstractWheel) dialog.findViewById(R.id.spinner_wheel);
        Button dialogButton = (Button) dialog.findViewById(R.id.dialog_button);

        dialogSpinner.setViewAdapter(new SpinnerWheelAdapter(GalleryActivity.this, R.layout.spinner_wheel_item, compare_string));
        dialogSpinner.setCyclic(true);

        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Set food's information(title and picture name)
                food.setTitle("test");
                food.setContent("blank content");
                food.setFileName(fileName);
                food.setCalorie(0.0f);
                food.setGrams(100.0f);
                food.setPortions(1.0f);
                food.setPicUriString(picUriString);
                food.setTakeFromCamera(false);
                food.setDatetime(new Date().getTime());

                Intent result = getIntent();
                result.putExtra("com.example.nthucs.prototype.FoodList.Food", food);
                setResult(Activity.RESULT_OK, result);

                dialog.dismiss();
                finish();
            }
        });

        // show dialog
        dialog.show();
    }
}
