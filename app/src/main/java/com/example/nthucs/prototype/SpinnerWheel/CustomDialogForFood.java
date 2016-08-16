package com.example.nthucs.prototype.SpinnerWheel;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.widget.Button;

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
    private Activity activity;

    // Scrolling flag
    private boolean scrolling = false;

    // Global spinner and adapter
    private AbstractWheel categorySpinner, chineseNameSpinner;
    private ArrayWheelAdapter<String> categoryAdapter, chineseNameAdapter;

    // Food's category and chinese name
    String category[] = new String[]{"穀物類", "澱粉類", "堅果及種子類", "水果類", "蔬菜類", "藻類", "菇類", "豆類", "肉類", "魚貝類",
                                     "蛋類", "乳品類", "油脂類", "糖類", "嗜好性飲料類", "調味料及香辛料類", "糕餅點心類", "加工調理食品類"};
    String chineseName[][] = new String[18][];

    // record active category and chinese name
    int mActiveCategory = 0;
    int mActiveChineseName[] = new int[] {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};

    public CustomDialogForFood(List<FoodCal> foodCalList, Activity activity) {

        this.foodCalList = foodCalList;
        this.activity = activity;

        int categoryCount = 0;
        int chineseNameCount[] = new int[18];

        for (int i = 0 ; i < foodCalList.size() ; i++) {
            if (foodCalList.get(i).getCategory().equals(category[categoryCount])) {
                chineseName[categoryCount][chineseNameCount[categoryCount]] = foodCalList.get(i).getChineseName();
                chineseNameCount[categoryCount]++;
            }

            if (foodCalList.get(i).getCategory().equals(foodCalList.get(i + 1).getCategory()) == false
                    && i != foodCalList.size()-1) {
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

                // test
                System.out.println(chineseNameAdapter.getItemText(categorySpinner.getCurrentItem()));

                // dismiss dialog
                dialog.dismiss();
            }
        });
    }
}
