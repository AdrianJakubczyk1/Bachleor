package com.example.demo.persistent.model;

import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("class_signups")
public class ClassSignUp {
    @Id
    private Long id;

    private Long schoolClassId;
    private Long userId;
    private String status;

    private LocalDateTime createdDate;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getSchoolClassId() {
        return schoolClassId;
    }
    public void setSchoolClassId(Long schoolClassId) {
        this.schoolClassId = schoolClassId;
    }
    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
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
