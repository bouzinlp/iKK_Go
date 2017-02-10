package com.example.nthucs.prototype.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.nthucs.prototype.AsyncTask.AsyncTaskConnect;
import com.example.nthucs.prototype.AsyncTask.AsyncTaskJsoup;
import com.example.nthucs.prototype.FoodList.CalorieDAO;
import com.example.nthucs.prototype.FoodList.FoodCal;
import com.example.nthucs.prototype.SpinnerWheel.CustomDialog;
import com.example.nthucs.prototype.Utility.CompFoodDB;
import com.example.nthucs.prototype.Utility.FileUtil;
import com.example.nthucs.prototype.FoodList.Food;
import com.example.nthucs.prototype.R;
import com.example.nthucs.prototype.Utility.RealPathUtil;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
    private String realPath;

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

    RequestParams params = new RequestParams();
    String encodedString,recordedDate;
    Bitmap bitmaptoUpload;

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
                System.out.println("REAL PATH = "+realPath);
                System.out.println("FILE = "+picFile);
                AsyncTaskConnect asyncTaskConnect = new AsyncTaskConnect(picFile, realPath, GalleryActivity.this);
                responseString =  asyncTaskConnect.execute().get();
            } catch (InterruptedException e) {
                System.out.println("Interrupted exception");
            } catch (ExecutionException e) {
                System.out.println("Execution exception");
            }

            //System.out.println("response = "+responseString);
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
            CompFoodDB compFoodDB = new CompFoodDB(resultText, foodCalList);
            int[] compare_result = compFoodDB.compareFoodCalDB();

            // output test
            System.out.println("Suggested result: " + resultText);

            // if the compare result is empty
            if (compare_result.length == 0) {
                // Process normal food event
                processFoodEvent();
            } else {
                // Process dialog with spinner wheel
                CustomDialog customDialog = new CustomDialog(compare_result, food, foodCalList,
                                                            fileName, picUriString, GalleryActivity.this);
                customDialog.processDialogControllers();
            }
        } else if (view.getId() == R.id.cancel_item) {
            finish();
        }

        new encodeImagetoString().execute();

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
                bitmaptoUpload = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // SDK < API11
        if (Build.VERSION.SDK_INT < 11)
            realPath = RealPathUtil.getRealPathFromURI_BelowAPI11(this, data.getData());
            // SDK >= 11 && SDK < 19
        else if (Build.VERSION.SDK_INT < 19)
            realPath = RealPathUtil.getRealPathFromURI_API11to18(this, data.getData());
            // SDK > 19 (Android 4.4)
        else
            realPath = RealPathUtil.getRealPathFromURI_API19(this, data.getData());

        // the address of the image on the SD card
        Uri uri = data.getData();

        // test for different storage
        //System.out.println("img uri = "+uri);
        //System.out.println("img path = "+getRealPathFromURI(uri));

        // uri is from external media
        if (uri.getPath().toLowerCase().contains("external")) {
            // fix bug with invalid extension from passing true picUri
            picUri = Uri.parse(realPath);
            picFile = new File(realPath);
            fileName = FileUtil.getUniqueFileName();
        // uri is from real path, like: sdcard
        } else {
            picUri = Uri.fromFile(new File(realPath));
            picFile = new File(realPath);
            fileName = picFile.getName();
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


    // Process normal food event if cannot match result from data base
    private void processFoodEvent() {

        // original set to food event
        food.setTitle(resultText);
        food.setContent("blank content");
        food.setFileName(fileName);
        food.setCalorie(0.0f);
        food.setGrams(100.0f);
        food.setPortions(1.0f);
        food.setPicUriString(picUriString);
        food.setTakeFromCamera(false);
        food.setDatetime(new Date().getTime());

        // back to main activity
        Intent result = getIntent();
        result.putExtra("com.example.nthucs.prototype.FoodList.Food", food);
        setResult(Activity.RESULT_OK, result);
        finish();
    }

    private class encodeImagetoString extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {


            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            // Must compress the Image to reduce image size to make upload easy
            bitmaptoUpload.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] byte_arr = stream.toByteArray();
            // Encode Image to String
            encodedString = Base64.encodeToString(byte_arr, Base64.DEFAULT);
            recordedDate = new SimpleDateFormat("yyyyMMdd").format(new Date());

            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            // Put converted Image string into Async Http Post param
            params.put("image", encodedString);
            params.put("filename",recordedDate+"_"+fileName);
            params.put("userFBId",LoginActivity.facebookUserID);
            // Trigger Image upload
            triggerImageUpload();

        }
    }

    public void triggerImageUpload() {
        makeHTTPCall();
    }

    // Make Http call to upload Image to Php server
    public void makeHTTPCall() {
        AsyncHttpClient client = new AsyncHttpClient();
        // Don't forget to change the IP address to your LAN address. Port no as well.
        client.post("http://140.114.88.136:80/mhealth/upload_image.php",
                params, new AsyncHttpResponseHandler() {
                    // When the response returned by REST has Http
                    // response code '200'

                    public void onSuccess(int status, cz.msebera.android.httpclient.Header[] headers, byte[] bytes) {

                        try {
                            String str = new String(bytes,"UTF-8");
                            System.out.println(str);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                    // When the response returned by REST has Http
                    // response code other than '200' such as '404',
                    // '500' or '403' etc
                    public void onFailure(int status, cz.msebera.android.httpclient.Header[] headers, byte[] bytes, Throwable throwable) {
                        // Hide Progress Dialog
                        // When Http response code is '404'
                        if (status == 404) {
                            Toast.makeText(getApplicationContext(),
                                    "Requested resource not found",
                                    Toast.LENGTH_LONG).show();
                        }
                        // When Http response code is '500'
                        else if (status == 500) {
                            Toast.makeText(getApplicationContext(),
                                    "Something went wrong at server end",
                                    Toast.LENGTH_LONG).show();
                        }
                        // When Http response code other than 404, 500
                        else {
                            Toast.makeText(
                                    getApplicationContext(),
                                    "Error Occured n Most Common Error: n1. Device not connected to Internetn2. Web App is not deployed in App servern3. App server is not runningn HTTP Status code : "
                                            + status, Toast.LENGTH_LONG)
                                    .show();
                        }
                    }
                });
    }


}
