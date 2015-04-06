package com.finalproject.app.model;

import java.util.ArrayList;

/**
 * Timetable for classes
 *
 * Created by Kossy on 3/8/2015.
 */
public class ClassTable {

	public static String[] DAY_OF_WEEK = {"", "SUNDAY", "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY"};

    private Course course;

    private ArrayList <String> startTime, endTime, dayOfWeek;

    public ClassTable() {
    	endTime = new ArrayList <String>();
    	startTime = new ArrayList <String>();
    	dayOfWeek = new ArrayList <String>();
    }
    
    public void setCourse(Course course) {
        this.course = course;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek.add(dayOfWeek);
    }

    public void setStartTime(String startTime) {
        this.startTime.add(startTime);
    }

    public void setEndTime(String endTime) {
        this.endTime.add(endTime);
    }

    public Course getCourse() {
        return course;
    }

    public ArrayList <String> getDayOfWeek() {
        return dayOfWeek;
    }

    public ArrayList <String> getStartTime() {
        return startTime;
    }

    public ArrayList <String> getEndTime() {
        return endTime;
    }
}
