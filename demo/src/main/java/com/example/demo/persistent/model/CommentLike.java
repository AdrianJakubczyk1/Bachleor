package com.example.demo.persistent.model;
import java.io.Serializable;
import java.util.Objects;

public class CommentLike implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long commentId;
    private String username;

    // No-args ctor for Hazelcast
    public CommentLike() { }

    // Full-args ctor
    public CommentLike(Long id, Long commentId, String username) {
        this.id = id;
        this.commentId = commentId;
        this.username = username;
    }

    // Getters & setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Long getCommentId() {
        return commentId;
    }
    public void setCommentId(Long commentId) {
        this.commentId = commentId;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CommentLike)) return false;
        CommentLike that = (CommentLike) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(commentId, that.commentId) &&
                Objects.equals(username, that.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, commentId, username);
    }

    @Override
    public String toString() {
        return "CommentLike{" +
                "id=" + id +
                ", commentId=" + commentId +
                ", username='" + username + '\'' +
                '}';
    }
}
