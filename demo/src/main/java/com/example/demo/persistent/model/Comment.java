package com.example.demo.persistent.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class Comment implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long postId;
    private String username;
    private String text;
    private Integer likes;
    private LocalDateTime createdDate;
    public Comment() { }

    public Comment(Long id, Long postId, String username,
                   String text, Integer likes, LocalDateTime createdDate) {
        this.id = id;
        this.postId = postId;
        this.username = username;
        this.text = text;
        this.likes = likes;
        this.createdDate = createdDate;
    }

    // Getters & setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPostId() { return postId; }
    public void setPostId(Long postId) { this.postId = postId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public Integer getLikes() { return likes; }
    public void setLikes(Integer likes) { this.likes = likes; }

    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Comment)) return false;
        Comment that = (Comment) o;
        return Objects.equals(id, that.id)
                && Objects.equals(postId, that.postId)
                && Objects.equals(username, that.username)
                && Objects.equals(text, that.text)
                && Objects.equals(likes, that.likes)
                && Objects.equals(createdDate, that.createdDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, postId, username, text, likes, createdDate);
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", postId=" + postId +
                ", username='" + username + '\'' +
                ", text='" + text + '\'' +
                ", likes=" + likes +
                ", createdDate=" + createdDate +
                '}';
    }
}