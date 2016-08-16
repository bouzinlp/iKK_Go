package com.example.nthucs.prototype.SpinnerWheel;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.widget.Button;

import com.example.nthucs.prototype.Activity.FoodActivity;
import com.example.nthucs.prototype.FoodList.FoodCal;
import com.example.nthucs.prototype.R;
import com.example.nthucs.prototype.antistatic.spinnerwheel.AbstractWheel;
import com.example.nthucs.prototype.antistatic.spinnerwheel.OnWheelChangedListener;
import com.example.nthucs.prototype.antistatic.spinnerwheel.OnWheelClickedListener;
import com.example.nthucs.prototype.antistatic.spinnerwheel.OnWheelScrollListener;
import com.example.nthucs.prototype.antistatic.spinnerwheel.adapters.ArrayWheelAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2016/8/15.
 */
public class CustomDialogForFood {

    // Food cal list, from data base and parent activity
    private List<FoodCal> foodCalList = new ArrayList<>();

    // Parent activity
    private FoodActivity activity;

    // Scrolling flag
    private boolean scrolling = false;

    // Global spinner and adapter
    private AbstractWheel categorySpinner, chineseNameSpinner;
    private ArrayWheelAdapter<String> categoryAdapter, chineseNameAdapter;

    // Food's category, chinese name, and calorie
    private String category[] = new String[]{"穀物類", "澱粉類", "堅果及種子類", "水果類", "蔬菜類", "藻類", "菇類", "豆類", "肉類", "魚貝類",
                                     "蛋類", "乳品類", "油脂類", "糖類", "嗜好性飲料類", "調味料及香辛料類", "糕餅點心類", "加工調理食品類"};
    private String chineseName[][] = new String[18][];
    private int calorie[][] = new int[18][];

    // Every category's total number
    private int categoryNumber[] = new int[]{110, 44, 61, 245, 287, 22, 63, 44, 156, 337, 70, 83, 45, 12, 92, 137, 105, 169};

    // Record active category and chinese name
    private int mActiveCategory = 0;
    private int mActiveChineseName[] = new int[] {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};

    public CustomDialogForFood(List<FoodCal> foodCalList, FoodActivity activity) {

        this.foodCalList = foodCalList;
        this.activity = activity;

        int categoryCount = 0;
        int chineseNameCount[] = new int[18];

        // initialize with different length
        for (int i = 0 ; i < 18 ; i++) {
            chineseName[i] = new String[categoryNumber[i]];
            calorie[i] = new int[categoryNumber[i]];
        }

        // assign to 2d string array: [category][chinese name]
        for (int i = 0 ; i < foodCalList.size() ; i++) {
            if (foodCalList.get(i).getCategory().equals(category[categoryCount])) {
                chineseName[categoryCount][chineseNameCount[categoryCount]] = foodCalList.get(i).getChineseName();
                calorie[categoryCount][chineseNameCount[categoryCount]] = foodCalList.get(i).getCalorie();
                chineseNameCount[categoryCount]++;
            }

            if (i < foodCalList.size()-1 &&
                    foodCalList.get(i).getCategory().equals(foodCalList.get(i + 1).getCategory()) == false) {
                categoryCount++;
            }
        }
    }

    // process custom dialog
    public void processDialogControllers() {

        // initialize custom dialog
        final Dialog dialog = new Dialog(activity);
        dialog.setCancelable(false);
        dialog.setTitle("Choose the food");
        dialog.setContentView(R.layout.custom_dialog_for_food);

        // process category spinner wheel controllers
        processCategorySpinnerControllers(dialog);

        // process dialog's button controllers
        processButtonControllers(dialog);

        // show dialog
        dialog.show();
    }

    // process category spinner wheel controllers
    private void processCategorySpinnerControllers(Dialog dialog) {

        // initialize and set adapter to food category wheel spinner
        categorySpinner = (AbstractWheel)dialog.findViewById(R.id.food_category_spinner);
        categoryAdapter = new ArrayWheelAdapter<String>(activity, category);
        categoryAdapter.setTextSize(20);
        categorySpinner.setViewAdapter(categoryAdapter);

        // initialize food chinese name wheel spinner
        chineseNameSpinner = (AbstractWheel)dialog.findViewById(R.id.food_chinese_name_spinner);
        chineseNameSpinner.setVisibleItems(5);

        // register on wheel change listener
        OnWheelChangedListener wheelListener = new OnWheelChangedListener() {
            public void onChanged(AbstractWheel wheel, int oldValue, int newValue) {
                if (!scrolling) {
                    // update food's chinese name with corresponding category
                    updateChineseNameSpinner(newValue);
                }
            }
        };

        OnWheelChangedListener nameWheelListener = new OnWheelChangedListener() {
            public void onChanged(AbstractWheel wheel, int oldValue, int newValue) {
                if (!scrolling) {
                    mActiveChineseName[mActiveCategory] = newValue;
                }
            }
        };

        // set on wheel change listener
        categorySpinner.addChangingListener(wheelListener);
        chineseNameSpinner.addChangingListener(nameWheelListener);

        // register on wheel click listener
        OnWheelClickedListener clickListener = new OnWheelClickedListener() {
            public void onItemClicked(AbstractWheel wheel, int itemIndex) {
                wheel.setCurrentItem(itemIndex, true);
            }
        };

        // set on wheel click listener
        categorySpinner.addClickingListener(clickListener);

        // register on wheel scroll listener
        OnWheelScrollListener scrollListener = new OnWheelScrollListener() {
            @Override
            public void onScrollingStarted(AbstractWheel wheel) {
                scrolling = true;
            }

            @Override
            public void onScrollingFinished(AbstractWheel wheel) {
                scrolling = false;

                // update food's chinese name with corresponding category
                updateChineseNameSpinner(categorySpinner.getCurrentItem());
            }
        };

        // set on wheel scroll listener
        categorySpinner.addScrollingListener(scrollListener);

        // set current item
        categorySpinner.setCurrentItem(1);
    }

    // update chinese name spinner wheel
    private void updateChineseNameSpinner(int index) {

        mActiveCategory = index;

        // initialize and set adapter
        chineseNameAdapter = new ArrayWheelAdapter<String>(activity, chineseName[index]);
        chineseNameAdapter.setTextSize(18);
        chineseNameSpinner.setViewAdapter(chineseNameAdapter);

        // set current item
        chineseNameSpinner.setCurrentItem(mActiveChineseName[index]);
    }

    // process button controllers
    private void processButtonControllers(final Dialog dialog) {

        // initialize button
        Button dialogOkButton = (Button) dialog.findViewById(R.id.dialog_ok_button);

        // register & set on click listener
        dialogOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // set dialog title button's text
                activity.getDialogTitleButton()
                        .setText(chineseNameAdapter.getItemText(chineseNameSpinner.getCurrentItem()));

                // set calorie edit text
                for (int i = 0 ; i < 18 ; i++) {
                    for (int j = 0 ; j < categoryNumber[i] ; j++) {
                        if (chineseName[i][j]
                                .equals(chineseNameAdapter.getItemText(chineseNameSpinner.getCurrentItem()))) {
                            activity.getCalorieText().setText(Integer.toString(calorie[i][j]));
                            break;
                        }
                    }
                }

                // dismiss dialog
                dialog.dismiss();
            }
        });
    }
}
