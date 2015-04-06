package com.finalproject.app.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Kossy on 3/9/2015.
 */
public class CourseContract {

    // Name of the content provider
    public static final String CONTENT_AUTHORITY = "com.finalproject.app";

    // URI authority which will be used to interact with the ContentProvider
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_COURSE_TABLE = "course";
    public static final String PATH_CLASS_TABLE = "class";

    /**
     * Defines the content of the course table
     */
    public static final class CourseEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_COURSE_TABLE).build();

        // Indicates if the Uri returns a directory or an item
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_COURSE_TABLE;
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_COURSE_TABLE;

        public static final String TABLE_NAME = "course";

        // The column for the name of the course
        public static final String COLUMN_NAME = "name";

        // Column for the course code
        public static final String COLUMN_CODE = "code";

        // Column for the unit, stored as an integer
        public static final String COLUMN_UNIT = "unit";
        
        public static final String COLUMN_INSTRUCTOR = "instructor";
        
        public static final String COLUMN_LOCATION = "location";

        public static Uri buildCourseUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
        
        public static String getIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        } 
    }

    public static final class ClassEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_CLASS_TABLE).build();

        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_CLASS_TABLE;
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_CLASS_TABLE;

        public static final String TABLE_NAME = "class";

        // Column with foreign key to the course table
        public static final String COLUMN_COURSE_KEY = "course_id";

        // Time stored as TEXT in the format: HH:MM (24-hr clock)
        public static final String COLUMN_START_HOUR = "start_hour";
        public static final String COLUMN_START_MIN = "start_min";
        
        public static final String COLUMN_STOP_TIME = "stop_time";

        public static final String COLUMN_DAY = "day";

        public static Uri buildClassTableUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildClassTableWithDayAndTime(String day, String startTime) {
            return CONTENT_URI.buildUpon().appendPath(day).appendPath(startTime).build();
        }

        public static Uri buildClassTableWithDay(String day) {
            return CONTENT_URI.buildUpon().appendPath(day).build();
        }

        public static String getDayFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static String getTimeFromUri(Uri uri) {
            return uri.getPathSegments().get(2);
        }
    }

//    public static final class ReadEntry implements BaseColumns {
//
//        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_READ_TABLE).build();
//
//        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_READ_TABLE;
//        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_READ_TABLE;
//
//        public static final String TABLE_NAME = "read";
//
//        // Column with foreign key to the course table
//        public static final String COLUMN_COURSE_KEY = "course_id";
//
//        // Time stored as TEXT in the format: HH-MM (24-hr clock)
//        public static final String COLUMN_START_TIME = "start_time";
//        public static final String COLUMN_STOP_TIME = "stop_time";
//
//        public static final String COLUMN_DAY = "day";
//
//        public static Uri buildReadTableUri(long id) {
//            return ContentUris.withAppendedId(CONTENT_URI, id);
//        }
//
//        public static Uri buildReadTableWithDayAndTime(String day, String startTime) {
//            return CONTENT_URI.buildUpon().appendPath(day).appendPath(startTime).build();
//        }
//    }
}
