package com.example.nthucs.prototype.Settings;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.nthucs.prototype.Utility.MyDBHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by USER12345678 on 2016/7/24.
 */
public class MyProfileDAO {
    public static final String TABLE_NAME = "myProfile";

    public static final String KEY_ID = "_id";
    public static final String DATETIME_COLUMN = "datetime";
    public static final String LASTMODIFY_COLUMN = "lastModify";
    public static final String BIRTHDAY_COLUMN = "birthDay";
    public static final String SEX_COLUMN = "sex";
    public static final String HEIGHT_COLUMN = "height";
    public static final String WEIGHT_COLUMN = "weight";
    public static final String WEIGHTLOSSGOAL_COLUMN = "weightLossGoal";
    public static final String WEEKLYLOSSWEIGHT_COLUMN = "weeklyLossWeight";

    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    DATETIME_COLUMN + " INTEGER NOT NULL, " +
                    LASTMODIFY_COLUMN + " INTEGER NOT NULL, " +
                    BIRTHDAY_COLUMN + " INTEGER NOT NULL, " +
                    SEX_COLUMN + " TEXT NOT NULL, " +
                    HEIGHT_COLUMN + " TEXT NOT NULL, " +
                    WEIGHT_COLUMN + " TEXT NOT NULL, " +
                    WEIGHTLOSSGOAL_COLUMN + " TEXT, " +
                    WEEKLYLOSSWEIGHT_COLUMN + " TEXT)";

    private SQLiteDatabase db;

    public MyProfileDAO(Context context) {db = MyDBHelper.getDatabase(context);}

    public void close() { db.close();}

    public Profile insert(Profile profile) {
        ContentValues cv = new ContentValues();

        cv.put(DATETIME_COLUMN, profile.getDatetime());
        cv.put(LASTMODIFY_COLUMN, profile.getLastModify());
        cv.put(BIRTHDAY_COLUMN, profile.getBirthDay());
        cv.put(SEX_COLUMN, profile.getSex());
        cv.put(HEIGHT_COLUMN, profile.getHeight());
        cv.put(WEIGHT_COLUMN, profile.getWeight());
        cv.put(WEIGHTLOSSGOAL_COLUMN, profile.getWeightLossGaol());
        cv.put(WEEKLYLOSSWEIGHT_COLUMN, profile.getWeeklyLossWeight());

        long id = this.db.insert(TABLE_NAME, null, cv);

        profile.setId(id);

        return profile;
    }

    public List<Profile> getAll() {
        List<Profile> result = new ArrayList<>();
        Cursor cursor  = db.query(TABLE_NAME, null, null, null, null, null, null, null);

        while (cursor.moveToNext()) result.add(getRecord(cursor));

        cursor.close();
        return result;
    }

    public Profile getRecord(Cursor cursor) {
        Profile result  = new Profile();

        result.setId(cursor.getLong(0));
        result.setDatetime(cursor.getLong(1));
        result.setLastModify(cursor.getLong(2));
        result.setBirthDay(cursor.getLong(3));
        result.setSex(cursor.getString(4));
        result.setHeight(cursor.getFloat(5));
        result.setWeight(cursor.getFloat(6));
        result.setWeightLossGaol(cursor.getFloat(7));
        result.setWeeklyLossWeight(cursor.getFloat(8));

        return result;
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
