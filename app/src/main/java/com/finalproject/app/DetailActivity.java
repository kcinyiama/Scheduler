package com.finalproject.app;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.finalproject.app.data.CourseContract.ClassEntry;
import com.finalproject.app.data.CourseContract.CourseEntry;
import com.finalproject.app.model.ClassTable;
import com.finalproject.app.model.Course;
import com.finalproject.app.util.Utility;

public class DetailActivity extends Activity {

//	private final static String LOG_TAG = DetailActivity.class.getSimpleName();

	private TextView nameTextView;
	private TextView codeTextView;
	private TextView unitTextView;
	private TextView periodTextView;
	private TextView locationTextView;
	private TextView instructorTextView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_layout);
        
		Intent intent = getIntent();

		if (intent != null) {
			String courseName = intent.getStringExtra(Utility.COURSE_NAME);
			
			// query the db from the course code
			// Let's run it in an async task
			new QueryDB().execute(courseName);
		}
		
		nameTextView = (TextView) this.findViewById(R.id.nameTextView);
		codeTextView = (TextView) this.findViewById(R.id.codeTextView);
		unitTextView = (TextView) this.findViewById(R.id.unitTextView);
		periodTextView = (TextView) this.findViewById(R.id.periodTextView);
		locationTextView = (TextView) this.findViewById(R.id.locationTextView);
		instructorTextView = (TextView) this.findViewById(R.id.instructorTextView);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		int id = item.getItemId();

		switch (id) {
			
		case android.R.id.home:
			
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	public class QueryDB extends AsyncTask<String, ClassTable, ClassTable> {

		@Override
		protected ClassTable doInBackground(String... params) {
			
			Course c = new Course();
			ClassTable table = new ClassTable();

			Cursor cursor = DetailActivity.this.getContentResolver().query(CourseEntry.CONTENT_URI, null, CourseEntry.COLUMN_NAME + " = ?", 
					new String[]{params[0]}, null);

			int courseRowId = -1;
			
			if(cursor.moveToFirst()) {
				
				c.setNameOfCourse(params[0]);
				c.setCourseUnit(cursor.getInt(cursor.getColumnIndex(CourseEntry.COLUMN_UNIT)));
				c.setCourseCode(cursor.getString(cursor.getColumnIndex(CourseEntry.COLUMN_CODE)));
				c.setLocation(cursor.getString(cursor.getColumnIndex(CourseEntry.COLUMN_LOCATION)));
				c.setInstructor(cursor.getString(cursor.getColumnIndex(CourseEntry.COLUMN_INSTRUCTOR)));
				table.setCourse(c);
				
				courseRowId = cursor.getInt(cursor.getColumnIndex(CourseEntry._ID));
				
				cursor.close();
			}
			
			if(courseRowId != -1) {
			
				cursor = DetailActivity.this.getContentResolver().query
						(ClassEntry.buildClassTableUri(courseRowId), 
								new String[]{ClassEntry.COLUMN_DAY, ClassEntry.COLUMN_STOP_TIME, ClassEntry.COLUMN_START_HOUR, ClassEntry.COLUMN_START_MIN}, 
								null, null, null);
				
				if(cursor.moveToFirst()) {
			
					do {
						String stopTime = cursor.getString(cursor.getColumnIndex(ClassEntry.COLUMN_STOP_TIME));
						int startHr = cursor.getInt(cursor.getColumnIndex(ClassEntry.COLUMN_START_HOUR));
						int startMin = cursor.getInt(cursor.getColumnIndex(ClassEntry.COLUMN_START_MIN));
						String day = cursor.getString(cursor.getColumnIndex(ClassEntry.COLUMN_DAY));
						
						table.setDayOfWeek(day);
						table.setEndTime(stopTime);
						table.setStartTime(startHr + ":" + startMin);
					}
					while(cursor.moveToNext());
				}
			}
			
			return table;
		}
		
		@Override
		protected void onPostExecute(ClassTable result) {
			
			Course course = result.getCourse();
			
			// update the ui
			final String PRE_PERIOD = getResources().getString(R.string.period);
			final String PRE_NAME = getResources().getString(R.string.course_name);
			final String PRE_CODE = getResources().getString(R.string.course_code);
			final String PRE_UNIT = getResources().getString(R.string.course_unit);
			final String PRE_LOCATION = getResources().getString(R.string.location);
			final String PRE_INSTRUCTOR = getResources().getString(R.string.instructor);
			
			nameTextView.setText(PRE_NAME + ": " + course.getNameOfCourse());
			codeTextView.setText(PRE_CODE + ": " + course.getCourseCode());
			unitTextView.setText(PRE_UNIT + ": " + course.getCourseUnit());
			periodTextView.setText(PRE_PERIOD + ": " + Utility.displaySchedule(result));
			locationTextView.setText(PRE_LOCATION + ": " + course.getLocation());
			instructorTextView.setText(PRE_INSTRUCTOR + ": " + course.getInstructor());
		}	
	}
}
