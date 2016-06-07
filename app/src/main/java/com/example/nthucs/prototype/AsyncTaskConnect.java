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

    // Parent class
    PictureActivity pictureActivity;

    // URL upload
    private static final String SERVER_URL = "http://uploads.im/api?upload";

    // Http response
    private String responseString;

    AsyncTaskConnect(File picFile, String picPath, PictureActivity picActivity) {
        this.picFile = picFile;
        this.picPath = picPath;
        pictureActivity = picActivity;
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

                // Get the response string from server
                responseString = hfu.getResponseString();

            } catch (FileNotFoundException e) {
                // Error: File not found
            }
            return responseString;
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
