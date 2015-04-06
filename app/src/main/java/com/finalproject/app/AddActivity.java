package com.finalproject.app;

import java.util.List;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.finalproject.app.data.CourseContract;
import com.finalproject.app.data.CourseContract.ClassEntry;
import com.finalproject.app.data.CourseContract.CourseEntry;
import com.finalproject.app.util.Utility;

public class AddActivity extends FragmentActivity implements ClassFragment.OnScheduleListener, CourseFragment.OnCourseListener {
	
	public static final String LOG_TAG = AddActivity.class.getSimpleName();
	
	private String whichFragment;
	
	private OnCourseListener courseListener;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_layout);

		whichFragment = getIntent().getStringExtra("FRAGMENT");

		FragmentManager fragmentManager = getSupportFragmentManager();

		/*
		 * Remove all the Fragments to avoid duplicates if any
		 */
		List<Fragment> allFragment = fragmentManager.getFragments();

		if (allFragment != null) {
			for (Fragment fragment : allFragment) {

				getSupportFragmentManager().beginTransaction().remove(fragment).commit();
			}
		}
		
		/*
		 * Add the required fragment
		 */
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

		if (whichFragment.equals(Utility.ADD_COURSE)) {

			CourseFragment courseFragment = new CourseFragment();
			courseFragment.setOnCourseListener(this);
			
			fragmentTransaction.add(R.id.container, courseFragment).commit();
		}
		else if (whichFragment.equals(Utility.ADD_SCHEDULE)) {

			ClassFragment classFragment = new ClassFragment();
			classFragment.setOnScheduleListener(this);
			
			fragmentTransaction.add(R.id.container, classFragment).commit();
		}
		else {
			
			ViewFragment viewFragment = new ViewFragment();
			
			Bundle b = new Bundle();
			b.putString("FRAGMENT", whichFragment);
			viewFragment.setArguments(b);
			
			fragmentTransaction.add(R.id.container, viewFragment).commit();
		}
	}

	@Override
	public long onScheduleAdded(String courseCode, String day, String startTime, String endTime) {
		
		Cursor cursor = this.getContentResolver().query(CourseEntry.CONTENT_URI, new String[]{CourseEntry._ID}, 
				CourseEntry.COLUMN_CODE + " = ?", new String[]{courseCode}, null);
		
		int courseKey = -1;
		
		if(cursor.moveToFirst()) {
			Log.e(LOG_TAG, "Course exists, that's good");
			courseKey = cursor.getInt(cursor.getColumnIndex(CourseEntry._ID));
		}
		
		cursor.close();
		
		long classRowId = -1;
		if(courseKey != -1) {
			ContentValues values = new ContentValues();			
			values.put(ClassEntry.COLUMN_DAY, day.toUpperCase());
			values.put(ClassEntry.COLUMN_STOP_TIME, endTime.toUpperCase());
			values.put(ClassEntry.COLUMN_START_HOUR, startTime.split(" ")[0]);
			values.put(ClassEntry.COLUMN_START_MIN, startTime.split(" ")[1]);
			values.put(ClassEntry.COLUMN_COURSE_KEY, courseKey);
			
			Log.v(LOG_TAG, "Insertting new schedule");
			
			Uri classRowUri = this.getContentResolver().insert(CourseContract.ClassEntry.CONTENT_URI, values);
			
			classRowId = ContentUris.parseId(classRowUri);
			
			Toast.makeText(this, R.string.schedule_added, Toast.LENGTH_SHORT).show();
		}
		
		return classRowId;
	}
	
	@Override
	public long onCourseAdded(String courseName, String courseCode, String courseUnit, String location, String instructor) {
		// add to the database
		
		ContentValues values = new ContentValues();
		values.put(CourseEntry.COLUMN_NAME, courseName.toUpperCase());
		values.put(CourseEntry.COLUMN_CODE, courseCode.toUpperCase());
		values.put(CourseEntry.COLUMN_UNIT, courseUnit);
		values.put(CourseEntry.COLUMN_LOCATION, location.toUpperCase());
		values.put(CourseEntry.COLUMN_INSTRUCTOR, instructor.toUpperCase());
				
		Log.v(LOG_TAG, "Insertting new course");
		
		Uri courseRowUri = this.getContentResolver().insert(CourseContract.CourseEntry.CONTENT_URI, values);
		
		long courseRowId = ContentUris.parseId(courseRowUri);
		
		return courseRowId;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		if(whichFragment.equals(Utility.DELETE_COURSE) || whichFragment.equals(Utility.DELETE_SCHEDULE)) {
			
			getMenuInflater().inflate(R.menu.action_menu, menu);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		if(item.getItemId() == R.id.delete_action) {
			
			if(courseListener != null) {
				
				courseListener.onDeleteClicked();
			}
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void setOnCourseListener(OnCourseListener listener) {
		this.courseListener = listener;
	}
	
	public interface OnCourseListener {
		
		public void onDeleteClicked();
	}
}



