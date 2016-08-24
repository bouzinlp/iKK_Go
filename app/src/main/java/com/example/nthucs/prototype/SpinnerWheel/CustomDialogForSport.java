package com.example.nthucs.prototype.SpinnerWheel;

import android.app.Dialog;
import android.view.View;
import android.widget.Button;

import com.example.nthucs.prototype.Activity.SportActivity;
import com.example.nthucs.prototype.R;
import com.example.nthucs.prototype.SportList.SportCal;
import com.example.nthucs.prototype.antistatic.spinnerwheel.AbstractWheel;
import com.example.nthucs.prototype.antistatic.spinnerwheel.OnWheelChangedListener;
import com.example.nthucs.prototype.antistatic.spinnerwheel.OnWheelClickedListener;
import com.example.nthucs.prototype.antistatic.spinnerwheel.OnWheelScrollListener;
import com.example.nthucs.prototype.antistatic.spinnerwheel.adapters.ArrayWheelAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2016/8/22.
 */
public class CustomDialogForSport {

    // Sport cal list, from parent activity
    private List<SportCal> sportCalList = new ArrayList<>();

    // Parent activity
    private SportActivity activity;

    // Global spinner and adapter
    private AbstractWheel sportNameSpinner;
    private ArrayWheelAdapter<String> sportNameAdapter;

    // Sport name array
    private String[] sportName;

    // Current sport name title and index for set current item
    private String curSportName;
    private int curSportIdx;

    public CustomDialogForSport(List<SportCal> sportCalList, SportActivity activity) {

        this.sportCalList = sportCalList;
        this.activity = activity;

        // initialize current sport name title
        if (activity.getDialogTitleButton().getText().toString().isEmpty() == false) {
            curSportName = activity.getDialogTitleButton().getText().toString();
        } else {
            curSportName = new String();
        }

        // initialize sport name string array
        this.sportName = new String[this.sportCalList.size()];

        for (int i = 0 ; i < sportCalList.size() ; i++) {
            this.sportName[i] = sportCalList.get(i).getSportName();

            if (curSportName.equals(sportName[i])) {
                curSportIdx = i;
            }
        }
    }

    // process custom dialog
    public void processDialogControllers() {

        // initialize custom dialog
        final Dialog dialog = new Dialog(activity);
        dialog.setCancelable(false);
        dialog.setTitle("Choose the sport");
        dialog.setContentView(R.layout.custom_dialog_for_sport);

        // process sport name spinner wheel controllers
        processNameSpinnerControllers(dialog);

        // process dialog's button controllers
        processButtonControllers(dialog);

        // show dialog
        dialog.show();
    }

    // process sport name spinner wheel controllers
    private void processNameSpinnerControllers(Dialog dialog) {

        // initialize and set adapter to sport category wheel spinner
        sportNameSpinner = (AbstractWheel)dialog.findViewById(R.id.sport_name_spinner);
        sportNameAdapter = new ArrayWheelAdapter<String>(activity, sportName);
        sportNameAdapter.setTextSize(20);
        sportNameSpinner.setViewAdapter(sportNameAdapter);

        // register on wheel change listener
        OnWheelChangedListener wheelListener = new OnWheelChangedListener() {
            public void onChanged(AbstractWheel wheel, int oldValue, int newValue) {

            }
        };

        // set on wheel change listener
        sportNameSpinner.addChangingListener(wheelListener);

        // register on wheel click listener
        OnWheelClickedListener clickListener = new OnWheelClickedListener() {
            public void onItemClicked(AbstractWheel wheel, int itemIndex) {
                wheel.setCurrentItem(itemIndex, true);
            }
        };

        // set on wheel click listener
        sportNameSpinner.addClickingListener(clickListener);

        // register on wheel scroll listener
        OnWheelScrollListener scrollListener = new OnWheelScrollListener() {
            @Override
            public void onScrollingStarted(AbstractWheel wheel) {
            }

            @Override
            public void onScrollingFinished(AbstractWheel wheel) {
            }
        };

        // set on wheel scroll listener
        sportNameSpinner.addScrollingListener(scrollListener);

        // set current item
        sportNameSpinner.setCurrentItem(curSportIdx);
    }

    // process button controllers
    private void processButtonControllers(final Dialog dialog) {

        // initialize ok button
        Button dialogOkButton = (Button) dialog.findViewById(R.id.dialog_ok_button);

        // register & set on click listener
        dialogOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // set dialog title button's text
                activity.getDialogTitleButton()
                        .setText(sportNameAdapter.getItemText(sportNameSpinner.getCurrentItem()));

                // dismiss dialog
                dialog.dismiss();
            }
        });

        // initialize cancel button
        Button dialogCancelButton = (Button) dialog.findViewById(R.id.dialog_cancel_button);

        // register& set on click listener
        dialogCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // dismiss dialog
                dialog.dismiss();
            }
        });
    }
}
