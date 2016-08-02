package com.example.nthucs.prototype.AsyncTask;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.example.nthucs.prototype.Activity.CameraActivity;
import com.example.nthucs.prototype.Activity.GalleryActivity;
import com.example.nthucs.prototype.Utility.HttpFileUpload;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class AsyncTaskConnect extends AsyncTask<String, Integer, String> {

    // Picture
    File picFile;
    String picPath;

    // Parent class
    CameraActivity cameraActivity;
    GalleryActivity galleryActivity;

    // URL upload
    private static final String SERVER_URL = "http://uploads.im/api?upload";

    // Http response
    private String responseString;

    // ProgressDialog
    private ProgressDialog uploadProgressDialog;
    private final CharSequence dialogTitle = "Uploading";
    private final CharSequence dialogMessage = "Wait to upload data ...";

    // Boolean value to identify the parent activity
    private boolean isFromCamera;

    public AsyncTaskConnect(File picFile, String picPath) {
        this.picFile = picFile;
        this.picPath = picPath;
    }

    public AsyncTaskConnect(File picFile, String picPath, CameraActivity cameraActivity) {
        this.picFile = picFile;
        this.picPath = picPath;
        this.cameraActivity = cameraActivity;
        this.isFromCamera = true;
    }

    public AsyncTaskConnect(File picFile, String picPath, GalleryActivity galleryActivity) {
        this.picFile = picFile;
        this.picPath = picPath;
        this.galleryActivity = galleryActivity;
        this.isFromCamera = false;
    }

    @Override
    protected void onPreExecute() {

        // from camera
        if (isFromCamera == true) {
            uploadProgressDialog = new ProgressDialog(cameraActivity);
        // from gallery
        } else {
            uploadProgressDialog = new ProgressDialog(galleryActivity);
        }

        // set title, message & style
        uploadProgressDialog.setTitle(dialogTitle);
        uploadProgressDialog.setMessage(dialogMessage);
        uploadProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

        // start from zero, end to max
        uploadProgressDialog.setProgress(0);
        uploadProgressDialog.setMax(100);

        // show dialog when uploading
        uploadProgressDialog.show();

        super.onPreExecute();
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
    protected void onProgressUpdate(Integer... progress) {

        super.onProgressUpdate(progress);
    }

    @Override
    protected void onPostExecute(String result) {
        if (result.equals(responseString)) {
            uploadProgressDialog.dismiss();
        }
        super.onPostExecute(result);
    }
}
