package com.example.nthucs.prototype;

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

    AsyncTaskJsoup(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    protected String doInBackground(String... urls) {
        try {
            try {
                Document doc = Jsoup.connect("http://images.google.com/searchbyimage?image_url=" + imageUrl).get();
                Elements elem = doc.getElementsByClass("_gUb");
                resultText = elem.text();
                System.out.println(resultText);
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
