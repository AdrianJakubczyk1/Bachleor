package com.example.demo.persistent.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;

public class TaskSubmission implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long lessonTaskId;
    private Long userId;
    private String solutionText;
    private byte[] solutionFile;
    private String solutionFilename;
    private String solutionContentType;
    private LocalDateTime submittedDate;
    private Double grade;
    private String teacherComments;

    public TaskSubmission() {}

    public TaskSubmission(Long id,
                          Long lessonTaskId,
                          Long userId,
                          String solutionText,
                          byte[] solutionFile,
                          String solutionFilename,
                          String solutionContentType,
                          LocalDateTime submittedDate,
                          Double grade,
                          String teacherComments) {
        this.id = id;
        this.lessonTaskId = lessonTaskId;
        this.userId = userId;
        this.solutionText = solutionText;
        this.solutionFile = solutionFile;
        this.solutionFilename = solutionFilename;
        this.solutionContentType = solutionContentType;
        this.submittedDate = submittedDate;
        this.grade = grade;
        this.teacherComments = teacherComments;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TaskSubmission)) return false;
        TaskSubmission that = (TaskSubmission) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(lessonTaskId, that.lessonTaskId) &&
                Objects.equals(userId, that.userId) &&
                Objects.equals(solutionText, that.solutionText) &&
                Arrays.equals(solutionFile, that.solutionFile) &&
                Objects.equals(solutionFilename, that.solutionFilename) &&
                Objects.equals(solutionContentType, that.solutionContentType) &&
                Objects.equals(submittedDate, that.submittedDate) &&
                Objects.equals(grade, that.grade) &&
                Objects.equals(teacherComments, that.teacherComments);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, lessonTaskId, userId, solutionText, solutionFilename, solutionContentType, submittedDate, grade, teacherComments);
        result = 31 * result + Arrays.hashCode(solutionFile);
        return result;
    }

    @Override
    public String toString() {
        return "TaskSubmission{" +
                "id=" + id +
                ", lessonTaskId=" + lessonTaskId +
                ", userId=" + userId +
                ", submittedDate=" + submittedDate +
                ", grade=" + grade +
                '}';
    }
}
