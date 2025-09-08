package com.example.demo.persistent.model;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class ClassSignUp implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long schoolClassId;
    private Long userId;
    private String status;
    private LocalDateTime createdDate;

    public ClassSignUp() { }

    // Full-args ctor
    public ClassSignUp(Long id, Long schoolClassId, Long userId,
                       String status, LocalDateTime createdDate) {
        this.id = id;
        this.schoolClassId = schoolClassId;
        this.userId = userId;
        this.status = status;
        this.createdDate = createdDate;
    }

    // Getters & setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getSchoolClassId() { return schoolClassId; }
    public void setSchoolClassId(Long schoolClassId) { this.schoolClassId = schoolClassId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClassSignUp)) return false;
        ClassSignUp that = (ClassSignUp) o;
        return Objects.equals(id, that.id)
                && Objects.equals(schoolClassId, that.schoolClassId)
                && Objects.equals(userId, that.userId)
                && Objects.equals(status, that.status)
                && Objects.equals(createdDate, that.createdDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, schoolClassId, userId, status, createdDate);
    }

    @Override
    public String toString() {
        return "ClassSignUp{" +
                "id=" + id +
                ", schoolClassId=" + schoolClassId +
                ", userId=" + userId +
                ", status='" + status + '\'' +
                ", createdDate=" + createdDate +
                '}';
    }
}