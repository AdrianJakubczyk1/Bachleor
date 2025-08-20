package com.example.demo.persistent.model;

import java.io.Serializable;
import java.util.Objects;

public class PostRating implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long postId;
    private Long userId;
    private short rating;

    // No‑args ctor for Hazelcast
    public PostRating() {}

    // Full‑args ctor
    public PostRating(Long id, Long postId, Long userId, short rating) {
        this.id = id;
        this.postId = postId;
        this.userId = userId;
        this.rating = rating;
    }

    // Getters & setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPostId() { return postId; }
    public void setPostId(Long postId) { this.postId = postId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public short getRating() { return rating; }
    public void setRating(short rating) { this.rating = rating; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PostRating)) return false;
        PostRating that = (PostRating) o;
        return rating == that.rating &&
                Objects.equals(id, that.id) &&
                Objects.equals(postId, that.postId) &&
                Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, postId, userId, rating);
    }

    @Override
    public String toString() {
        return "PostRating{" +
                "id=" + id +
                ", postId=" + postId +
                ", userId=" + userId +
                ", rating=" + rating +
                '}';
    }
}