package com.example.demo.persistent.model;

import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("lesson_tasks")
public class LessonTask {

    @Id
    private Long id;

    // The lesson to which this task belongs.
    private Long lessonId;

    private String title;
    private String description;

    // Maximum date to upload a solution.
    private LocalDateTime dueDate;

    // If true, a solution is required; if false, the solution is optional.
    private Boolean solutionRequired;

    // Getters and setters

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getLessonId() {
        return lessonId;
    }
    public void setLessonId(Long lessonId) {
        this.lessonId = lessonId;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public LocalDateTime getDueDate() {
        return dueDate;
    }
    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }
    public Boolean getSolutionRequired() {
        return solutionRequired;
    }
    public void setSolutionRequired(Boolean solutionRequired) {
        this.solutionRequired = solutionRequired;
    }
}
