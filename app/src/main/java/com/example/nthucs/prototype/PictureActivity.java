package com.example.nthucs.prototype;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

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
    private String responseString;
    private String picUrl;

    // Search by word
    private TextView searchResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);

        Intent intent = getIntent();
        String action = intent.getAction();

        // 取得顯示照片的ImageView元件
        picture = (ImageView) findViewById(R.id.picture);

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
            // use asyncTask to open httpUrlConnection for upload picture
            AsyncTaskConnect asyncTaskConnect = new AsyncTaskConnect(picFile, getImagePath(picUri), this);
            asyncTaskConnect.execute();

            // parse response string
            // Execute some code after 2 seconds have passed
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //System.out.println(asyncTaskConnect.getResponseString());
                }
            }, 3000);
            System.out.println(asyncTaskConnect.getResponseString());
            String responseString = asyncTaskConnect.getResponseString();
            System.out.println(responseString);
            getParseString(responseString, "status");
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

    public void setResponseString(String str) {
        responseString = str;
    }

    private String getParseString(String jsonStr, String target) {
        try {
            String strJson="{\"Employee\" :[ {\"id\":\"01\",\"name\":\"Gopal Varma\",\"salary\":\"500000\"}, {\"id\":\"02\",\"name\":\"Sairamkrishna\",\"salary\":\"500000\"}, {\"id\":\"03\",\"name\":\"Sathish kallakuri\",\"salary\":\"600000\"} ]}";
            JSONObject jsonObject = new JSONObject(strJson);
            String status = jsonObject.getString("Employee");
            System.out.println(jsonStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
