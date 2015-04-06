package com.finalproject.app.model;

/**
 * Model class for courses
 *
 * Created by Kossy on 3/8/2015.
 */
public class Course {

    /**
     * The unit of the course
     */
    private int courseUnit;

    /**
     * The name of the course
     */
    private String nameOfCourse;

    /**
     * The code of the course
     */
    private String courseCode;

    /**
     * The instructor for the course to be taken
     */
    private String instructor;
    
    /**
     * The location/room where the course would be taken
     */
    private String location;
    
    /**
     * Sets the name of the instructor
     *
     * @param instructor the name of the instructor
     */
    public void setInstructor(String instructor) {
        this.instructor = instructor;
    }

    /**
     * Gets the name of the instructor
     *
     * @return the name of the instructor
     */
    public String getInstructor() {
        return instructor;
    }

    /**
     * Sets the location where the course will be taken
     * @param location the location where the course will be taken
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Gets the location where the course will be held
     *
     * @return the location
     */
    public String getLocation() {
        return location;
    }
    
    /**
     * Sets the name of the course
     *
     * @param nameOfCourse the name of the course
     */
    public void setNameOfCourse(String nameOfCourse) {
        this.nameOfCourse = nameOfCourse;
    }

    /**
     * Sets the code of the course
     *
     * @param courseCode the code of the course
     */
    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    /**
     * Sets the unit of the course
     *
     * @param courseUnit the unit of the course
     */
    public void setCourseUnit(int courseUnit) {
        this.courseUnit = courseUnit;
    }

    /**
     * Gets the name of the course
     *
     * @return the course's name
     */
    public String getNameOfCourse() {
        return nameOfCourse;
    }

    /**
     * Gets the code of the course
     *
     * @return the course's code
     */
    public String getCourseCode() {
        return courseCode;
    }

    /**
     * Gets the unit of the course
     *
     * @return the course's unit
     */
    public int getCourseUnit() {
        return courseUnit;
    }
}
