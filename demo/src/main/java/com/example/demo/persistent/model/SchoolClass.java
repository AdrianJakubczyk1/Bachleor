package com.example.demo.persistent.model;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class SchoolClass implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String description;
    private String content;
    private Boolean autoApprove;
    private Long teacherId;
    private LocalDateTime signupDeadline;

    // No‑args ctor for Hazelcast
    public SchoolClass() {}

    // Full‑args ctor
    public SchoolClass(Long id,
                       String name,
                       String description,
                       String content,
                       Boolean autoApprove,
                       Long teacherId,
                       LocalDateTime signupDeadline) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.content = content;
        this.autoApprove = autoApprove;
        this.teacherId = teacherId;
        this.signupDeadline = signupDeadline;
    }

    // Getters & setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Boolean getAutoApprove() { return autoApprove; }
    public void setAutoApprove(Boolean autoApprove) { this.autoApprove = autoApprove; }

    public Long getTeacherId() { return teacherId; }
    public void setTeacherId(Long teacherId) { this.teacherId = teacherId; }

    public LocalDateTime getSignupDeadline() { return signupDeadline; }
    public void setSignupDeadline(LocalDateTime signupDeadline) {
        this.signupDeadline = signupDeadline;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SchoolClass)) return false;
        SchoolClass that = (SchoolClass) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(description, that.description) &&
                Objects.equals(content, that.content) &&
                Objects.equals(autoApprove, that.autoApprove) &&
                Objects.equals(teacherId, that.teacherId) &&
                Objects.equals(signupDeadline, that.signupDeadline);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                id, name, description, content,
                autoApprove, teacherId, signupDeadline
        );
    }

    @Override
    public String toString() {
        return "SchoolClass{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", teacherId=" + teacherId +
                ", autoApprove=" + autoApprove +
                ", signupDeadline=" + signupDeadline +
                '}';
    }
}