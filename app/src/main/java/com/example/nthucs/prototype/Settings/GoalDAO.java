package com.example.nthucs.prototype.Settings;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.nthucs.prototype.Utility.MyDBHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SElab on 2017/12/10.
 */

public class GoalDAO {
    public static final String TABLE_NAME = "goal";

    public static final String KEY_ID = "_id";
    public static final String USER_ID = "userID";
    public static final String TIME = "time";
    public static final String STEP = "step";
    public static final String CALORIE = "calorie";
    public static final String DISTANCE = "distance";

    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                    USER_ID + " TEXT NOT NULL, "+
                    TIME + " INTEGER NOT NULL, "+
                    STEP + " INTEGER NOT NULL, " +
                    CALORIE + " INTEGER NOT NULL, "+
                    DISTANCE + " INTEGER NOT NULL)";

    private SQLiteDatabase db;

    public GoalDAO(Context context) { db = MyDBHelper.getDatabase(context); }

    public void close() { db.close();}

    public Goal insert(Goal gl) {
        ContentValues cv = new ContentValues();
        cv.put(USER_ID, gl.getUserID());
        cv.put(TIME, gl.getGoalTime());
        cv.put(STEP, gl.getGoalStep());
        cv.put(CALORIE, gl.getGoalBurn());
        cv.put(DISTANCE, gl.getGoalDist());

        long id = db.insert(TABLE_NAME, null, cv);
        gl.setId(id);

        return gl;
    }

    public boolean update(Goal gl) {
        ContentValues cv = new ContentValues();
        cv.put(USER_ID, gl.getUserID());
        cv.put(TIME, gl.getGoalTime());
        cv.put(STEP, gl.getGoalStep());
        cv.put(CALORIE, gl.getGoalBurn());
        cv.put(DISTANCE, gl.getGoalDist());

        String where = KEY_ID + "=" + gl.getId();

        return this.db.update(TABLE_NAME, cv, where, null) > 0;
    }

    public List<Goal> getAll() {
        List<Goal> result = new ArrayList<>();
        Cursor cursor  = db.query(TABLE_NAME, null, null, null, null, null, null, null);

        while (cursor.moveToNext()) result.add(getRecord(cursor));

        cursor.close();
        return result;
    }

    public Goal getCurrentGoal(long userId) {
        List<Goal> result = getAll();
        for (Goal gl : result) {
            if (gl.getUserID() == userId) return gl;
        }

        return null;
    }

    public Goal getRecord(Cursor cursor) {
        Goal result = new Goal();

        result.setId(cursor.getLong(0));
        result.setUserID(cursor.getLong(1));
        result.setGoalTime(cursor.getInt(2));
        result.setGoalStep(cursor.getInt(3));
        result.setGoalBurn(cursor.getInt(4));
        result.setGoalDist(cursor.getInt(5));

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
