package com.example.nthucs.prototype.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.nthucs.prototype.R;
import com.example.nthucs.prototype.Settings.MyProfileDAO;
import com.example.nthucs.prototype.Settings.Profile;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.List;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by user on 2016/8/2.
 */
public class WeightChartActivity extends AppCompatActivity {

    // Back button
    private Button backButton;

    // data base for profile
    private MyProfileDAO myProfileDAO;

    // list of profile
    private List<Profile> profileList = new ArrayList<>();

    // data points for weight
    private DataPoint[] weightsData;

    // date label's number in axis X
    private int mNumLabels = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight_chart);

        // initialize data base
        myProfileDAO = new MyProfileDAO(getApplicationContext());

        // get all profile data from data base
        profileList = myProfileDAO.getAll();

        // initialize data point array
        weightsData = new DataPoint[profileList.size()];

        // from array list to data point
        for (int i = 0 ; i < profileList.size() ; i++) {
            // convert miles second to date time
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(profileList.get(i).getDatetime());
            Date date  = calendar.getTime();
            weightsData[i] = new DataPoint(date, profileList.get(i).getWeight());
        }

        // custom view in action bar
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.weight_chart_menu);

        // process back button
        processBackControllers();

        // temporary test graph view
        drawExampleWeightChart();
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
                back.setClass(WeightChartActivity.this, SettingsActivity.class);
                startActivity(back);
                finish();

                // origin activity slide to right, new activity slide from left
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
    }

    // example data for drawing weight chart
    private void drawExampleWeightChart() {
        GraphView graphView = (GraphView) findViewById(R.id.graph);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(weightsData);

        // set manual Y bounds
        graphView.getViewport().setYAxisBoundsManual(true);
        graphView.getViewport().setMinY(0);
        graphView.getViewport().setMaxY(150);

        // set manual X bounds
        graphView.getViewport().setYAxisBoundsManual(true);
        graphView.getViewport().setMinX(weightsData[0].getX());
        graphView.getViewport().setMaxX(weightsData[profileList.size()-1].getX());

        // as we use dates as labels, the human rounding to nice readable numbers is not necessary
        graphView.getGridLabelRenderer().setHumanRounding(false);

        // activate horizontal zooming and scrolling
        //graphView.getViewport().setScalable(true);
        // activate horizontal scrolling
        graphView.getViewport().setScrollable(true);
        // activate horizontal and vertical zooming and scrolling
        graphView.getViewport().setScalableY(true);
        // activate vertical scrolling
        //graphView.getViewport().setScrollableY(true);

        // set date label formatter in X axis
        graphView.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(graphView.getContext()));
        graphView.getGridLabelRenderer().setNumHorizontalLabels(mNumLabels);

        // set kg label formatter in Y axis, but conflict with X, so comment temporarily
        /*graphView.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueY) {
                if (isValueY) {
                    // show normal y values
                    return super.formatLabel(value, isValueY);
                } else {
                    // show currency for y values
                    return super.formatLabel(value, isValueY) + " kg";
                }
            }
        });*/

        graphView.addSeries(series);
    }
}
