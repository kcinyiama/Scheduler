package com.finalproject.app;

import java.util.ArrayList;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.finalproject.app.data.CourseContract.ClassEntry;
import com.finalproject.app.data.CourseContract.CourseEntry;
import com.finalproject.app.util.Utility;

public class ViewFragment extends Fragment implements OnItemClickListener, AddActivity.OnCourseListener {

	private String TAG = "";
	private String toDelete = "";
	private ListView courseListView;
	private ArrayAdapter<CharSequence> courseAdapter;
	private ArrayAdapter<CharSequence> classAdapter;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		((AddActivity)getActivity()).setOnCourseListener(this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		Bundle bundle = getArguments();
		
		if(bundle != null) {
			
			TAG = bundle.getString("FRAGMENT");
		}
		
		View rootView = inflater.inflate(R.layout.view_courses_layout, container, false);
		
		courseListView = (ListView) rootView.findViewById(R.id.coursesListView);
		
		if(TAG.equals(Utility.DELETE_COURSE) || TAG.equals(Utility.DELETE_SCHEDULE)) {
			
			courseListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
			courseListView.setItemsCanFocus(false);
		}
		courseListView.setOnItemClickListener(this);

		populateListView();
		
		return rootView;
	}
	
	private void populateListView() {
		
		ArrayList <String> courseNames = new ArrayList <String>();
		ArrayList <Integer> courseIndex = new ArrayList <Integer>();
		ArrayList <String> classDetails = new ArrayList <String>();
		
		// query the db for all courses. We need just the name
		
		Cursor cursor = getActivity().getContentResolver().query(CourseEntry.CONTENT_URI, null, null, null, null);
		
		if(cursor.moveToFirst()) {
		
			do {
				String name = cursor.getString(cursor.getColumnIndex(CourseEntry.COLUMN_NAME));
				String code = cursor.getString(cursor.getColumnIndex(CourseEntry.COLUMN_CODE));
				int unit = cursor.getInt(cursor.getColumnIndex(CourseEntry.COLUMN_UNIT));
				
				courseIndex.add(cursor.getInt(cursor.getColumnIndex(CourseEntry._ID)));
				courseNames.add(name + "  " + code + "  " + unit);
			}
			while(cursor.moveToNext());
			
			cursor.close();
		}
		
		String[] namesArray = new String[courseNames.size()];
		namesArray = courseNames.toArray(namesArray);
		
		if(TAG.equals(Utility.DELETE_SCHEDULE)) {
			
			for(int i = 0; i < courseIndex.size(); i++) {
				
//				cursor = getActivity().getContentResolver().query
//						(ClassEntry.buildClassTableUri(courseIndex.get(i)), 
//								new String[]{ClassEntry.COLUMN_DAY, ClassEntry.COLUMN_STOP_TIME, ClassEntry.COLUMN_START_HOUR, ClassEntry.COLUMN_START_MIN}, 
//								null, null, null);
				
				cursor = getActivity().getContentResolver().query
						(ClassEntry.CONTENT_URI, 
								new String[]{ClassEntry.COLUMN_DAY, ClassEntry.COLUMN_STOP_TIME, ClassEntry.COLUMN_START_HOUR, ClassEntry.COLUMN_START_MIN}, 
								ClassEntry.COLUMN_COURSE_KEY + " = ?",
								new String[]{courseIndex.get(i) + ""}, null);
				
				if(cursor.moveToFirst()) {
			
					do {
						String stopTime = cursor.getString(cursor.getColumnIndex(ClassEntry.COLUMN_STOP_TIME));
						int startHr = cursor.getInt(cursor.getColumnIndex(ClassEntry.COLUMN_START_HOUR));
						int startMin = cursor.getInt(cursor.getColumnIndex(ClassEntry.COLUMN_START_MIN));
						String day = cursor.getString(cursor.getColumnIndex(ClassEntry.COLUMN_DAY));
						
						String builder = courseNames.get(i) + " ON (" + day + " at " + startHr + ":" + startMin + " to " + stopTime + ")\n";
						
						classDetails.add(builder);
					}
					while(cursor.moveToNext());
				}
			}
		}
		
		String[] classArray = new String[classDetails.size()];
		classArray = classDetails.toArray(classArray);
		
		if(TAG.equals(Utility.DELETE_SCHEDULE)){
			classAdapter = new ArrayAdapter <CharSequence>(getActivity(), android.R.layout.simple_list_item_multiple_choice, classArray);
			courseListView.setAdapter(classAdapter);
		}
		else if(TAG.equals(Utility.DELETE_COURSE)){
			courseAdapter = new ArrayAdapter <CharSequence>(getActivity(), android.R.layout.simple_list_item_multiple_choice, namesArray);
			courseListView.setAdapter(courseAdapter);
		}
		else {
			courseAdapter = new ArrayAdapter <CharSequence>(getActivity(), android.R.layout.simple_spinner_item, namesArray);
			courseListView.setAdapter(courseAdapter);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View arg1, int position, long arg3) {
		
		if(TAG.equals(Utility.DELETE_COURSE) || TAG.equals(Utility.DELETE_SCHEDULE)) {
			
//			String temp = ((String)parent.getItemAtPosition(position)).split(" ")[0] + " ";
//			
//			if(!toDelete.contains(temp))
//				toDelete += temp;
		}
		else {
		
			String name = ((String)parent.getItemAtPosition(position)).split(" ")[0].trim();
			
			Intent intent = new Intent(getActivity(), DetailActivity.class);
			intent.putExtra(Utility.COURSE_NAME, name);
			startActivity(intent);
		}
	}
	
	@Override
	public void onDeleteClicked() {

		SparseBooleanArray checkedItems = courseListView.getCheckedItemPositions();
		int SIZE = checkedItems.size();
		
		for(int i = 0; i < SIZE; i++) {
			
			int position = checkedItems.keyAt(i);
			boolean isChecked = checkedItems.valueAt(i);
			
			if(isChecked) {
				
				if(TAG.equals(Utility.DELETE_COURSE))
					toDelete += courseAdapter.getItem(position).toString().split(" ")[0] + "-";
				else
					toDelete += classAdapter.getItem(position).toString() + "-";
			}
		}
		
		if(TAG.equals(Utility.DELETE_COURSE) && toDelete.length() > 1) {
			
			String[] array = toDelete.split("-");
			
			for(int i = 0; i < array.length; i++) {
				
				String[] temp = array[i].split(" ");
				
				getActivity().getContentResolver().delete(
						CourseEntry.CONTENT_URI,
						CourseEntry.COLUMN_NAME + " = ?",
						new String[]{temp[0]});
				
				Toast.makeText(getActivity(), "course deleted", Toast.LENGTH_SHORT).show();
			}
		}
		else if(TAG.equals(Utility.DELETE_SCHEDULE) && toDelete.length() > 1) {
			
			String array[] = toDelete.split("-");
			
			for(int i = 0; i < array.length; i++) {
				
				String[] temp = array[i].split(" ");
				
				String day = temp[6].replace('(', ' ').trim();
				String[] start = temp[8].split(":");
				String stop = temp[10].replace(')', ' ').trim();
				
				getActivity().getContentResolver().delete(
						ClassEntry.CONTENT_URI, 
						ClassEntry.COLUMN_DAY + " = ? AND " +
						ClassEntry.COLUMN_START_HOUR + " = ? AND " + 
						ClassEntry.COLUMN_START_MIN + " = ? AND " + 
						ClassEntry.COLUMN_STOP_TIME + " = ?", new String[]{day, start[0], start[1], stop});
				
				Toast.makeText(getActivity(), "Schedule deleted", Toast.LENGTH_SHORT).show();
			}
		}
		
		populateListView();
	}
}
