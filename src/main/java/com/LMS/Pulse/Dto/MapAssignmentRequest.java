package com.LMS.Pulse.Dto;

public class MapAssignmentRequest {
    private String courseName;
    private String jotformName;

    public String getCourseName() {
        return courseName;
    }
    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getJotformName() {
        return jotformName;
    }
    public void setJotformName(String jotformName) {
        this.jotformName = jotformName;
    }
}