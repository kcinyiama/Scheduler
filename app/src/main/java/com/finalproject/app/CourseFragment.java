package com.finalproject.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CourseFragment extends Fragment {

	private EditText nameEditText;
	private EditText codeEditText;
	private EditText unitEditText;
	private EditText locationEditText;
	private EditText instructorEditText;
	private Button addAnother;
	
	private OnCourseListener courseListener;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.add_course_layout, container, false);

		nameEditText = (EditText) rootView.findViewById(R.id.nameEditText);
		codeEditText = (EditText) rootView.findViewById(R.id.codeEditText);
		unitEditText = (EditText) rootView.findViewById(R.id.unitEditText);
		locationEditText = (EditText) rootView.findViewById(R.id.locationEditText);
		instructorEditText = (EditText) rootView.findViewById(R.id.instructorEditText);

		addAnother = (Button) rootView.findViewById(R.id.addAnother);
		addAnother.setOnClickListener(addClickListener);

		return rootView;
	}

	private OnClickListener addClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {

			String courseName = nameEditText.getText().toString();
			String courseCode = codeEditText.getText().toString();
			String courseUnit = unitEditText.getText().toString();
			String location = locationEditText.getText().toString();
			String instructor = instructorEditText.getText().toString();

			if ((courseName.length() > 0) && (courseCode.length() > 0) && (courseUnit.length() > 0)
					&& (location.length() > 0) && (instructor.length() > 0)) {

				if(courseListener != null) {
					
					courseListener.onCourseAdded(courseName, courseCode, courseUnit, location, instructor);
					
					nameEditText.setText("");
					codeEditText.setText("");
					unitEditText.setText("");
					locationEditText.setText("");
					instructorEditText.setText("");
					
					Toast.makeText(getActivity(), R.string.course_added, Toast.LENGTH_SHORT).show();
				}
			}
			else {
				Toast.makeText(getActivity(), R.string.incomplete_fields, Toast.LENGTH_SHORT).show();
			}
		}
	};
	
	public void setOnCourseListener(OnCourseListener listener) {
		this.courseListener = listener;
	}
	
	public interface OnCourseListener {
		
		public long onCourseAdded(String courseName, String courseCode, String courseUnit, String location, String instructor);
	}
}
