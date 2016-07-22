package com.example.nthucs.prototype.FoodList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

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
    public static final String MODIFIEDCALORIE_COLUMN = "modifiedCalorie";

    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    INDEX_COLUMN + " TEXT NOT NULL, " +
                    CATEGORY_COLUMN + " TEXT NOT NULL, " +
                    CHINESENAME_COLUMN + " TEXT NOT NULL, " +
                    ENGLISHNAME_COLUMN + " TEXT, " +
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
        result.setCalorie(cursor.getInt(5));
        result.setModifiedCalorie(cursor.getInt(6));

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
