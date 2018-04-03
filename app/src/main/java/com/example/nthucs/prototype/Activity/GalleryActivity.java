package com.example.nthucs.prototype.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.Spinner;
import android.widget.ArrayAdapter;

import com.example.nthucs.prototype.FoodList.CalorieDAO;
import com.example.nthucs.prototype.FoodList.Food;
import com.example.nthucs.prototype.FoodList.FoodCal;
import com.example.nthucs.prototype.R;
import com.example.nthucs.prototype.SpinnerWheel.CustomDialog;
import com.example.nthucs.prototype.Utility.CompFoodDB;
import com.example.nthucs.prototype.Utility.FileUtil;
import com.example.nthucs.prototype.Utility.RealPathUtil;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;



/*For image recognition*/
import clarifai2.api.ClarifaiBuilder;
import clarifai2.api.ClarifaiClient;
import okhttp3.OkHttpClient;
import clarifai2.dto.input.ClarifaiInput;
import clarifai2.dto.model.output.ClarifaiOutput;
import clarifai2.dto.prediction.Concept;




/**
 * Created by NTHUCS on 2016/7/1.
 * Modified on 2018/1/13 by YuJui Chen
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

    //讓user選多種食物當中的一類
    List<String> food_choose_list = new ArrayList<String>();

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
    String encodedString;
    Bitmap bitmaptoUpload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_gallery);

        Intent intent = getIntent();
        String action = intent.getAction();

        //以下開始實做spinner的部分 Modified on 03/16
        //spinner = (Spinner)findViewById(R.id.spinner);

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

    //AsyncTask 3種型態分別為 傳入值 更新進度 結果
    private class ATC extends AsyncTask<String, Integer, String> {

        // URL upload
        private static final String SERVER_URL = "http://uploads.im/api?upload";

        // Http response
        private String responseString;

        // Picture
        File PicFile;
        String PicPath;

        GalleryActivity galleryActivity;

        // ProgressDialog
        private ProgressDialog uploadProgressDialog;
        private final CharSequence dialogTitle = "上傳中";
        private final CharSequence dialogMessage = "請等待上傳資料";

        public ATC(File picFile, String picPath, GalleryActivity galleryActivity) {
            this.PicFile = picFile;
            this.PicPath = picPath;
            this.galleryActivity = galleryActivity;
        }

        @Override
        protected void onPreExecute() {
            uploadProgressDialog = new ProgressDialog(galleryActivity);

            // set title, message & style
            uploadProgressDialog.setTitle(dialogTitle);
            uploadProgressDialog.setMessage(dialogMessage);

            // show dialog when uploading
            uploadProgressDialog.show();

            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String... urls) {

                try {
                    /*For image recognition , Implemented by YuJui Chen*/
                    //ReImplemented by YuJui Chen
                    /*利用線上clarifai來進行圖片分析 此處使用food model進行實作 */
                    ClarifaiClient client_clarifi = new ClarifaiBuilder("c3064802a10e4254bd714f7e121e2c99")
                            .client(new OkHttpClient()) // OPTIONAL. Allows customization of OkHttp by the user
                            .buildSync();

                    /*predict 每張圖片裡面的內容 從中選取機率最大的*/
                    final List<ClarifaiOutput<Concept>> predictionResults =
                            client_clarifi.getDefaultModels().foodModel() // You can also do client.getModelByID("id") to get your custom models
                                    .predict()
                                    .withInputs(
                                            ClarifaiInput.forImage(new File(this.PicPath))) //利用clarifai來預測圖片傳進去的結果
                                    .executeSync()
                                    .get();

                    System.out.println(predictionResults);
                    System.out.println(predictionResults.get(0).data().get(0).name());
                    responseString = new String(predictionResults.get(0).data().get(0).name());

                    //把多種食物加入選單內讓user選
                    for(int i=0;i<10;i++){
                        if(predictionResults.get(0).data().get(i).name().isEmpty() == false ){
                            food_choose_list.add(predictionResults.get(0).data().get(i).name());
                        }
                    }

                } catch (Exception e) {
                    // Error: File not found
                }
                return responseString;

        }


        @Override
        protected void onPostExecute(String result) {
            if (result.equals(responseString)) {
                uploadProgressDialog.dismiss();
            }
            imageUrl = getParseString(responseString, "data", "img_url");
            System.out.println(imageUrl);

            /*創造一個選單讓user選擇要上傳哪一類食物(EX 便當)*/
            setContentView(R.layout.custom_spinner_for_food);
            Spinner spinner = (Spinner)findViewById(R.id.spinner);
            Button button = (Button)findViewById(R.id.button2);

            ArrayAdapter<String>  lunchList = new ArrayAdapter<>(this.galleryActivity,
                    android.R.layout.simple_spinner_dropdown_item,
                    food_choose_list);
            spinner.setAdapter(lunchList);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    //Toast.makeText(GalleryActivity.this, "你選的是" + food_choose_list[position], Toast.LENGTH_SHORT).show(); // For debugging
                    resultText = food_choose_list.get(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });


            // resultText 是最終選出食物的名字
            resultText = result.replace(" ","");

            // Compare Food Cal DAO to get calorie
            CompFoodDB compFoodDB = new CompFoodDB(resultText, foodCalList);
            int[] compare_result = compFoodDB.compareFoodCalDB();

            // output test
            System.out.println("Suggested result: " + resultText);
            System.out.println("after : " + resultText.replace(" ",""));

            // if the compare result is empty
            if (compare_result == null || compare_result.length == 0) {
                button.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        // Code here executes on main thread after user presses button
                        processFoodEvent();
                    }
                });

            } else { //If comparison matches data in the dataset
                CustomDialog customDialog = new CustomDialog(compare_result, food, foodCalList,
                        fileName, picUriString, GalleryActivity.this,encodedString);
                customDialog.processDialogControllers();
            }
        }
    }

    private class ATJ extends AsyncTask<String, Void, String> {
        String Url;
        GalleryActivity galleryActivity;
        String result_text;
        ProgressDialog prd;

        public ATJ(String Url, GalleryActivity galleryActivity){
            this.Url = Url;
            this.galleryActivity = galleryActivity;
        }

        @Override
        protected void onPreExecute() {
            prd = new ProgressDialog(galleryActivity);
            prd.setTitle("搜尋中");
            prd.setMessage("請等待搜尋結果");
            prd.show();
        }

        @Override
        protected String doInBackground(String... urls) {
            try {
                try {
                    // Connect website: google search by image
                    Document doc = Jsoup.connect("http://images.google.com/searchbyimage?image_url=" + Url).timeout(0).get();

                    // Parse html with class name: _gUb
                    //Elements elem = doc.getElementsByClass("_gUb");

                    // Get the text content
                    //result_text = elem.text();

                    // output test
                /*System.out.println("============");
                System.out.println(elem);
                System.out.println("============");*/

                } /*catch (IOException e) {
                    System.out.println("IO exception");
                }*/ catch (Exception e) {
                    e.printStackTrace();
                }

                return result_text;
            }
            finally {

            }
        }

        @Override
        protected void onPostExecute(String result) {
            // Get the result text from the response string
            if (prd.isShowing()) prd.dismiss();
            resultText = result.replace(" ","");

            // Compare Food Cal DAO to get calorie
            CompFoodDB compFoodDB = new CompFoodDB(resultText, foodCalList);
            int[] compare_result = compFoodDB.compareFoodCalDB();

            // output test
            System.out.println("Suggested result: " + resultText);
            System.out.println("after : " + resultText.replace(" ",""));
            // if the compare result is empty
            if (compare_result == null || compare_result.length == 0) {
                // Process normal food event
                processFoodEvent();
            } else {
                // Process dialog with spinner wheel
                CustomDialog customDialog = new CustomDialog(compare_result, food, foodCalList,
                        fileName, picUriString, GalleryActivity.this,encodedString);
                customDialog.processDialogControllers();
            }
        }
    }

    public void onSubmit(View view) {
        if (view.getId() == R.id.search_item) {
            // Use Async Task to open httpUrlConnection for upload picture
            //String responseString = new String();

            // Use Async Task

            System.out.println("REAL PATH = "+realPath);
            System.out.println("FILE = "+picFile);
            /*我們現在要做的事就是把real path的值傳進去即可*/

            ATC atc = new ATC(picFile, realPath, GalleryActivity.this);
            /*目前抓到問題為atc.execute()*/
            atc.execute();

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
        else if (Build.VERSION.SDK_INT == 19)
            realPath = RealPathUtil.getRealPathFromURI_API19(this, data.getData());
        else{
            realPath = RealPathUtil.getRealPathFromUri_AboveAPI19(this,data.getData());
        }
        //set encodedString by compress bitmap
        encodedString = encodeImagetoString();
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
        System.out.println("Width = " + bitmap.getWidth());
        System.out.println("Height = " + bitmap.getHeight());
        Bitmap newBm = resizeBitmap(bitmap);
        System.out.println("Width = " + newBm.getWidth());
        System.out.println("Height = " + newBm.getHeight());
        //picture.setImageBitmap(bitmap);
        picture.setImageBitmap(newBm);
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
        //food.setEncodedString(encodedString); //!!!這行會閃退
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

    private String encodeImagetoString(){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        // Must compress the Image to reduce image size to make upload easy
        bitmaptoUpload.compress(Bitmap.CompressFormat.JPEG, 50, stream);
        byte[] byte_arr = stream.toByteArray();
        // Encode Image to String
        encodedString = Base64.encodeToString(byte_arr, Base64.DEFAULT);
        return encodedString;
    }

    private Bitmap resizeBitmap(Bitmap bm){
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth;
        float scaleHeight;

        if (width * height > 2000*2000) {
            float scaleArea = (float) 2000*2000/width/height;
            double scale = Math.sqrt((double) scaleArea);
            scaleHeight = (float) scale;
            scaleWidth = (float) scale;
        }

        else {
            scaleHeight = 0.75f;
            scaleWidth = 0.75f;
        }

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
    }
}
