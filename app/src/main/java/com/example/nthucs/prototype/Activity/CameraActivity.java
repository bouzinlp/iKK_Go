package com.example.nthucs.prototype.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nthucs.prototype.AsyncTask.AsyncTaskConnect;
import com.example.nthucs.prototype.AsyncTask.AsyncTaskJsoup;
import com.example.nthucs.prototype.BuildConfig;
import com.example.nthucs.prototype.FoodList.CalorieDAO;
import com.example.nthucs.prototype.FoodList.FoodCal;
import com.example.nthucs.prototype.SpinnerWheel.CustomDialog;
import com.example.nthucs.prototype.Utility.CompFoodDB;
import com.example.nthucs.prototype.Utility.FileUtil;
import com.example.nthucs.prototype.FoodList.Food;
import com.example.nthucs.prototype.R;
import com.example.nthucs.prototype.Utility.HttpFileUpload;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import clarifai2.api.ClarifaiBuilder;
import clarifai2.api.ClarifaiClient;
import clarifai2.dto.input.ClarifaiInput;
import clarifai2.dto.model.output.ClarifaiOutput;
import clarifai2.dto.prediction.Concept;
import okhttp3.OkHttpClient;

public class CameraActivity extends AppCompatActivity {

    private MenuItem search_pic;

    // 寫入外部儲存設備授權請求代碼
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION = 100;
    private static final int START_CAMERA = 2;

    // Picture's original name and image view
    private String fileName;
    private ImageView picture;
    private Bitmap bitmaptoUpload;
    private String encodedString;
    // Picture's file, uri, urlLink;
    private File picFile;
    private Uri picUri;
    private String imageUrl;

    // Search by word
    private String resultText;

    // Food storage
    private Food food;

    // food cal list, only from main activity
    private List<FoodCal> foodCalList = new ArrayList<>();

    // data base for storing calorie data
    private CalorieDAO calorieDAO;

    private Context context;

    //讓user選多種食物當中的一類
    List<String> food_choose_list = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        Intent intent = getIntent();
        String action = intent.getAction();
        context = this;

        // 取得顯示照片的ImageView元件
        picture = (ImageView) findViewById(R.id.picture);

        // new food
        food = new Food(resultText, fileName, true);

