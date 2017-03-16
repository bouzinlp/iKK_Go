package com.example.nthucs.prototype.FoodList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.nthucs.prototype.Activity.LoginActivity;
import com.example.nthucs.prototype.Utility.MyDBHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class FoodDAO {

    public static final String TABLE_NAME = "food";

    public static final String KEY_ID = "_id";
    public static final String USER_ID = "userId";
    public static final String CALORIE_COLUMN = "calorie";
    public static final String ENCODED_STRING = "encodedString";
    public static final String PORTIONS_COLUMN = "portions";
    public static final String GRAMS_COLUMN = "grams";
    public static final String TITLE_COLUMN = "title";
    public static final String CONTENT_COLUMN = "content";
    public static final String FILENAME_COLUMN = "filename";
    public static final String PICURISTRING_COLUMN = "picUriString";
    public static final String TAKEFROMCAMERA_COLUMN = "takeFromCamera";
    public static final String DATETIME_COLUMN = "datetime";

    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                    USER_ID + " TEXT NOT NULL, "+
                    CALORIE_COLUMN + " TEXT NOT NULL, " +
                    ENCODED_STRING + " TEXT NOT NULL, "+
                    PORTIONS_COLUMN + " TEXT NOT NULL, " +
                    GRAMS_COLUMN + " TEXT NOT NULL, " +
                    FILENAME_COLUMN + " TEXT, " +
                    TITLE_COLUMN + " TEXT NOT NULL, " +
                    CONTENT_COLUMN + " TEXT NOT NULL, "+
                    PICURISTRING_COLUMN + " TEXT, " +
                    TAKEFROMCAMERA_COLUMN + " TEXT NOT NULL, " +
                    DATETIME_COLUMN + " INTEGER NOT NULL)";

    private SQLiteDatabase db;

    public FoodDAO(Context context) {db = MyDBHelper.getDatabase(context);}

    public void close() { db.close();}

    public Food insert(Food food) {
        ContentValues cv = new ContentValues();

        cv.put(USER_ID, LoginActivity.facebookUserID);
        cv.put(CALORIE_COLUMN, food.getCalorie());
        cv.put(ENCODED_STRING,food.getEncodedString());
        cv.put(PORTIONS_COLUMN, food.getPortions());
        cv.put(GRAMS_COLUMN, food.getGrams());
        cv.put(FILENAME_COLUMN, food.getFileName());
        cv.put(TITLE_COLUMN, food.getTitle());
        cv.put(CONTENT_COLUMN, food.getContent());
        cv.put(PICURISTRING_COLUMN, food.getPicUriString());
        cv.put(TAKEFROMCAMERA_COLUMN, food.isTakeFromCamera());
        cv.put(DATETIME_COLUMN, food.getDatetime());

        long id = this.db.insert(TABLE_NAME, null, cv);

        food.setId(id);

        return food;
    }

    public boolean update(Food food) {
        ContentValues cv = new ContentValues();

        cv.put(CALORIE_COLUMN, food.getCalorie());
        cv.put(ENCODED_STRING,food.getEncodedString());
        cv.put(PORTIONS_COLUMN, food.getPortions());
        cv.put(GRAMS_COLUMN, food.getGrams());
        cv.put(FILENAME_COLUMN, food.getFileName());
        cv.put(TITLE_COLUMN, food.getTitle());
        cv.put(CONTENT_COLUMN, food.getContent());
        cv.put(PICURISTRING_COLUMN, food.getPicUriString());
        cv.put(TAKEFROMCAMERA_COLUMN, food.isTakeFromCamera());
        cv.put(DATETIME_COLUMN, food.getDatetime());

        String where = KEY_ID + "=" + food.getId();

        return this.db.update(TABLE_NAME, cv, where, null) > 0;
    }

    public boolean delete(long id){

        String where = KEY_ID + "=" + id;

        return db.delete(TABLE_NAME, where, null) > 0;
    }

    public List<Food> getAll() {
        List<Food> result = new ArrayList<>();
        Cursor cursor  = db.query(TABLE_NAME, null, null, null, null, null, null, null);

        while (cursor.moveToNext()) result.add(getRecord(cursor));

        cursor.close();
        return result;
    }

    public Food get(long id) {
        Food food = null;

        String where = KEY_ID + "=" + id;

        Cursor result = db.query(TABLE_NAME, null, where, null, null, null, null, null);

        if (result.moveToFirst()) food = getRecord(result);

        result.close();
        return food;
    }

    public Food getRecord(Cursor cursor) {
        Food result = new Food();

        result.setId(cursor.getLong(0));
        result.setCalorie(cursor.getFloat(2));
        result.setEncodedString(cursor.getString(3));
        result.setPortions(cursor.getFloat(5));
        result.setGrams(cursor.getFloat(6));
        result.setFileName(cursor.getString(6));
        result.setTitle(cursor.getString(7));
        result.setContent(cursor.getString(8));
        result.setPicUriString(cursor.getString(9));
        result.setTakeFromCamera(cursor.getInt(10) > 0);
        result.setDatetime(cursor.getLong(11));

        return result;
    }

    public int getCount() {
        int result = 0;
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_NAME, null);

        if (cursor.moveToNext()) result = cursor.getInt(0);

        return result;
    }
}
