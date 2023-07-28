package com.example.classnavigator;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TimetableDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "timetable.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "Timetable";
    public static final String COLUMN_SUBJECT = "subject";
    public static final String COLUMN_CLASSROOM = "classroom";
    public static final String COLUMN_DAY_OF_WEEK = "day_of_week";
    public static final String COLUMN_PERIOD = "period";

    public TimetableDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        if (db != null) {
            String createTableQuery = "CREATE TABLE " + TABLE_NAME + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_DAY_OF_WEEK + " TEXT," +
                    COLUMN_PERIOD + " INTEGER," +
                    COLUMN_SUBJECT + " TEXT," +
                    COLUMN_CLASSROOM + " TEXT" +
                    ")";
            db.execSQL(createTableQuery);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // データベースのアップグレード処理が必要な場合に実装します
    }
}
