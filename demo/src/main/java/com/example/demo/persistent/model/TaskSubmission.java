package com.example.demo.persistent.model;

import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("task_submissions")
public class TaskSubmission {

    @Id
    private Long id;

    // Foreign key to the lesson task
    private Long lessonTaskId;

    // Student's user ID – assuming a numeric ID for the student
    private Long userId;

    // Optional file data or solution text (for simplicity, using TEXT for solution content)
    private String solutionText;

    // Alternatively, if you expect file uploads, you could use a byte[] field.
    private byte[] solutionFile;
    private String solutionFilename;
    private String solutionContentType;

    private LocalDateTime submittedDate;

    // A grade, for example numeric grade. Null if not yet graded.
    private Double grade;

    // Teacher comments for the submission (optional)
    private String teacherComments;

    // Getters and Setters

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Long getLessonTaskId() {
        return lessonTaskId;
    }
    public void setLessonTaskId(Long lessonTaskId) {
        this.lessonTaskId = lessonTaskId;
    }

    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getSolutionText() {
        return solutionText;
    }
    public void setSolutionText(String solutionText) {
        this.solutionText = solutionText;
    }

    public byte[] getSolutionFile() {
        return solutionFile;
    }
    public void setSolutionFile(byte[] solutionFile) {
        this.solutionFile = solutionFile;
    }

    public String getSolutionFilename() {
        return solutionFilename;
    }
    public void setSolutionFilename(String solutionFilename) {
        this.solutionFilename = solutionFilename;
    }

    public String getSolutionContentType() {
        return solutionContentType;
    }
    public void setSolutionContentType(String solutionContentType) {
        this.solutionContentType = solutionContentType;
    }

    public LocalDateTime getSubmittedDate() {
        return submittedDate;
    }
    public void setSubmittedDate(LocalDateTime submittedDate) {
        this.submittedDate = submittedDate;
    }

    public Double getGrade() {
        return grade;
    }
    public void setGrade(Double grade) {
        this.grade = grade;
    }

    public String getTeacherComments() {
        return teacherComments;
    }
    public void setTeacherComments(String teacherComments) {
        this.teacherComments = teacherComments;
    }
}
