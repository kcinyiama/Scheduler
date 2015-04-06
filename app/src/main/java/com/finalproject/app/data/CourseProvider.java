package com.finalproject.app.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import com.finalproject.app.data.CourseContract.ClassEntry;
import com.finalproject.app.data.CourseContract.CourseEntry;

/**
 * Created by Kossy on 3/9/2015.
 */
public class CourseProvider extends ContentProvider {

	public static final String LOG_TAG = CourseProvider.class.getSimpleName();

	private static final int COURSE_ID = 101;
	private static final int CLASS_TABLE_ID = 102;

	private static final int CLASS_TABLE = 105;
	private static final int COURSE_TABLE = 106;

	private static final int COURSE_DETAILS = 107;

	private static final UriMatcher sUriMatcher = buildUriMatcher();

	private static final SQLiteQueryBuilder classCourseBuilder;

	static {

		classCourseBuilder = new SQLiteQueryBuilder();
		classCourseBuilder.setTables(ClassEntry.TABLE_NAME + " INNER JOIN "
				+ CourseEntry.TABLE_NAME + " ON " + ClassEntry.TABLE_NAME + "."
				+ ClassEntry.COLUMN_COURSE_KEY + " = " + CourseEntry.TABLE_NAME
				+ "." + CourseEntry._ID);
	}

	// private Cursor getClassCourseFromTime(Uri uri, String[] projection,
	// String sortOrder) {
	// // should get the course code from the course table via the id
	// // specified in the read table
	//
	// String selection = ClassEntry.COLUMN_START_TIME + " = ? AND " +
	// ClassEntry.COLUMN_DAY + " = ?";
	// String[] selectionArgs = new String[]{ClassEntry.getTimeFromUri(uri),
	// ClassEntry.getDayFromUri(uri)};
	//
	// return classCourseBuilder.query(courseDbHelper.getReadableDatabase(),
	// projection,
	// selection,
	// selectionArgs,
	// null,
	// null,
	// sortOrder
	// );
	// }

//	private Cursor getCourseDetails(Uri uri, String[] projection,
//			String sortOrder) {
//
//		String selection = ClassEntry.COLUMN_COURSE_KEY + " = ?";
//		String[] selectionArgs = new String[] { ContentUris.parseId(uri) + "" };
//
//		return classCourseBuilder.query(courseDbHelper.getReadableDatabase(),
//				projection, selection, selectionArgs, null, null, sortOrder);
//	}

	private CourseDbHelper courseDbHelper;

	private static UriMatcher buildUriMatcher() {

		final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
		final String authority = CourseContract.CONTENT_AUTHORITY;

		matcher.addURI(authority, CourseContract.PATH_COURSE_TABLE + "/#",
				COURSE_ID);
		matcher.addURI(authority, CourseContract.PATH_CLASS_TABLE + "/#",
				CLASS_TABLE_ID);

		matcher.addURI(authority, CourseContract.PATH_COURSE_TABLE,
				COURSE_TABLE);
		matcher.addURI(authority, CourseContract.PATH_CLASS_TABLE, CLASS_TABLE);

		matcher.addURI(authority, CourseContract.PATH_COURSE_TABLE + "/*",
				COURSE_DETAILS);
		
		return matcher;
	}

