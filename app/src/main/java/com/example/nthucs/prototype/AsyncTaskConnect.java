package com.example.nthucs.prototype;

import android.os.AsyncTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class AsyncTaskConnect extends AsyncTask<String, Void, String> {

    // Picture
    File picFile;
    String picPath;

    // URL upload
    private static final String SERVER_URL = "http://uploads.im/api?upload";

    AsyncTaskConnect(File picFile, String picPath) {
        this.picFile = picFile;
        this.picPath = picPath;
    }
    @Override
    protected String doInBackground(String... urls) {
        try {
            try {
                // Set your file path here
                FileInputStream fstrm = new FileInputStream(picFile);

                // Set your server page url (and the file title/description)
                HttpFileUpload hfu = new HttpFileUpload(SERVER_URL, "searchPic", "searchFood");

                // Send to server, pass file input stream and file's path
                hfu.Send_Now(fstrm, picPath);

            } catch (FileNotFoundException e) {
                // Error: File not found
            }
            return "success";
        } /*catch (IOException e) {
            return "Unable to retrieve web page. URL may be invalid.";
        }*/
        finally {

        }
    }

    @Override
    protected void onPostExecute(String result) {

    }
}
