package com.example.nthucs.prototype.FoodList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.nthucs.prototype.Utility.MyDBHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2016/7/22.
 */

public class CalorieDAO {
    public static final String TABLE_NAME = "calorie";

    public static final String KEY_ID = "_id";
    public static final String INDEX_COLUMN = "indexNumber";
    public static final String CATEGORY_COLUMN = "category";
    public static final String CHINESENAME_COLUMN = "chineseName";
    public static final String ENGLISHNAME_COLUMN = "englishName";
    public static final String CALORIE_COLUMN = "calorie";
    public static final String PROTEIN_COLUMN = "protein";
    public static final String FAT_COLUMN = "fat";
    public static final String CARBOHYDRATES_COLUMN = "carbohydrates";
    public static final String DIETARYFIBER_COLUMN = "dietaryFiber";
    public static final String SODIUM_COLUMN = "sodium";
    public static final String CALCIUM_COLUMN = "calcium";
    public static final String MODIFIEDCALORIE_COLUMN = "modifiedCalorie";

    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    INDEX_COLUMN + " TEXT NOT NULL, " +
                    CATEGORY_COLUMN + " TEXT NOT NULL, " +
                    CHINESENAME_COLUMN + " TEXT NOT NULL, " +
                    ENGLISHNAME_COLUMN + " TEXT, " +
                    PROTEIN_COLUMN + " REAL, " +
                    FAT_COLUMN + " REAL, " +
                    CARBOHYDRATES_COLUMN + " REAL, " +
                    DIETARYFIBER_COLUMN + " REAL, " +
                    SODIUM_COLUMN + " REAL, " +
                    CALCIUM_COLUMN + " REAL, " +
                    CALORIE_COLUMN + " INTEGER NOT NULL, " +
                    MODIFIEDCALORIE_COLUMN + " INTEGER NOT NULL)";

    private SQLiteDatabase db;

    public CalorieDAO(Context context) {db = MyDBHelper.getDatabase(context);}

    public void close() { db.close();}

    public void insert(FoodCal foodCal) {
        ContentValues cv = new ContentValues();

        cv.put(INDEX_COLUMN, foodCal.getIdx());
        cv.put(CATEGORY_COLUMN, foodCal.getCategory());
        cv.put(CHINESENAME_COLUMN, foodCal.getChineseName());
        cv.put(ENGLISHNAME_COLUMN, foodCal.getEnglishName());
        cv.put(PROTEIN_COLUMN, foodCal.getProtein());
        cv.put(FAT_COLUMN, foodCal.getFat());
        cv.put(CARBOHYDRATES_COLUMN, foodCal.getCarbohydrates());
        cv.put(DIETARYFIBER_COLUMN, foodCal.getDietaryFiber());
        cv.put(SODIUM_COLUMN, foodCal.getSodium());
        cv.put(CALCIUM_COLUMN, foodCal.getCalcium());
        cv.put(CALORIE_COLUMN, foodCal.getCalorie());
        cv.put(MODIFIEDCALORIE_COLUMN, foodCal.getModifiedCalorie());

        long id = this.db.insert(TABLE_NAME, null, cv);
        foodCal.setId(id);

        //return  foodCal;
    }

    public List<FoodCal> getAll() {
        List<FoodCal> result = new ArrayList<>();
        Cursor cursor  = db.query(TABLE_NAME, null, null, null, null, null, null, null);

        while (cursor.moveToNext()) result.add(getRecord(cursor));

        cursor.close();
        return result;
    }

    public FoodCal getRecord(Cursor cursor) {
        FoodCal result = new FoodCal();

        result.setId(cursor.getLong(0));
        result.setIdx(cursor.getString(1));
        result.setCategory(cursor.getString(2));
        result.setChineseName(cursor.getString(3));
        result.setEnglishName(cursor.getString(4));
        result.setProtein(cursor.getFloat(5));
        result.setFat(cursor.getFloat(6));
        result.setCarbohydrates(cursor.getFloat(7));
        result.setDietaryFiber(cursor.getFloat(8));
        result.setSodium(cursor.getFloat(9));
        result.setCalcium(cursor.getFloat(10));
        result.setCalorie(cursor.getInt(11));
        result.setModifiedCalorie(cursor.getInt(12));


        return  result;
    }

    public boolean isTableEmpty() {
        Cursor cursor  = db.query(TABLE_NAME, null, null, null, null, null, null, null);
        cursor.moveToFirst();

        if (cursor != null && cursor.getCount() > 0) {
            return false;
        } else {
            return true;
        }
    }
}
