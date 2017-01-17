package com.example.nthucs.prototype.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.nthucs.prototype.Jsouptest.JsoupUse;
import com.example.nthucs.prototype.R;
import com.example.nthucs.prototype.Settings.MyProfileDAO;
import com.example.nthucs.prototype.Settings.Profile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TestJsoup extends AppCompatActivity {
    private Button backButton, updateButton , nextpageButton;
    private int returnindex;
    // Edit text
    private EditText foodname_text ;
    public String food_name_text;
    private TextView heat_text ;
    public String heatarray;
    public AppCompatActivity activity;
    public String nowtext;
    public String[] inforarray;
    public int pagenum = 1;
    // data base for profile
    private MyProfileDAO myProfileDAO;

    // list of profile
    private List<Profile> profileList = new ArrayList<>();

    // currently and temporary profile
    private Profile curProfile, tempProfile;

    JsoupUse JsoupUse = new JsoupUse();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_test_jsoup);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        myProfileDAO = new MyProfileDAO(getApplicationContext());

        // get all profile data from data base
        profileList = myProfileDAO.getAll();

        // get the last profile data in the list
        curProfile = new Profile();


        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.test_jsoup_menu);

        // process back button
        processBackControllers();

        // process target edit text
        processEditTextControllers();


        // process update button
        processUpdateButtonControllers();

        processTextViewControllers();
        processNextpageButtonControllers();

        //isNetworkAvailable();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    // process back button listener
    private void processBackControllers() {
        // initialize back button
        backButton = (Button) findViewById(R.id.back_button);

        // avoid all upper case
        backButton.setTransformationMethod(null);

        // set button listener
        backButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent back = new Intent();
                back.setClass(TestJsoup.this, SettingsActivity.class);
                startActivity(back);
                finish();

                // origin activity slide to right, new activity slide from left
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
    }
    private void processTextViewControllers() {
        heat_text =   (TextView)findViewById(R.id.heat_select);
        //BMR_text =   (TextView)findViewById(R.id.BMR);

    }
    private void processEditTextControllers() {
        foodname_text = (EditText) findViewById(R.id.Write_Food_Name);
        foodname_text.getText().toString();
        //String name=tv1.getText().toString();
    }

    // process update button
    private void processUpdateButtonControllers() {

        updateButton = (Button) findViewById(R.id.update_button);

        // avoid all upper case
        updateButton.setTransformationMethod(null);
    }
    private void processNextpageButtonControllers() {

        nextpageButton = (Button) findViewById(R.id.next_page_button);

        // avoid all upper case
        nextpageButton.setTransformationMethod(null);
    }

    public void onSubmit(View view) throws IOException {
        // if user updated the profile
        String foodname;
        int nowselect;
        if (view.getId() == R.id.update_button) {
            foodname_text = (EditText) findViewById(R.id.Write_Food_Name);
            food_name_text = foodname_text.getText().toString();
            //foodname = food_name_text;
            //System.out.println("111111111111111111111111");
            //System.out.println(food_name_text);
            //System.out.println("0000000000000000000000000");
            heatarray = JsoupUse.getMyFitnessPalDateBase(food_name_text ,pagenum);
            //heat_text.setText("food_name_text");

            inforarray = JsoupUse.splitEveryInfor(heatarray);

            selectfood(inforarray);
        }
        else if (view.getId() == R.id.next_page_button) {
            pagenum = pagenum+1;
            foodname_text = (EditText) findViewById(R.id.Write_Food_Name);
            food_name_text = foodname_text.getText().toString();
            //foodname = food_name_text;
            //System.out.println("111111111111111111111111");
            //System.out.println(food_name_text);
            //System.out.println("0000000000000000000000000");
            heatarray = JsoupUse.getMyFitnessPalDateBase(food_name_text ,pagenum);
            //heat_text.setText("food_name_text");

            inforarray = JsoupUse.splitEveryInfor(heatarray);

            selectfood(inforarray);
        }

    }

    private void  selectfood(String[] foodarray   ) {
        final String[] items =foodarray;
        String temparray;

        for(int i = 0 ; i<items.length-1;i++){
            temparray = items[i+1];
            items[i] = temparray;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(TestJsoup.this);
        builder.setTitle("Select food");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int index) {
                    returnindex = index;
                    nowtext= inforarray[returnindex];
                    heat_text.setText(nowtext);
            }

        });
        builder.show();

    }


}