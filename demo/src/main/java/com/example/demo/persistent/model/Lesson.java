package com.example.demo.persistent.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;

public class Lesson implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long schoolClassId;
    private String title;
    private String description;
    private String content;
    private byte[] attachment;
    private String attachmentFilename;
    private String attachmentContentType;
    private Boolean solutionRequired;
    private LocalDateTime solutionDueDate;

    // No‑args ctor for Hazelcast serialization
    public Lesson() {}

    // Full‑args ctor
    public Lesson(Long id,
                  Long schoolClassId,
                  String title,
                  String description,
                  String content,
                  byte[] attachment,
                  String attachmentFilename,
                  String attachmentContentType,
                  Boolean solutionRequired,
                  LocalDateTime solutionDueDate) {
        this.id = id;
        this.schoolClassId = schoolClassId;
        this.title = title;
        this.description = description;
        this.content = content;
        this.attachment = attachment;
        this.attachmentFilename = attachmentFilename;
        this.attachmentContentType = attachmentContentType;
        this.solutionRequired = solutionRequired;
        this.solutionDueDate = solutionDueDate;
    }

    // Getters & setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getSchoolClassId() { return schoolClassId; }
    public void setSchoolClassId(Long schoolClassId) { this.schoolClassId = schoolClassId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public byte[] getAttachment() { return attachment; }
    public void setAttachment(byte[] attachment) { this.attachment = attachment; }

    public String getAttachmentFilename() { return attachmentFilename; }
    public void setAttachmentFilename(String attachmentFilename) { this.attachmentFilename = attachmentFilename; }

    public String getAttachmentContentType() { return attachmentContentType; }
    public void setAttachmentContentType(String attachmentContentType) { this.attachmentContentType = attachmentContentType; }

    public Boolean getSolutionRequired() { return solutionRequired; }
    public void setSolutionRequired(Boolean solutionRequired) { this.solutionRequired = solutionRequired; }

    public LocalDateTime getSolutionDueDate() { return solutionDueDate; }
    public void setSolutionDueDate(LocalDateTime solutionDueDate) { this.solutionDueDate = solutionDueDate; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Lesson)) return false;
        Lesson that = (Lesson) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(schoolClassId, that.schoolClassId) &&
                Objects.equals(title, that.title) &&
                Objects.equals(description, that.description) &&
                Objects.equals(content, that.content) &&
                Arrays.equals(attachment, that.attachment) &&
                Objects.equals(attachmentFilename, that.attachmentFilename) &&
                Objects.equals(attachmentContentType, that.attachmentContentType) &&
                Objects.equals(solutionRequired, that.solutionRequired) &&
                Objects.equals(solutionDueDate, that.solutionDueDate);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, schoolClassId, title, description, content,
                attachmentFilename, attachmentContentType,
                solutionRequired, solutionDueDate);
        result = 31 * result + Arrays.hashCode(attachment);
        return result;
    }

    @Override
    public String toString() {
        return "Lesson{" +
                "id=" + id +
                ", schoolClassId=" + schoolClassId +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", content='" + (content != null ? content.substring(0, Math.min(20, content.length())) + "…" : null) + '\'' +
                ", attachment=" + (attachment != null ? attachment.length + " bytes" : "null") +
                ", attachmentFilename='" + attachmentFilename + '\'' +
                ", attachmentContentType='" + attachmentContentType + '\'' +
                ", solutionRequired=" + solutionRequired +
                ", solutionDueDate=" + solutionDueDate +
                '}';
    }
}