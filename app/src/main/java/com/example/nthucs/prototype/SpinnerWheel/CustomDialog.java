package com.example.nthucs.prototype.SpinnerWheel;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.nthucs.prototype.FoodList.Food;
import com.example.nthucs.prototype.FoodList.FoodCal;
import com.example.nthucs.prototype.R;
import com.example.nthucs.prototype.antistatic.spinnerwheel.AbstractWheel;
import com.example.nthucs.prototype.antistatic.spinnerwheel.OnWheelChangedListener;
import com.example.nthucs.prototype.antistatic.spinnerwheel.OnWheelClickedListener;
import com.example.nthucs.prototype.antistatic.spinnerwheel.OnWheelScrollListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by user on 2016/8/14.
 */
public class CustomDialog {

    // compare result's index array
    private int[] compare_result;

    // Food storage
    private Food food;

    // food cal list, from data base and parent activity
    private List<FoodCal> foodCalList = new ArrayList<>();

    // Picture's original name and uri string
    private String fileName;
    private String picUriString;

    // Parent activity
    private Activity activity;

    // Global spinner wheel & it's adapter, for process button
    private AbstractWheel dialogSpinner;
    private SpinnerWheelAdapter spinnerWheelAdapter;

    // Identify parent activity
    private boolean parentIsGallery;

    //image encodedString
    private String encodedString;

    public CustomDialog(int[] compare_result, Food food, List<FoodCal> foodCalList,
                        String fileName, String picUriString, Activity activity,String encodedString) {
        this.compare_result = compare_result;
        this.food = food;
        this.foodCalList = foodCalList;

        this.fileName = fileName;
        this.picUriString = picUriString;

        this.activity = activity;

        // parent activity is gallery
        this.parentIsGallery = true;

        this.encodedString =encodedString;
    }

    public CustomDialog(int[] compare_result, Food food, List<FoodCal> foodCalList,
                        String fileName, Activity activity,String encodedString) {
        this.compare_result = compare_result;
        this.food = food;
        this.foodCalList = foodCalList;

        this.fileName = fileName;
        this.picUriString = new String();

        this.activity = activity;

        // parent activity is camera
        this.parentIsGallery = false;

        this.encodedString = encodedString;
    }

    // Dialog with spinner wheel to choose food name & calorie
    public void processDialogControllers() {

        // combine result string with chinese name & food calorie according to food calorie data base's index
        final String[] compare_string = mergeString();

        // initialize custom dialog
        final Dialog dialog = new Dialog(activity);
        dialog.setCancelable(false);
        dialog.setTitle(R.string.choose_food);
        dialog.setContentView(R.layout.custom_dialog);

        // process spinner wheel controllers
        processSpinnerWheelControllers(dialog, compare_string);

        // process dialog's button controllers
        processButtonControllers(dialog, compare_string);

        // show dialog
        dialog.show();
    }

    // Merge food's chinese name and calorie
    private String[] mergeString() {
        String[] compare_string = new String[compare_result.length];

        for (int i = 0 ; i < compare_result.length ; i++) {
            int spaceLength = 40 - foodCalList.get(compare_result[i]).getChineseName().getBytes().length;

            //System.out.println(foodCalList.get(compare_result[i]).getChineseName().length() + " "
            //        + Float.toString(foodCalList.get(compare_result[i]).getCalorie()).length());

            compare_string[i] = foodCalList.get(compare_result[i]).getChineseName();

            //System.out.println(foodCalList.get(compare_result[i]).getChineseName().getBytes().length+" "+spaceLength);

            for (int j = 0 ; j < spaceLength ; j++) {
                compare_string[i] += " ";
            }

            compare_string[i] += Float.toString(foodCalList.get(compare_result[i]).getCalorie());
            //compare_string[i] = foodCalList.get(compare_result[i]).getChineseName() + ": " +
            //        Float.toString(foodCalList.get(compare_result[i]).getCalorie());
        }

        return  compare_string;
    }

    // Process spinner wheel controllers
    private void processSpinnerWheelControllers(Dialog dialog, String[] compare_string) {

        // initialize dialog's wheel spinner
        dialogSpinner = (AbstractWheel) dialog.findViewById(R.id.spinner_wheel);

        // initialize and set adapter to spinner wheel
        spinnerWheelAdapter = new SpinnerWheelAdapter(activity, R.layout.spinner_wheel_item, compare_string);
        dialogSpinner.setViewAdapter(spinnerWheelAdapter);
        dialogSpinner.setCyclic(false);
        dialogSpinner.setVisibleItems(5);

        // register on wheel change listener
        OnWheelChangedListener wheelListener = new OnWheelChangedListener() {
            public void onChanged(AbstractWheel wheel, int oldValue, int newValue) {
            }
        };

        // set on wheel change listener
        dialogSpinner.addChangingListener(wheelListener);

        // register on wheel click listener
        OnWheelClickedListener clickListener = new OnWheelClickedListener() {
            public void onItemClicked(AbstractWheel wheel, int itemIndex) {
                wheel.setCurrentItem(itemIndex, true);
            }
        };

        // set on wheel click listener
        dialogSpinner.addClickingListener(clickListener);

        // register on wheel scroll listener
        OnWheelScrollListener scrollListener = new OnWheelScrollListener() {
            @Override
            public void onScrollingStarted(AbstractWheel wheel) {}

            @Override
            public void onScrollingFinished(AbstractWheel wheel) {}
        };

        // set on wheel scroll listener
        dialogSpinner.addScrollingListener(scrollListener);

        // set current item
        dialogSpinner.setCurrentItem(1);
    }

    // process button controllers
    private void processButtonControllers(final Dialog dialog, final String[] compare_string) {

        // initialize button
        Button dialogButton = (Button) dialog.findViewById(R.id.dialog_button);

        // register & set on click listener
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // record selected index
                int selectedIdx = 0;

                // find selected food by user within compare result
                for (int i = 0 ; i < compare_string.length ; i++) {
                    if (spinnerWheelAdapter.getItemText(
                            dialogSpinner.getCurrentItem()).equals(compare_string[i])) {
                        selectedIdx = compare_result[i];
                    }
                }
                // Set food's information(title and picture name)
                food.setTitle(foodCalList.get(selectedIdx).getChineseName());
                food.setContent("blank content");
                food.setFileName(fileName);
                food.setCalorie(foodCalList.get(selectedIdx).getCalorie());
                food.setEncodedString(encodedString);
                food.setGrams(100.0f);
                food.setPortions(1.0f);
                food.setPicUriString(picUriString);
                food.setEncodedString(encodedString);
                // distinguish parent activity
                if (parentIsGallery == true) {
                    food.setTakeFromCamera(false);
                } else if (parentIsGallery == false) {
                    food.setTakeFromCamera(true);
                }

                food.setDatetime(new Date().getTime());
                System.out.println("GETTIME = "+new Date().getTime());
                // Set result to main activity
                Intent result = activity.getIntent();
                result.putExtra("com.example.nthucs.prototype.FoodList.Food", food);
                activity.setResult(Activity.RESULT_OK, result);

                // dismiss dialog and finish activity
                dialog.dismiss();
                activity.finish();
            }
        });
    }
}
