package com.finalproject.app.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.finalproject.app.data.CourseContract.ClassEntry;
import com.finalproject.app.data.CourseContract.CourseEntry;

/**
 * Created by Kossy on 3/9/2015.
 */
public class CourseDbHelper extends SQLiteOpenHelper {


    public static final String DATABASE_NAME = "course.db";
    private static final int DATABASE_VERSION = 1;

    public CourseDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String CREATE_COURSE_TABLE = "CREATE TABLE " +
                CourseEntry.TABLE_NAME + " (" +

                CourseEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                CourseEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                CourseEntry.COLUMN_CODE + " TEXT NOT NULL, " +
                CourseEntry.COLUMN_UNIT + " INTEGER NOT NULL, " + 
                CourseEntry.COLUMN_INSTRUCTOR + " TEXT NOT NULL, " + 
                CourseEntry.COLUMN_LOCATION + " TEXT NOT NULL, UNIQUE (" +

                CourseEntry.COLUMN_NAME + ", " +
                CourseEntry.COLUMN_CODE + ") ON CONFLICT REPLACE);";

        final String CREATE_CLASS_TABLE = "CREATE TABLE " +
                ClassEntry.TABLE_NAME + " (" +

                ClassEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                ClassEntry.COLUMN_COURSE_KEY + " INTEGER NOT NULL, " +

                ClassEntry.COLUMN_DAY + " TEXT NOT NULL, " +
                ClassEntry.COLUMN_START_HOUR + " INTEGER NOT NULL, " +
                ClassEntry.COLUMN_START_MIN + " INTEGER NOT NULL, " +
                ClassEntry.COLUMN_STOP_TIME + " TEXT NOT NULL, FOREIGN KEY (" +

                ClassEntry.COLUMN_COURSE_KEY + ") REFERENCES " +

                CourseEntry.TABLE_NAME + " (" + CourseEntry._ID + "));";

//        final String CREATE_READ_TABLE = "CREATE TABLE " +
//                ReadEntry.TABLE_NAME + " (" +
//
//                ReadEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
//
//                ReadEntry.COLUMN_COURSE_KEY + " INTEGER NOT NULL, " +
//                ReadEntry.COLUMN_DAY + " TEXT NOT NULL, " +
//                ReadEntry.COLUMN_START_TIME + " TEXT NOT NULL, " +
//                ReadEntry.COLUMN_STOP_TIME + " TEXT NOT NULL, FOREIGN KEY (" +
//
//                ReadEntry.COLUMN_COURSE_KEY + ") REFERENCES " +
//
//                CourseEntry.TABLE_NAME + " (" +
//                CourseEntry._ID + "), UNIQUE (" +
//
//                ReadEntry.COLUMN_DAY + ") ON CONFLICT IGNORE);";

        db.execSQL(CREATE_COURSE_TABLE);
        db.execSQL(CREATE_CLASS_TABLE);
//        db.execSQL(CREATE_READ_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + CourseEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ClassEntry.TABLE_NAME);
//        db.execSQL("DROP TABLE IF EXISTS " + ReadEntry.TABLE_NAME);

        onCreate(db);
    }
}
