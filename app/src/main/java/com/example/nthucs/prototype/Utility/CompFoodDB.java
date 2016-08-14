package com.example.nthucs.prototype.Utility;

import com.example.nthucs.prototype.FoodList.FoodCal;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2016/8/14.
 */
public class CompFoodDB {

    // food cal list, from data base and parent activity
    private List<FoodCal> foodCalList = new ArrayList<>();

    // result text from constructor
    private String resultText;

    public CompFoodDB(String resultText, List<FoodCal> foodCalList) {

        this.foodCalList = foodCalList;
        this.resultText = resultText;
    }

    // find food title in food calorie data base
    public int[] compareFoodCalDB() {
        // return with integer array
        int[] compare_result = new int[]{};

        // temporary test
        //String[] compare_result = {"", Float.toString(0.0f)};

        // if origin result text is null
        if (resultText == null || resultText.isEmpty() == true) {
            return null;
        }

        // split result with space
        String[] splitText = resultText.split("\\s+");

        // whether the string is english
        boolean isEnglishString = true;

        // string for build chinese character
        String chineseResultText = new String();

        // record the number of result to integer array
        int[] arrayCount = new int[splitText.length];

        for (int i = 0 ; i < splitText.length ; i++) {

            // traversal split string
            for (int j = 0 ; j < splitText[i].length() ; j++) {
                if ((splitText[i].charAt(j) >= 65 && splitText[i].charAt(j) <= 90)
                        || (splitText[i].charAt(j) >= 97 && splitText[i].charAt(j) <= 122)) {
                    isEnglishString = true;
                } else {
                    isEnglishString = false;
                    break;
                }
            }

            // compare every split english string
            if (isEnglishString == true) {
                for (int j = 0 ; j < foodCalList.size() ; j++) {
                    if (splitText[i].toLowerCase().contains(foodCalList.get(j).getEnglishName().toLowerCase())
                            && foodCalList.get(j).getEnglishName().isEmpty() == false) {
                        // count total number
                        arrayCount[i]++;
                    }
                }

                if (arrayCount[i] != 0) {
                    compare_result = new int[arrayCount[i]];
                }
                arrayCount[i] = 0;

                for (int j = 0 ; j < foodCalList.size() ; j++) {
                    if (splitText[i].toLowerCase().contains(foodCalList.get(j).getEnglishName().toLowerCase())
                            && foodCalList.get(j).getEnglishName().isEmpty() == false) {
                        compare_result[arrayCount[i]] = j;
                        arrayCount[i]++;
                    }
                }
                // merge chinese sub-string
            } else {
                chineseResultText += splitText[i];
            }
        }

        // compare merged chinese string with food cal
        if (isEnglishString == false) {
            for (int i = 0 ; i < foodCalList.size() ; i++) {
                if (foodCalList.get(i).getChineseName().contains(chineseResultText)) {
                    // count total number
                    arrayCount[0]++;
                }
            }

            compare_result = new int[arrayCount[0]];
            arrayCount[0] = 0;

            for (int i = 0 ; i < foodCalList.size() ; i++) {
                if (foodCalList.get(i).getChineseName().contains(chineseResultText)) {
                    compare_result[arrayCount[0]] = i;
                    arrayCount[0]++;
                }
            }
        }

        // test output
        /*for (int i = 0 ; i < compare_result.length ; i++) {
            System.out.println("name: " + foodCalList.get(compare_result[i]).getChineseName()
                    + " calorie: " + foodCalList.get(compare_result[i]).getCalorie());
        }*/

        return compare_result;
    }
}
