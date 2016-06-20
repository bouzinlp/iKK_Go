package com.example.nthucs.prototype;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class PictureActivity extends AppCompatActivity {

    private MenuItem search_pic;

    // 寫入外部儲存設備授權請求代碼
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION = 100;
    private static final int START_CAMERA = 2;

    // Picture's original name and image view
    private String fileName;
    private ImageView picture;

    // Picture's file, uri, urlLink;
    private File picFile;
    private Uri picUri;
    private String imageUrl;

    // Search by word
    private String resultText;
    private TextView searchResult;

    // Web View for Google Image
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);

        Intent intent = getIntent();
        String action = intent.getAction();

        // 取得顯示照片的ImageView元件
        picture = (ImageView) findViewById(R.id.picture);

        // web view for Url
        webView = (WebView) findViewById(R.id.search_result);
        /*webView.loadUrl("https://www.google.com.tw/");
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.clearCache(true);
        registerForContextMenu(this.webView);*/

        // text view for input
        searchResult = (TextView) findViewById(R.id.result);

        if (action.equals("com.example.nthucs.prototype.TAKE_PICT"))
            requestStoragePermission();

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
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        search_pic = menu.findItem(R.id.search_pic);

        return true;
    }

    public void onSubmit(View view) {
        if (view.getId() == R.id.search_item) {
            // Use Async Task to open httpUrlConnection for upload picture
            String responseString = new String();

            // Use Async Task
            try{
                AsyncTaskConnect asyncTaskConnect = new AsyncTaskConnect(picFile, getImagePath(picUri), this);
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

            // Test: appear result in the text view
            //searchResult.setText(resultText);

            //Intent picResult = getIntent();

            // output test
            // System.out.println("Suggested result: "+resultText);

            // image result test
            // webView.loadUrl("http://images.google.com/searchbyimage?image_url="+imageUrl);

        }
        finish();
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
        Uri uri = Uri.fromFile(pictureFile);
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
}
