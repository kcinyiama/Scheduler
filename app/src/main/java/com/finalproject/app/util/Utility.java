package com.finalproject.app.util;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import com.finalproject.app.AlarmReceiver;
import com.finalproject.app.data.CourseContract.ClassEntry;
import com.finalproject.app.data.CourseContract.CourseEntry;
import com.finalproject.app.model.ClassTable;

/**
 * Utility methods for easy course
 * 
 * Created by Kossy on 3/8/2015.
 */
public class Utility {

	public final static String COURSE_NAME = "1";

	public final static String VIEW = "100";
	public final static String ADD_COURSE = "101";
	public final static String ADD_SCHEDULE = "102";
	public final static String DELETE_COURSE = "103";
	public final static String DELETE_SCHEDULE = "104";

	public static String displaySchedule(ClassTable result) {
		ArrayList<String> days = result.getDayOfWeek();
		ArrayList<String> stopTime = result.getEndTime();
		ArrayList<String> startTime = result.getStartTime();

		StringBuilder builder = new StringBuilder();

		for (int i = 0; i < days.size(); i++) {

			builder.append(days.get(i)).append(
					"(" + startTime.get(i) + " - " + stopTime.get(i) + ") ");
		}

		return builder.toString();
	}

	public static String[] nextClass(Context context) {

		Calendar calendar = Calendar.getInstance();
		int hr = calendar.get(Calendar.HOUR_OF_DAY);
		int min = calendar.get(Calendar.MINUTE);
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

		String day = ClassTable.DAY_OF_WEEK[dayOfWeek];

		Cursor cursor = context.getContentResolver().query(
				ClassEntry.CONTENT_URI,

				new String[] { ClassEntry.COLUMN_COURSE_KEY,
						ClassEntry.COLUMN_START_HOUR,
						ClassEntry.COLUMN_START_MIN },

				ClassEntry.COLUMN_START_HOUR + " = ? AND "
						+ ClassEntry.COLUMN_START_MIN + " > ? AND "
						+ ClassEntry.COLUMN_DAY + " = ?",

				new String[] { hr + "", min + "", day },
				ClassEntry.COLUMN_START_HOUR + " ASC");

		int courseKey = -1;
		int startHr = -1, startMin = -1;
		String location = "";

		if (cursor.moveToFirst()) {

			startHr = cursor.getInt(cursor
					.getColumnIndex(ClassEntry.COLUMN_START_HOUR));
			startMin = cursor.getInt(cursor
					.getColumnIndex(ClassEntry.COLUMN_START_MIN));

			courseKey = cursor.getInt(cursor
					.getColumnIndex(ClassEntry.COLUMN_COURSE_KEY));
		}

		cursor.close();
		
		String name = "";

		if (courseKey != -1) {

			cursor = context.getContentResolver().query(
					CourseEntry.CONTENT_URI,
					new String[] { CourseEntry.COLUMN_NAME,
							CourseEntry.COLUMN_LOCATION },
					CourseEntry._ID + " = ?", new String[] { courseKey + "" },
					null);

			if (cursor.moveToFirst()) {

				name = cursor.getString(cursor
						.getColumnIndex(CourseEntry.COLUMN_NAME));
				location = cursor.getString(cursor
						.getColumnIndex(CourseEntry.COLUMN_LOCATION));
			}

			cursor.close();
			
			String rem = "";

			if (startHr != -1 && startMin != -1) {

				calendar.set(Calendar.MINUTE, startMin);
				calendar.set(Calendar.HOUR_OF_DAY, startHr);
				calendar.set(Calendar.SECOND, 0);

				rem = (startHr - hr) + " hr(s) " + (startMin - min) + " min(s)";

				Intent intent = new Intent(context, AlarmReceiver.class);
				intent.putExtra("course", name);
				intent.putExtra("location", location);

				PendingIntent pendingIntent = PendingIntent.getBroadcast(
						context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

				AlarmManager alarmManager = (AlarmManager) context
						.getSystemService(Context.ALARM_SERVICE);
				alarmManager.set(AlarmManager.RTC_WAKEUP,
						calendar.getTimeInMillis(), pendingIntent);

				return new String[] { name, rem, location };
			}
		}
		return null;
	}
}
