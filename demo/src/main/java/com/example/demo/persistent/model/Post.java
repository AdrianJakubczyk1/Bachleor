package com.example.demo.persistent.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("posts")
public class Post {
    @Id
    private Long id;

    private String title;
    private String content;

    // Fields for file attachment
    private byte[] attachment;
    private String attachmentFilename;
    private String attachmentContentType;
    private String author;
    private int viewCount;
    private byte[] thumbnail;
    private String thumbnailFilename;
    private String thumbnailContentType;
    private Double avgRating;

    public Double getAvgRating() {
        return avgRating;
    }
    public void setAvgRating(Double avgRating) {
        this.avgRating = avgRating;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
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
    public String getAuthor() {
        return author;
    }
    public void setAuthor(String author) {
        this.author = author;
    }
    public int getViewCount() {
        return viewCount;
    }
    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public byte[] getThumbnail() { return thumbnail; }
    public void setThumbnail(byte[] thumbnail) { this.thumbnail = thumbnail; }

    public String getThumbnailFilename() { return thumbnailFilename; }
    public void setThumbnailFilename(String thumbnailFilename) { this.thumbnailFilename = thumbnailFilename; }

    public String getThumbnailContentType() { return thumbnailContentType; }
    public void setThumbnailContentType(String thumbnailContentType) { this.thumbnailContentType = thumbnailContentType; }
}