	@Override
	public boolean onCreate() {
		courseDbHelper = new CourseDbHelper(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {

		Cursor cursor;
		int match = sUriMatcher.match(uri);

		switch (match) {

		case CLASS_TABLE:
			cursor = courseDbHelper.getReadableDatabase().query(
					ClassEntry.TABLE_NAME, projection, selection,
					selectionArgs, null, null, sortOrder);
			break;

		case COURSE_TABLE:
			cursor = courseDbHelper.getReadableDatabase().query(
					CourseEntry.TABLE_NAME, projection, selection,
					selectionArgs, null, null, sortOrder);
			break;

		// case COURSE_ID:
		// cursor = courseDbHelper.getReadableDatabase().query
		// (CourseEntry.TABLE_NAME,
		// projection,
		// CourseEntry._ID + " = " + ContentUris.parseId(uri),
		// selectionArgs,
		// null,
		// null,
		// sortOrder);
		// break;

		case CLASS_TABLE_ID:
			int ID = (int) ContentUris.parseId(uri);

			cursor = courseDbHelper.getReadableDatabase().query(
					ClassEntry.TABLE_NAME, projection,
					ClassEntry.COLUMN_COURSE_KEY + " = " + ID, selectionArgs,
					null, null, sortOrder);
			break;

		default:
			throw new UnsupportedOperationException("Unknown uri: " + uri);
		}

		if (cursor != null)
			cursor.setNotificationUri(getContext().getContentResolver(), uri);

		return cursor;
	}

	@Override
	public String getType(Uri uri) {
		final int type = sUriMatcher.match(uri);

		switch (type) {

		case CLASS_TABLE:
			return ClassEntry.CONTENT_TYPE;

		case COURSE_TABLE:
			return CourseEntry.CONTENT_TYPE;

		case CLASS_TABLE_ID:
			return ClassEntry.CONTENT_ITEM_TYPE;

		default:
			throw new UnsupportedOperationException("Unknown Uri " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		final SQLiteDatabase db = courseDbHelper.getWritableDatabase();
		final int match = sUriMatcher.match(uri);

		Uri returnUri;

		// using only the root uri ensures that the descendant uris are also
		// notified
		switch (match) {

		case COURSE_TABLE:
			long _courseID = db.insert(CourseEntry.TABLE_NAME, null, values);

			if (_courseID > 0) {
				returnUri = CourseEntry.buildCourseUri(_courseID);
			} else {
				throw new UnsupportedOperationException(
						"Failed to insert row into: " + uri);
			}
			break;

		case CLASS_TABLE:
			long _classID = db.insert(ClassEntry.TABLE_NAME, null, values);

			if (_classID > 0) {
				returnUri = ClassEntry.buildClassTableUri(_classID);
			} else {
				throw new SQLException("Failed to insert row into: " + uri);
			}
			break;

		default:
			throw new UnsupportedOperationException("Unknown Uri: " + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null);

		return returnUri;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		final SQLiteDatabase db = courseDbHelper.getWritableDatabase();
		final int match = sUriMatcher.match(uri);
		int rowsDeleted;

		// using only the root uri ensures that the descendant uris are also
		// notified
		switch (match) {

		case COURSE_TABLE:
			// deleting the courses deletes them from the timetable also

			// get the course index
			Cursor cursor = courseDbHelper.getReadableDatabase().query(
					CourseEntry.TABLE_NAME, new String[] { CourseEntry._ID },
					selection, selectionArgs, null, null, null);

			int courseIndex = -1;

			if (cursor.moveToFirst()) {
				Log.v(LOG_TAG, "Course exists, that's good");
				courseIndex = cursor.getInt(cursor
						.getColumnIndex(CourseEntry._ID));
			}
			cursor.close();

			// now delete it from the class table and the course table
			deleteFromClassTable(ClassEntry.COLUMN_COURSE_KEY + " = ?",
					new String[] { courseIndex + "" });

			rowsDeleted = db.delete(CourseEntry.TABLE_NAME, selection,
					selectionArgs);
			break;

		case CLASS_TABLE:

			rowsDeleted = deleteFromClassTable(selection, selectionArgs);
			break;

		default:
			throw new UnsupportedOperationException("Unknown Uri: " + uri);
		}

		if (selection == null || rowsDeleted != 0)
			getContext().getContentResolver().notifyChange(uri, null);

		return rowsDeleted;
	}

	private int deleteFromClassTable(String selection, String[] selectionArgs) {
		return courseDbHelper.getWritableDatabase().delete(
				ClassEntry.TABLE_NAME, selection, selectionArgs);
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		final SQLiteDatabase db = courseDbHelper.getWritableDatabase();
		final int match = sUriMatcher.match(uri);
		int rowsUpdated;

		// using only the root uri ensures that the descendant uris are also
		// notified
		switch (match) {

		case COURSE_TABLE:
			rowsUpdated = db.update(CourseEntry.TABLE_NAME, values, selection,
					selectionArgs);
			break;

		case CLASS_TABLE:
			rowsUpdated = db.update(ClassEntry.TABLE_NAME, values, selection,
					selectionArgs);
			break;

		default:
			throw new UnsupportedOperationException("Unknown Uri: " + uri);
		}

		if (rowsUpdated != 0)
			getContext().getContentResolver().notifyChange(uri, null);

		return rowsUpdated;
	}
}