        if (action.equals("com.example.nthucs.prototype.TAKE_PICT")) {
            requestStoragePermission();
            // calorie data base
            calorieDAO = new CalorieDAO(getApplicationContext());

            // get all data
            foodCalList = calorieDAO.getAll();
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
            if (requestCode == START_CAMERA) {

            }
        }
    }

    // 覆寫請求授權後執行的方法
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePicture();
            } else {
                Toast.makeText(this, R.string.write_external_storage_denied,
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        File file = configFileName("P", ".jpg");
        picFile = file;

        if (file.exists()) {
            // 顯示照片元件
            picture.setVisibility(View.VISIBLE);
            // 設定照片
            FileUtil.fileToImageView(file.getAbsolutePath(), picture);
            bitmaptoUpload = BitmapFactory.decodeFile(file.getAbsolutePath()/*fileName*/);
            if (bitmaptoUpload == null) {
                //System.out.println("bitmap null "+ fileName);
            }
            encodedString = encodeImagetoString();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        search_pic = menu.findItem(R.id.search_pic);

        return true;
    }

    private class ATC extends AsyncTask<String, Integer, String> {

        // URL upload
        private static final String SERVER_URL = "http://uploads.im/api?upload";

        // Http response
        private String responseString;

        // Picture
        File PicFile;
        String PicPath;

        CameraActivity cameraActivity;

        // ProgressDialog
        private ProgressDialog uploadProgressDialog;
        private final CharSequence dialogTitle = "上傳中";
        private final CharSequence dialogMessage = "請等待上傳資料";

        public ATC(File picFile, String picPath, CameraActivity cameraActivity) {
            this.PicFile = picFile;
            this.PicPath = picPath;
            this.cameraActivity = cameraActivity;
        }

        @Override
        protected void onPreExecute() {
            uploadProgressDialog = new ProgressDialog(cameraActivity);

            // set title, message & style
            uploadProgressDialog.setTitle(dialogTitle);
            uploadProgressDialog.setMessage(dialogMessage);
            //uploadProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

            // start from zero, end to max
            //uploadProgressDialog.setProgress(0);
            //uploadProgressDialog.setMax(100);

            // show dialog when uploading
            uploadProgressDialog.show();

            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String... urls) {
            try {
                try {
                    // Set your file path here
                    //FileInputStream fstrm = new FileInputStream(PicFile);

                    // Set your server page url (and the file title/description)
                    //HttpFileUpload hfu = new HttpFileUpload(SERVER_URL, "searchPic", "searchFood");

                    // Send to server, pass file input stream and file's path
                    //hfu.Send_Now(fstrm, PicPath);

                    // Get the response string from server
                    //responseString = hfu.getResponseString();

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
            finally {

            }
        }

//        @Override
//        protected void onProgressUpdate(Integer... progress) {
//            uploadProgressDialog.incrementProgressBy(5);
//            super.onProgressUpdate(progress);
//        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals(responseString)) {
                uploadProgressDialog.dismiss();
            }
            imageUrl = getParseString(responseString, "data", "img_url");
            System.out.println(imageUrl);

            /*創造一個選單讓user選擇要上傳哪一類食物(EX 便當裡要上傳哪一種食物)*/
            setContentView(R.layout.custom_spinner_for_food);
            Spinner spinner = (Spinner)findViewById(R.id.spinner);
            Button button = (Button)findViewById(R.id.button2);

            ArrayAdapter<String> lunchList = new ArrayAdapter<>(this.cameraActivity,
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

            resultText = result.replace(" ","");

            // Compare Food Cal DAO to get calorie
            CompFoodDB compFoodDB = new CompFoodDB(resultText, foodCalList);
            int[] compare_result = compFoodDB.compareFoodCalDB();

            // output test
            System.out.println("Suggested result: " + resultText);
            System.out.println("after : " + resultText.replace(" ",""));

            //processFoodEvent();
            // if the compare result is empty
            if (compare_result == null || compare_result.length == 0) {
                button.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        // Code here executes on main thread after user presses button
                        processFoodEvent();
                    }
                });
                
            } else { //If comparison matches data in the dataset
                CustomDialog customDialog = new CustomDialog(compare_result, food, foodCalList,fileName, CameraActivity.this,encodedString);
                customDialog.processDialogControllers();
            }

            super.onPostExecute(result);
        }
    }

    private class ATJ extends AsyncTask<String, Void, String> {
        String Url;
        CameraActivity cameraActivity;
        String result_text;
        ProgressDialog prd;

        public ATJ(String Url, CameraActivity cameraActivity){
            this.Url = Url;
            this.cameraActivity = cameraActivity;
        }

        @Override
        protected void onPreExecute() {
            prd = new ProgressDialog(cameraActivity);
            prd.setTitle("搜尋中");
            prd.setMessage("請等待搜尋結果");
            prd.show();
        }

        @Override
        protected String doInBackground(String... urls) {
            try {
                try {
                    // Connect website: google search by image
                    Document doc = Jsoup.connect("http://images.google.com/searchbyimage?image_url=" + Url).get();

                    // Parse html with class name: _gUb
                    Elements elem = doc.getElementsByClass("_gUb");

                    // Get the text content
                    result_text = elem.text();



                    // output test
                /*System.out.println("============");
                System.out.println(elem);
                System.out.println("============");*/
                } catch (IOException e) {
                    System.out.println("IO exception");
                } catch (Exception e) {
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
            resultText = result;

            // Compare Food Cal DAO to get calorie
            CompFoodDB compFoodDB = new CompFoodDB(resultText, foodCalList);
            int[] compare_result = compFoodDB.compareFoodCalDB();

            // output test
            System.out.println("Suggested result: " + resultText);

            // if the compare result is empty
            if (compare_result == null || compare_result.length == 0) {
                // Process normal food event
                processFoodEvent();
            } else {
                // Process dialog with spinner wheel
                CustomDialog customDialog = new CustomDialog(compare_result, food, foodCalList,fileName, CameraActivity.this,encodedString);
                customDialog.processDialogControllers();
            }
        }
    }

    public void onSubmit(View view) {
        if (view.getId() == R.id.search_item) {
            // Use Async Task to open httpUrlConnection for upload picture
            //String responseString = new String();

            System.out.println("Pic File = "+picFile);
            System.out.println("Image Path = "+getImagePath(picUri));

            ATC atc = new ATC(picFile, getImagePath(picUri), CameraActivity.this);
            atc.execute();

        } else if (view.getId() == R.id.cancel_item) {
            finish();
        }
    }

    private void requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int hasPermission = checkSelfPermission(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if (hasPermission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION);
                return;
            }
        }

        takePicture();
    }

    private void takePicture() {
        Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File pictureFile = configFileName("P", ".jpg");
        Uri uri;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            uri = Uri.fromFile(pictureFile);
        } else {
            uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileProvider", pictureFile);
        }
        picUri = uri;

        intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, uri);

        startActivityForResult(intentCamera, START_CAMERA);
    }

    private File configFileName(String prefix, String extension) {
        if (fileName == null) {
            fileName = FileUtil.getUniqueFileName();
        }

        return new File(FileUtil.getExternalStorageDir(FileUtil.APP_DIR),
                prefix + fileName + extension);
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
        food.setTakeFromCamera(true);
        food.setDatetime(new Date().getTime());
        //food.setEncodedString(encodedString);
        // back to main activity
        Intent result = getIntent();
        result.putExtra("com.example.nthucs.prototype.FoodList.Food", food);
        setResult(Activity.RESULT_OK, result);

        finish();
    }

    private String encodeImagetoString(){
        String result;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        // Must compress the Image to reduce image size to make upload easy
        bitmaptoUpload.compress(Bitmap.CompressFormat.JPEG, 50, stream);
        byte[] byte_arr = stream.toByteArray();
        // Encode Image to String
        result = Base64.encodeToString(byte_arr, Base64.DEFAULT);
        return result;
    }
}
