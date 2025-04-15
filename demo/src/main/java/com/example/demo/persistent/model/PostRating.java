package com.example.demo.persistent.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("post_rating")
public class PostRating {

    @Id
    private Long id;

    private Long postId;
    private Long userId;
    private short rating; // rating from 0 to 10

    // Getters and setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getPostId() {
        return postId;
    }
    public void setPostId(Long postId) {
        this.postId = postId;
    }
    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public short getRating() {
        return rating;
    }
    public void setRating(short rating) {
        this.rating = rating;
    }
}