package com.example.nthucs.prototype.SpinnerWheel;

import com.example.nthucs.prototype.Activity.SportActivity;
import com.example.nthucs.prototype.SportList.SportCal;

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

    public CustomDialogForSport(List<SportCal> sportCalList, SportActivity activity) {

        this.sportCalList = sportCalList;
        this.activity = activity;

    }

    // process custom dialog
    public void processDialogControllers() {

        // initialize custom dialog

    }
}
