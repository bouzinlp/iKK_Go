package com.example.nthucs.prototype.AsyncTask;

/**
 * Created by USER12345678 on 2016/6/20.
 */

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class AsyncTaskJsoup extends AsyncTask<String, Void, String> {

    // image url with string
    String imageUrl;

    // response string
    String resultText;

    public AsyncTaskJsoup(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    protected String doInBackground(String... urls) {
        try {
            try {
                // Connect website: google search by image
                Document doc = Jsoup.connect("http://images.google.com/searchbyimage?image_url=" + imageUrl).get();

                // Parse html with class name: _gUb
                Elements elem = doc.getElementsByClass("_gUb");

                // Get the text content
                resultText = elem.text();

                // output test
                /*System.out.println("============");
                System.out.println(elem);
                System.out.println("============");*/
            } catch (IOException e) {
                System.out.println("IO exception");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return resultText;
        }
        finally {

        }
    }

    @Override
    protected void onPostExecute(String result) {
    }
}
