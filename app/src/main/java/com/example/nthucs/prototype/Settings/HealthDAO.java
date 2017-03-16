package com.example.nthucs.prototype.Settings;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.nthucs.prototype.Activity.LoginActivity;
import com.example.nthucs.prototype.Utility.MyDBHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Debbie Chiou on 2017/1/21.
 */

public class HealthDAO {
    public static final String TABLE_NAME = "health";
    public static final String KEY_ID = "_id";
    public static final String USER_ID = "userID";
    public static final String DATETIME_COLUMN = "datetime";
    public static final String LASTMODIFY_COLUMN = "lastModify";
    public static final String TEMPERATURE_COLUMN = "temperature";
    public static final String DRUNKWATER_COLUMN = "drunkWater";
    public static final String SYSTOLICBLOOD_COLUMN = "systolicBloodPressure";
    public static final String DIASTOLICBLOOD_COLUMN = "diastolicBloodPressure";
    public static final String PULSE_COLUMN = "pulse";

    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    USER_ID + " INTEGER NOT NULL, " +
                    DATETIME_COLUMN + " INTEGER NOT NULL, " +
                    LASTMODIFY_COLUMN + " INTEGER NOT NULL, " +
                    TEMPERATURE_COLUMN + " TEXT, " +
                    DRUNKWATER_COLUMN + " INTEGER, " +
                    SYSTOLICBLOOD_COLUMN + " TEXT, " +
                    DIASTOLICBLOOD_COLUMN + " TEXT, " +
                    PULSE_COLUMN + " TEXT)";

    private SQLiteDatabase db;

    public HealthDAO(Context context) {db = MyDBHelper.getDatabase(context);}

    public void close() { db.close();}

    public Health insert(Health health) {
        ContentValues cv = new ContentValues();

        cv.put(USER_ID, LoginActivity.facebookUserID);
        cv.put(DATETIME_COLUMN, health.getDatetime());
        cv.put(LASTMODIFY_COLUMN, health.getLastModify());
        cv.put(TEMPERATURE_COLUMN, health.getTemperature());
        cv.put(DRUNKWATER_COLUMN, health.getDrunkWater());
        cv.put(SYSTOLICBLOOD_COLUMN, health.getSystolicBloodPressure());
        cv.put(DIASTOLICBLOOD_COLUMN, health.getDiastolicBloodPressure());
        cv.put(PULSE_COLUMN, health.getPulse());

        long id = db.insert(TABLE_NAME, null, cv);

        health.setId(id);
        return health;
    }

    public boolean update(Health health) {
        ContentValues cv = new ContentValues();

        cv.put(USER_ID, health.getUserID());
        cv.put(DATETIME_COLUMN, health.getDatetime());
        cv.put(LASTMODIFY_COLUMN, health.getLastModify());
        cv.put(TEMPERATURE_COLUMN, health.getTemperature());
        cv.put(DRUNKWATER_COLUMN, health.getDrunkWater());
        cv.put(SYSTOLICBLOOD_COLUMN, health.getSystolicBloodPressure());
        cv.put(DIASTOLICBLOOD_COLUMN, health.getDiastolicBloodPressure());
        cv.put(PULSE_COLUMN, health.getPulse());

        String where = KEY_ID + "=" + health.getId();

        return this.db.update(TABLE_NAME, cv, where, null) > 0;
    }

    public List<Health> getAll() {
        List<Health> result = new ArrayList<>();
        Cursor cursor  = db.query(TABLE_NAME, null, null, null, null, null, null, null);

        while (cursor.moveToNext()) result.add(getRecord(cursor));

        cursor.close();
        return result;
    }

    public Health getRecord(Cursor cursor) {
        Health result = new Health();

        result.setId(cursor.getLong(0));
        result.setUserFBID(cursor.getLong(1));
        result.setDatetime(cursor.getLong(2));
        result.setLastModify(cursor.getLong(3));
        result.setTemperature(cursor.getFloat(4));
        result.setDrunkWater(cursor.getLong(5));
        result.setSystolicBloodPressure(cursor.getFloat(6));
        result.setDiastolicBloodPressure(cursor.getFloat(7));
        result.setPulse(cursor.getFloat(8));

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
