package com.example.nthucs.prototype.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.nthucs.prototype.R;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

/**
 * Created by user on 2016/8/2.
 */
public class WeightChartActivity extends AppCompatActivity {

    // Back button
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight_chart);

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
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[]{
                new DataPoint(0, 0), new DataPoint(1, 68.5), new DataPoint(2, 68), new DataPoint(3, 67.8), new DataPoint(4, 67),
                new DataPoint(5, 67.5), new DataPoint(6, 67.8), new DataPoint(7, 67.3), new DataPoint(8, 67), new DataPoint(9, 66.5),
                new DataPoint(10, 66), new DataPoint(11, 65.5), new DataPoint(12, 66), new DataPoint(13, 65.4), new DataPoint(14, 65),
                new DataPoint(15, 65.1), new DataPoint(16, 65.2), new DataPoint(17, 64.8), new DataPoint(18, 64), new DataPoint(19, 64.2),
                new DataPoint(20, 64), new DataPoint(21, 63.6), new DataPoint(22, 63.4), new DataPoint(23, 63), new DataPoint(24, 62.7),
                new DataPoint(25, 62.6), new DataPoint(26, 62.5), new DataPoint(27, 62.5), new DataPoint(28, 62.4), new DataPoint(29, 62.3),
        });

        // activate horizontal zooming and scrolling
        //graphView.getViewport().setScalable(true);
        // activate horizontal scrolling
        graphView.getViewport().setScrollable(true);
        // activate horizontal and vertical zooming and scrolling
        graphView.getViewport().setScalableY(true);
        // activate vertical scrolling
        //graphView.getViewport().setScrollableY(true);

        graphView.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
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
        });

        graphView.addSeries(series);
    }
}
