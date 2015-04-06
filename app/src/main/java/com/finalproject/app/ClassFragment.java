package com.finalproject.app;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.finalproject.app.data.CourseContract.CourseEntry;

public class ClassFragment extends Fragment {
	
	private Spinner daySpinner;
	private Spinner courseSpinner;
	
	private Button stopTimeButton;
	private Button startTimeButton;
	private Button addTimeSchedule;

	private static boolean startClicked = false;
	
	private static TextView stopTimeView;
	private static TextView startTimeView;
	
	private OnScheduleListener onScheduleListener;
	
	private String courseCode, day, startTime, endTime;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.add_schedule_layout, container, false);

		daySpinner = (Spinner) rootView.findViewById(R.id.daySpinner);
		courseSpinner = (Spinner) rootView.findViewById(R.id.courseSpinner);

		addTimeSchedule = (Button) rootView.findViewById(R.id.addSchedule);
		stopTimeButton = (Button) rootView.findViewById(R.id.stopTimeButton);
		startTimeButton = (Button) rootView.findViewById(R.id.startTimeButton);
		
		stopTimeView = (TextView) rootView.findViewById(R.id.stopTimeView);
		startTimeView = (TextView) rootView.findViewById(R.id.startTimeView);
		
		daySpinner.setOnItemSelectedListener(daySpinnerListener);
		courseSpinner.setOnItemSelectedListener(courseSpinnerListener);
		
		addTimeSchedule.setOnClickListener(scheduleListener);
		stopTimeButton.setOnClickListener(showTimePickerDialog);
		startTimeButton.setOnClickListener(showTimePickerDialog);
		
		populateFields();

		return rootView;
	}

	private OnClickListener showTimePickerDialog = new OnClickListener() {

		@Override
		public void onClick(View v) {
			
			if(v.equals(startTimeButton)) {
				
				startClicked = true;
			}
			else {
				startClicked = false;
			}
			
			DialogFragment newFragment = new TimePickerFragment();
			newFragment.show(getActivity().getSupportFragmentManager(), "timePicker");
		}
	};

	private OnClickListener scheduleListener = new OnClickListener() {

		// add to the database
		
		@Override
		public void onClick(View v) {
					
			startTime = startTimeView.getText().toString().replaceAll(":", " ");
			endTime = stopTimeView.getText().toString();
			
			if(courseCode != null && day != null && startTime != null && endTime != null) {
			
				if(onScheduleListener != null) {
					
					onScheduleListener.onScheduleAdded(courseCode, day, startTime, endTime);
				}
				
				stopTimeView.setText("");
				startTimeView.setText("");
			}
			else {
				Toast.makeText(getActivity(), R.string.incomplete_fields, Toast.LENGTH_SHORT).show();
			}
		}
	};
	
	private OnItemSelectedListener daySpinnerListener = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> parent, View arg1, int position, long arg3) {
			day = (String) parent.getItemAtPosition(position);
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			day = null;
		}
	};

	private OnItemSelectedListener courseSpinnerListener = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> parent, View arg1, int position, long arg3) {
			courseCode = (String) parent.getItemAtPosition(position);
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			courseCode = null;
		}
	};

	
	public void setOnScheduleListener(OnScheduleListener listener) {
		this.onScheduleListener = listener;
	}
	
	private void populateFields() {
		
		// day spinner
		ArrayAdapter<CharSequence> dayAdapter = ArrayAdapter.createFromResource
				(getActivity(), R.array.days_of_week,android.R.layout.simple_spinner_item);

		dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		daySpinner.setAdapter(dayAdapter);

		
		ArrayList <String> courses = new ArrayList<String>();
		
		// get the courses from the database
		Cursor cursor = getActivity().getContentResolver().query(CourseEntry.CONTENT_URI, 
				new String[]{CourseEntry.COLUMN_CODE}, null, null, null);
		
		if(cursor.moveToFirst()) {
		
			do{
				courses.add(cursor.getString(cursor.getColumnIndex("code")));
			}
			while(cursor.moveToNext());
			
			cursor.close();
		}
		
		// convert to an array
		String[] coursesArray = new String[courses.size()];
		coursesArray = courses.toArray(coursesArray);
		
		// course spinner
		ArrayAdapter<CharSequence> courseAdapter = new ArrayAdapter <CharSequence>(getActivity(), 
				android.R.layout.simple_spinner_item, coursesArray);
		
		courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		courseSpinner.setAdapter(courseAdapter);
	}
	
	public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			final Calendar c = Calendar.getInstance();
			int hour = c.get(Calendar.HOUR_OF_DAY);
			int minute = c.get(Calendar.MINUTE);

			return new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));
		}

		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			
			String hourString = "";
			hourString = hourOfDay + "".length() == 1 ? "0" + hourString + hourOfDay : hourOfDay + "";
			
			if(startClicked) {
				
				// this time is the start time
				startTimeView.setText(hourString + ":" + minute);
			}
			else {
				stopTimeView.setText(hourString + ":" + minute);
			}
		}
	}
	
	public interface OnScheduleListener {
		
		public long onScheduleAdded(String courseCode, String day, String startTime, String endTime);
	}
}