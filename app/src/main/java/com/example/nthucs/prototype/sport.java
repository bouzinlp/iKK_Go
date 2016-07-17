package com.example.nthucs.prototype;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.opencsv.CSVReader;

import java.io.FileReader;
import java.util.List;

public class sport extends AppCompatActivity {
    sport sport_class;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sport);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        try {
            sport_class.read();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void read()throws Exception{
        CSVReader reader = new CSVReader(new FileReader("sport_E.csv"));
        List myEntries = reader.readAll();
        System.out.print(myEntries);
    }




}