package com.example.nthucs.prototype;

import android.os.AsyncTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class AsyncTaskConnect extends AsyncTask<String, Void, String> {

    File picFile;

    AsyncTaskConnect(File picFile) {
        this.picFile = picFile;
    }
    @Override
    protected String doInBackground(String... urls) {
        try {
            try {
                // Set your file path here
                FileInputStream fstrm = new FileInputStream(picFile);

                // Set your server page url (and the file title/description)
                HttpFileUpload hfu = new HttpFileUpload("http://uploads.im/api?upload", "search picture", "none");

                hfu.Send_Now(fstrm);

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
