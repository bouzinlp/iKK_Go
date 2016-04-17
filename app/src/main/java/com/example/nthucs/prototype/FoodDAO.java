package com.example.nthucs.prototype;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class FoodDAO {

    public static final String TABLE_NAME = "food";

    public static final String KEY_ID = "_id";
    public static final String CALORIE_COLUMN = "calorie";
    public static final String PORTIONS_COLUMN = "portions";
    public static final String GRAMS_COLUMN = "grams";
    public static final String TITLE_COLUMN = "title";
    public static final String CONTENT_COLUMN = "content";

    public static final String CREATE_TABLE = "" +
            "CREATE TABLE " + TABLE_NAME + " (" +
                    KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    CALORIE_COLUMN + " TEXT NOT NULL, " +
                    PORTIONS_COLUMN + " TEXT NOT NULL, " +
                    GRAMS_COLUMN + " TEXT NOT NULL, " +
                    TITLE_COLUMN + " TEXT NOT NULL, " +
                    CONTENT_COLUMN + " TEXT NOT NULL)";

    private SQLiteDatabase db;

    public FoodDAO(Context context) {db = MyDBHelper.getDatabase(context);}

    public void close() { db.close();}

    public Food insert(Food food) {
        ContentValues cv = new ContentValues();

        cv.put(CALORIE_COLUMN, food.getCalorie());
        cv.put(PORTIONS_COLUMN, food.getPortions());
        cv.put(GRAMS_COLUMN, food.getGrams());
        cv.put(TITLE_COLUMN, food.getTitle());
        cv.put(CONTENT_COLUMN, food.getContent());

        long id = this.db.insert(TABLE_NAME, null, cv);

        food.setId(id);

        return food;
    }

    public boolean update(Food food) {
        ContentValues cv = new ContentValues();

        cv.put(CALORIE_COLUMN, food.getCalorie());
        cv.put(PORTIONS_COLUMN, food.getPortions());
        cv.put(GRAMS_COLUMN, food.getGrams());
        cv.put(TITLE_COLUMN, food.getTitle());
        cv.put(CONTENT_COLUMN, food.getContent());

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
        result.setCalorie(cursor.getFloat(1));
        result.setPortions(cursor.getFloat(2));
        result.setGrams(cursor.getFloat(3));
        result.setTitle(cursor.getString(4));
        result.setContent(cursor.getString(5));

        return result;
    }

    public int getCount() {
        int result = 0;
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_NAME, null);

        if (cursor.moveToNext()) result = cursor.getInt(0);

        return result;
    }
}
