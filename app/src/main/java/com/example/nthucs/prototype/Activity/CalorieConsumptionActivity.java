package com.example.nthucs.prototype.Activity;

import android.content.Intent;
import java.util.Calendar;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.nthucs.prototype.FoodList.Food;
import com.example.nthucs.prototype.FoodList.FoodDAO;
import com.example.nthucs.prototype.R;
import com.example.nthucs.prototype.SportList.Sport;
import com.example.nthucs.prototype.SportList.SportDAO;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;

import java.util.Date;
import java.util.List;

import static com.example.nthucs.prototype.R.id.graph;

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

    // date label's number in axis X
    private int mNumLabels = 3;

    // X label date storage
    private Date[] allDate;
    //private Date startDate;
    //private Date currentDate;

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

        if (foods.isEmpty() == true && sports.isEmpty() == false) {
            Toast.makeText(CalorieConsumptionActivity.this, "飲食紀錄不可為空", Toast.LENGTH_SHORT).show();
            Intent back = new Intent();
            back.setClass(CalorieConsumptionActivity.this, SettingsActivity.class);
            startActivity(back);
            finish();
        } else if (foods.isEmpty() == false && sports.isEmpty() == true) {
            Toast.makeText(CalorieConsumptionActivity.this, "運動紀錄不可為空", Toast.LENGTH_SHORT).show();
            Intent back = new Intent();
            back.setClass(CalorieConsumptionActivity.this, SettingsActivity.class);
            startActivity(back);
            finish();
        } else if (foods.isEmpty() == true && sports.isEmpty() == true) {
            Toast.makeText(CalorieConsumptionActivity.this, "飲食與運動紀錄不可為空", Toast.LENGTH_SHORT).show();
            Intent back = new Intent();
            back.setClass(CalorieConsumptionActivity.this, SettingsActivity.class);
            startActivity(back);
            finish();
        } else {
            // count the total days
            countTotalDays();

            // initialize data point array
            conCalData = new DataPoint[consumptDays];
            absCalData = new DataPoint[absorbDays];

            // from array list to data point
            for (int i = 0; i < consumptDays; i++) {
                conCalData[i] = new DataPoint(allDate[i], totalConCals[i]);
            }
            for (int i = 0; i < absorbDays; i++) {
                absCalData[i] = new DataPoint(allDate[i], totalAbsCals[i]);
            }

            // custom view in action bar
            getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            getSupportActionBar().setCustomView(R.layout.calorie_consumption_menu);

            // process back button
            processBackControllers();

            // temporary test graph view
            drawExampleCalorieChart();
        }
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
        GraphView graphView = (GraphView) findViewById(graph);

        LineGraphSeries<DataPoint> consumeSeries = new LineGraphSeries<>(conCalData);
        consumeSeries.setColor(Color.argb(255, 255, 60, 60));
        //consumeSeries.setDrawBackground(true);
        consumeSeries.setAnimated(true);
        consumeSeries.setDrawDataPoints(true);
        consumeSeries.setTitle("consume calorie");
        consumeSeries.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series consumeSeries, DataPointInterface dataPoint) {
                Toast.makeText(CalorieConsumptionActivity.this, "消耗熱量: "+dataPoint.getY()+"kcal", Toast.LENGTH_SHORT).show();
            }
        });

        LineGraphSeries<DataPoint> absorbSeries = new LineGraphSeries<>(absCalData);
        //absorbSeries.setDrawBackground(true);
        absorbSeries.setAnimated(true);
        absorbSeries.setDrawDataPoints(true);
        absorbSeries.setTitle("absorb calorie");
        absorbSeries.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series absorbSeries, DataPointInterface dataPoint) {
                Toast.makeText(CalorieConsumptionActivity.this, "攝取熱量: "+dataPoint.getY()+"kcal", Toast.LENGTH_SHORT).show();
            }
        });

        // set manual Y bounds
        graphView.getViewport().setYAxisBoundsManual(true);
        graphView.getViewport().setMinY(0);
        graphView.getViewport().setMaxY(findMaxCalorie());

        // set manual X bounds
        graphView.getViewport().setYAxisBoundsManual(true);
        //graphView.getViewport().setMinX(0);
        //graphView.getViewport().setMaxX(findMaxDays()-1);

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

        // set date label formatter
        graphView.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(graphView.getContext()));
        graphView.getGridLabelRenderer().setNumHorizontalLabels(mNumLabels);

        // set manual X bounds to have nice steps
        graphView.getViewport().setMinX(allDate[0].getTime());
        graphView.getViewport().setMaxX(allDate[consumptDays-1].getTime());
        graphView.getViewport().setXAxisBoundsManual(true);

        // as we use dates as labels, the human rounding to nice readable numbers is not nessecary
        graphView.getGridLabelRenderer().setHumanRounding(false);
    }

    // calculate the total days of consumption and absorption
    private void countTotalDays() {
        // count total days for eat & sport
        int eatDays[] = new int[foods.size()];
        int sportDays[] = new int[sports.size()];

        // total calorie in the same days
        totalAbsCals = new int[30/*absorbDays*/];
        totalConCals = new int[30/*consumptDays*/];
        allDate = new Date[30];

        for (int i = 0 ; i < foods.size() ; i++) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(foods.get(i).getDatetime());
            if (i == 0) {
                //startDate = calendar.getTime();
                calendar.set(Calendar.HOUR_OF_DAY, 12);
                allDate[i] = calendar.getTime();
            } else if (i == foods.size()-1) {
                //currentDate = calendar.getTime();
            }
            eatDays[i] = calendar.get(Calendar.DAY_OF_MONTH);
            if (i > 0 && eatDays[i] != eatDays[i-1]) {
                calendar.set(Calendar.HOUR_OF_DAY, 12);
                allDate[absorbDays] = calendar.getTime();
                absorbDays++;
            }
            totalAbsCals[absorbDays-1] += foods.get(i).getCalorie();
        }
        for (int i = 0 ; i < sports.size() ; i++) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(sports.get(i).getDatetime());
            sportDays[i] = calendar.get(Calendar.DAY_OF_MONTH);
            if (i > 0 && sportDays[i] != sportDays[i-1]) {
                consumptDays++;
            }
            totalConCals[consumptDays-1] += sports.get(i).getCalorie();
        }
        System.out.println(absorbDays + " " + consumptDays);
        for (int i = 0; i < absorbDays ; i++) System.out.println(totalAbsCals[i]+"**");
        for (int i = 0; i < consumptDays ; i++) System.out.println(totalConCals[i]+"*");

        mNumLabels = consumptDays;
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
