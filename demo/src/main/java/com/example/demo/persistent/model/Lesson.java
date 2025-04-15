package com.example.demo.persistent.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("lessons")
public class Lesson {

    @Id
    private Long id;

    // Foreign key: the school class this lesson belongs to
    private Long schoolClassId;

    private String title;

    // A short description of the lesson (optional)
    private String description;

    // Full text content of the lesson (could be TEXT in PostgreSQL)
    private String content;

    // Optional file attachment (if any)
    private byte[] attachment;
    private String attachmentFilename;
    private String attachmentContentType;
    private Boolean solutionRequired;
    private LocalDateTime solutionDueDate;

    // Getters and setters

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
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public byte[] getAttachment() {
        return attachment;
    }
    public void setAttachment(byte[] attachment) {
        this.attachment = attachment;
    }
    public String getAttachmentFilename() {
        return attachmentFilename;
    }
    public void setAttachmentFilename(String attachmentFilename) {
        this.attachmentFilename = attachmentFilename;
    }
    public String getAttachmentContentType() {
        return attachmentContentType;
    }
    public void setAttachmentContentType(String attachmentContentType) {
        this.attachmentContentType = attachmentContentType;
    }
    public Boolean getSolutionRequired() {
        return solutionRequired;
    }
    public void setSolutionRequired(Boolean solutionRequired) {
        this.solutionRequired = solutionRequired;
    }

    public LocalDateTime getSolutionDueDate() {
        return solutionDueDate;
    }
    public void setSolutionDueDate(LocalDateTime solutionDueDate) {
        this.solutionDueDate = solutionDueDate;
    }
}
