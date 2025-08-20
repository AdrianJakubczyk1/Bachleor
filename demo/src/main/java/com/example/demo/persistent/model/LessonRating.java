package com.example.demo.persistent.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class LessonRating implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long lessonId;
    private Long userId;
    private int rating;
    private LocalDateTime createdDate;

    // No‑args ctor for Hazelcast serialization
    public LessonRating() {}

    // Full‑args ctor
    public LessonRating(Long id,
                        Long lessonId,
                        Long userId,
                        int rating,
                        LocalDateTime createdDate) {
        this.id = id;
        this.lessonId = lessonId;
        this.userId = userId;
        this.rating = rating;
        this.createdDate = createdDate;
    }

    // Getters & setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getLessonId() { return lessonId; }
    public void setLessonId(Long lessonId) { this.lessonId = lessonId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LessonRating)) return false;
        LessonRating that = (LessonRating) o;
        return rating == that.rating &&
                Objects.equals(id, that.id) &&
                Objects.equals(lessonId, that.lessonId) &&
                Objects.equals(userId, that.userId) &&
                Objects.equals(createdDate, that.createdDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, lessonId, userId, rating, createdDate);
    }

    @Override
    public String toString() {
        return "LessonRating{" +
                "id=" + id +
                ", lessonId=" + lessonId +
                ", userId=" + userId +
                ", rating=" + rating +
                ", createdDate=" + createdDate +
                '}';
    }
}