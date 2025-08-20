package com.example.demo.persistent.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class LessonTask implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long lessonId;
    private String title;
    private String description;
    private LocalDateTime dueDate;
    private Boolean solutionRequired;

    // No‑args ctor for Hazelcast serialization
    public LessonTask() {}

    // Full‑args ctor
    public LessonTask(Long id,
                      Long lessonId,
                      String title,
                      String description,
                      LocalDateTime dueDate,
                      Boolean solutionRequired) {
        this.id = id;
        this.lessonId = lessonId;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.solutionRequired = solutionRequired;
    }

    // Getters & setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getLessonId() { return lessonId; }
    public void setLessonId(Long lessonId) { this.lessonId = lessonId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getDueDate() { return dueDate; }
    public void setDueDate(LocalDateTime dueDate) { this.dueDate = dueDate; }

    public Boolean getSolutionRequired() { return solutionRequired; }
    public void setSolutionRequired(Boolean solutionRequired) {
        this.solutionRequired = solutionRequired;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LessonTask)) return false;
        LessonTask that = (LessonTask) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(lessonId, that.lessonId) &&
                Objects.equals(title, that.title) &&
                Objects.equals(description, that.description) &&
                Objects.equals(dueDate, that.dueDate) &&
                Objects.equals(solutionRequired, that.solutionRequired);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, lessonId, title, description, dueDate, solutionRequired);
    }

    @Override
    public String toString() {
        return "LessonTask{" +
                "id=" + id +
                ", lessonId=" + lessonId +
                ", title='" + title + '\'' +
                ", description='" + (description != null ? description.substring(0, Math.min(20, description.length())) + "…" : null) + '\'' +
                ", dueDate=" + dueDate +
                ", solutionRequired=" + solutionRequired +
                '}';
    }
}