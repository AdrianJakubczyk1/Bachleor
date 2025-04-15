package com.example.demo;

import java.time.LocalDateTime;

public class ClassSignUpDetail {

    private Long signUpId;
    private Long studentId;
    private String firstName;
    private String lastName;
    private String status;
    private LocalDateTime createdDate;

    // Getters and Setters

    public Long getSignUpId() {
        return signUpId;
    }
    public void setSignUpId(Long signUpId) {
        this.signUpId = signUpId;
    }
    public Long getStudentId() {
        return studentId;
    }
    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public LocalDateTime getCreatedDate() {
        return createdDate;
    }
    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }
}