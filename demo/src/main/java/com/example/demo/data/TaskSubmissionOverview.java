package com.example.demo.data;

import com.example.demo.persistent.model.LessonTask;
import com.example.demo.persistent.model.SchoolClass;

public class TaskSubmissionOverview {
    private LessonTask task;
    private SchoolClass schoolClass;
    private int enrolledStudents;
    private int submittedCount;
    private long secondsRemaining; // remaining seconds until the task's due date

    // Getters and setters
    public LessonTask getTask() {
        return task;
    }
    public void setTask(LessonTask task) {
        this.task = task;
    }
    public SchoolClass getSchoolClass() {
        return schoolClass;
    }
    public void setSchoolClass(SchoolClass schoolClass) {
        this.schoolClass = schoolClass;
    }
    public int getEnrolledStudents() {
        return enrolledStudents;
    }
    public void setEnrolledStudents(int enrolledStudents) {
        this.enrolledStudents = enrolledStudents;
    }
    public int getSubmittedCount() {
        return submittedCount;
    }
    public void setSubmittedCount(int submittedCount) {
        this.submittedCount = submittedCount;
    }
    public long getSecondsRemaining() {
        return secondsRemaining;
    }
    public void setSecondsRemaining(long secondsRemaining) {
        this.secondsRemaining = secondsRemaining;
    }
}

