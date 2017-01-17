package com.example.nthucs.prototype.Utility;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.nthucs.prototype.Activity.LoginActivity;
import com.example.nthucs.prototype.FoodList.CalorieDAO;
import com.example.nthucs.prototype.FoodList.FoodDAO;
import com.example.nthucs.prototype.Settings.MyProfileDAO;
import com.example.nthucs.prototype.SportList.SportDAO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class MyDBHelper extends SQLiteOpenHelper {

    // 資料庫名稱
    public static final String DATABASE_NAME = "food.db";

    // 資料庫版本，資料結構改變的時候要更改這個數字，通常是加一
    public static final int VERSION = 18;

    // 資料庫物件，固定的欄位變數
    private static SQLiteDatabase database;

    // 建構子，在一般的應用都不需要修改
    public MyDBHelper(Context context, String name, CursorFactory factory,
                      int version) {
        super(context, name, factory, version);
    }

    // 需要資料庫的元件呼叫這個方法，這個方法在一般的應用都不需要修改
    public static SQLiteDatabase getDatabase(Context context) {
        if (database == null || !database.isOpen()) {
            database = new MyDBHelper(context, DATABASE_NAME,
                    null, VERSION).getWritableDatabase();
        }

        return database;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 建立應用程式需要的表格
        db.execSQL(FoodDAO.CREATE_TABLE);

        // create calorie table
        db.execSQL(CalorieDAO.CREATE_TABLE);

        // create profile table
        db.execSQL(MyProfileDAO.CREATE_TABLE);

        // create sport table
        db.execSQL(SportDAO.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 刪除原有的表格
        db.execSQL("DROP TABLE IF EXISTS " + FoodDAO.TABLE_NAME);

        // delete origin calorie table
        db.execSQL("DROP TABLE IF EXISTS " + CalorieDAO.TABLE_NAME);

        // delete origin profile table
        db.execSQL("DROP TABLE IF EXISTS " + MyProfileDAO.TABLE_NAME);

        // delete origin sport table
        db.execSQL("DROP TABLE IF EXISTS " + SportDAO.TABLE_NAME);

        // 呼叫onCreate建立新版的表格
        onCreate(db);
    }
}
