package com.example.nthucs.prototype.Activity;

import android.content.Intent;
import java.util.Calendar;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.nthucs.prototype.FoodList.Food;
import com.example.nthucs.prototype.FoodList.FoodDAO;
import com.example.nthucs.prototype.R;
import com.example.nthucs.prototype.SportList.Sport;
import com.example.nthucs.prototype.SportList.SportDAO;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.List;

/**
 * Created by user on 2016/8/2.
 */
public class CalorieConsumptionActivity extends AppCompatActivity {

    // Back button
    private Button backButton;

    // data base for storing food list
    private FoodDAO foodDAO;

    // data base for storing sport list
    private SportDAO sportDAO;

    // list of foods
    private List<Food> foods;

    // list of sports
    private List<Sport> sports;

    // data points for consumption calorie
    private DataPoint[] conCalData;

    // data points for absorbed  calorie
    private DataPoint[] absCalData;

    // total days
    private int consumptDays = 1, absorbDays = 1;

    // total calorie in the same days
    private int totalConCals[], totalAbsCals[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calorie_consumption);

        // food list data base
        foodDAO = new FoodDAO(getApplicationContext());
        foods = foodDAO.getAll();

        // sport list data base
        sportDAO = new SportDAO(getApplicationContext());
        sports = sportDAO.getAll();

        // count the total days
        countTotalDays();

        // initialize data point array
        conCalData = new DataPoint[consumptDays];
        absCalData = new DataPoint[absorbDays];

        // from array list to data point
        for (int i = 0 ; i < consumptDays ; i++) {
            conCalData[i] = new DataPoint(i, totalConCals[i]);
        }
        for (int i = 0 ; i < absorbDays ; i++) {
            absCalData[i] = new DataPoint(i, totalAbsCals[i]);
        }

        // custom view in action bar
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.calorie_consumption_menu);

        // process back button
        processBackControllers();

        // temporary test graph view
        drawExampleCalorieChart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    // process back button listener
    private void processBackControllers() {
        // initialize back button
        backButton = (Button)findViewById(R.id.back_button);

        // avoid all upper case
        backButton.setTransformationMethod(null);

        // set button listener
        backButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent back = new Intent();
                back.setClass(CalorieConsumptionActivity.this, SettingsActivity.class);
                startActivity(back);
                finish();

                // origin activity slide to right, new activity slide from left
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
    }

    // example data for drawing calorie consumption chart
    private void drawExampleCalorieChart() {
        GraphView graphView = (GraphView) findViewById(R.id.graph);
        LineGraphSeries<DataPoint> consumeSeries = new LineGraphSeries<>(conCalData);
        consumeSeries.setColor(Color.argb(255, 255, 60, 60));
        consumeSeries.setTitle("consume calorie");
        LineGraphSeries<DataPoint> absorbSeries = new LineGraphSeries<>(absCalData);
        absorbSeries.setTitle("absorb calorie");

        // set manual Y bounds
        graphView.getViewport().setYAxisBoundsManual(true);
        graphView.getViewport().setMinY(0);
        graphView.getViewport().setMaxY(findMaxCalorie());

        // set manual X bounds
        graphView.getViewport().setYAxisBoundsManual(true);
        graphView.getViewport().setMinX(0);
        graphView.getViewport().setMaxX(findMaxDays()-1);

        // activate horizontal scrolling
        //graphView.getViewport().setScrollable(true);
        // activate horizontal and vertical zooming and scrolling
        //graphView.getViewport().setScalableY(true);

        // use static labels for horizontal and vertical labels
        /*StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graphView);
        staticLabelsFormatter.setHorizontalLabels(new String[] {"Day1", "Day2", "Day3", "Day4", "Day5", "Day6", "Day7"});
        graphView.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);*/

        graphView.addSeries(consumeSeries);
        graphView.addSeries(absorbSeries);
    }

    // calculate the total days of consumption and absorption
    private void countTotalDays() {
        // count total days for eat & sport
        int eatDays[] = new int[foods.size()];
        int sportDays[] = new int[sports.size()];

        // total calorie in the same days
        totalConCals = new int[30/*consumptDays*/];
        totalAbsCals = new int[30/*absorbDays*/];

        for (int i = 0 ; i < foods.size() ; i++) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(foods.get(i).getDatetime());
            eatDays[i] = calendar.get(Calendar.DAY_OF_MONTH);
            if (i > 0 && eatDays[i] != eatDays[i-1]) consumptDays++;
            totalConCals[consumptDays-1] += foods.get(i).getCalorie();
        }
        for (int i = 0 ; i < sports.size() ; i++) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(sports.get(i).getDatetime());
            sportDays[i] = calendar.get(Calendar.DAY_OF_MONTH);
            if (i > 0 && sportDays[i] != sportDays[i-1]) absorbDays++;
            totalAbsCals[absorbDays-1] += sports.get(i).getCalorie();
        }
        System.out.println(consumptDays + " " + absorbDays);
        for (int i = 0; i < consumptDays ; i++) System.out.println(totalConCals[i]+"*");
        for (int i = 0; i < absorbDays ; i++) System.out.println(totalAbsCals[i]+"**");
    }

    // find maximum calorie in the consume & absorb days
    private int findMaxCalorie() {
        int maxCal = 0;
        for (int i = 0 ; i < consumptDays ; i++) {
            if (totalConCals[i] > maxCal) maxCal = totalConCals[i];
        }
        for (int i = 0 ; i < absorbDays ; i++) {
            if (totalAbsCals[i] > maxCal) maxCal = totalAbsCals[i];
        }
        return maxCal;
    }

    // find max days between consumption and absorption days
    private int findMaxDays() {
        if (absorbDays > consumptDays) return absorbDays;
        else return  consumptDays;
    }
}
