package com.example.nthucs.prototype;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.HttpURLConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PictureActivity extends AppCompatActivity {

    private MenuItem search_pic;

    // 寫入外部儲存設備授權請求代碼
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION = 100;
    private static final int START_CAMERA = 2;

    // Picture
    private String fileName;
    private ImageView picture;

    // Food information
    //private EditText title_text, content_text, calorie_text, portions_text, grams_text;
    //private Food food;

    // Search by word
    String searchUrl = "https://www.googleapis.com/customsearch/v1";
    String searchItem = "android";
    String searchQuery = searchUrl + searchItem;

    TextView searchResult;


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

        //new JsonSearchTask().execute();
    }

    // 覆寫請求授權後執行的方法
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePicture();
            }
            else {
                Toast.makeText(this, R.string.write_external_storage_denied,
                        Toast.LENGTH_SHORT).show();
            }
        }
        else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        File file = configFileName("P", ".jpg");

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

    private class JsonSearchTask extends AsyncTask<Void, Void, Void> {
        String searchResultString = "";

        @Override
        protected Void doInBackground(Void... params) {
            try {
                searchResultString = ParseStringResult(sendQuery(searchQuery));
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            searchResult.setText(searchResultString);
            super.onPostExecute(aVoid);
        }
    }

    private String sendQuery(String query) throws IOException {
        String result = "";
        URL sUrl = new URL(query);

        HttpURLConnection httpURLConnection = (HttpURLConnection) sUrl.openConnection();

        if(httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            InputStreamReader inputStream = new InputStreamReader(httpURLConnection.getInputStream());

            BufferedReader bufferedReader = new BufferedReader(inputStream, 8192);

            String line = null;

            while((line = bufferedReader.readLine()) != null) {
                result += line;
            }

            bufferedReader.close();
        }

        return result;
    }

    private String ParseStringResult(String json) throws JSONException {
        String parsedResult = "";

        JSONObject jsonObject = new JSONObject(json);
        JSONObject jsonObject_responseData = jsonObject.getJSONObject("responseData");
        JSONArray jsonArray_result = jsonObject_responseData.getJSONArray("results");

        parsedResult += "Google Search for: " + searchItem + "\n";
        parsedResult += "Number of results returned = " + jsonArray_result.length() + "\n\n";

        for(int i = 0; i < jsonArray_result.length(); i++) {
            JSONObject jsonObject_i = jsonArray_result.getJSONObject(i);
            parsedResult += "title: " + jsonObject_i.getString("title") + "\n";
            parsedResult += "content: " + jsonObject_i.getString("content") + "\n";
            parsedResult += "url: " + jsonObject_i.getString("url") + "\n\n";
        }

        return parsedResult;
    }
}
