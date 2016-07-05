package com.example.nthucs.prototype.AsyncTask;

import android.os.AsyncTask;

import com.example.nthucs.prototype.Activity.CameraActivity;
import com.example.nthucs.prototype.Utility.HttpFileUpload;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class AsyncTaskConnect extends AsyncTask<String, Void, String> {

    // Picture
    File picFile;
    String picPath;

    // Parent class
    CameraActivity cameraActivity;

    // URL upload
    private static final String SERVER_URL = "http://uploads.im/api?upload";

    // Http response
    private String responseString;

    public AsyncTaskConnect(File picFile, String picPath) {
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
