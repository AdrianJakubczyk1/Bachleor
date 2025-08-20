package com.example.demo.persistent.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

public class Post implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String title;
    private String content;
    private byte[] attachment;
    private String attachmentFilename;
    private String attachmentContentType;
    private String author;
    private int viewCount;
    private byte[] thumbnail;
    private String thumbnailFilename;
    private String thumbnailContentType;
    private Double avgRating;

    // No‑args ctor
    public Post() {}

    // Full‑args ctor
    public Post(Long id,
                String title,
                String content,
                byte[] attachment,
                String attachmentFilename,
                String attachmentContentType,
                String author,
                int viewCount,
                byte[] thumbnail,
                String thumbnailFilename,
                String thumbnailContentType,
                Double avgRating) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.attachment = attachment;
        this.attachmentFilename = attachmentFilename;
        this.attachmentContentType = attachmentContentType;
        this.author = author;
        this.viewCount = viewCount;
        this.thumbnail = thumbnail;
        this.thumbnailFilename = thumbnailFilename;
        this.thumbnailContentType = thumbnailContentType;
        this.avgRating = avgRating;
    }

    // Getters & setters...
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public byte[] getAttachment() { return attachment; }
    public void setAttachment(byte[] attachment) { this.attachment = attachment; }

    public String getAttachmentFilename() { return attachmentFilename; }
    public void setAttachmentFilename(String attachmentFilename) {
        this.attachmentFilename = attachmentFilename;
    }

    public String getAttachmentContentType() { return attachmentContentType; }
    public void setAttachmentContentType(String attachmentContentType) {
        this.attachmentContentType = attachmentContentType;
    }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public int getViewCount() { return viewCount; }
    public void setViewCount(int viewCount) { this.viewCount = viewCount; }

    public byte[] getThumbnail() { return thumbnail; }
    public void setThumbnail(byte[] thumbnail) { this.thumbnail = thumbnail; }

    public String getThumbnailFilename() { return thumbnailFilename; }
    public void setThumbnailFilename(String thumbnailFilename) {
        this.thumbnailFilename = thumbnailFilename;
    }

    public String getThumbnailContentType() { return thumbnailContentType; }
    public void setThumbnailContentType(String thumbnailContentType) {
        this.thumbnailContentType = thumbnailContentType;
    }

    public Double getAvgRating() { return avgRating; }
    public void setAvgRating(Double avgRating) { this.avgRating = avgRating; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Post)) return false;
        Post that = (Post) o;
        return viewCount == that.viewCount &&
                Objects.equals(id, that.id) &&
                Objects.equals(title, that.title) &&
                Objects.equals(content, that.content) &&
                Arrays.equals(attachment, that.attachment) &&
                Objects.equals(attachmentFilename, that.attachmentFilename) &&
                Objects.equals(attachmentContentType, that.attachmentContentType) &&
                Objects.equals(author, that.author) &&
                Arrays.equals(thumbnail, that.thumbnail) &&
                Objects.equals(thumbnailFilename, that.thumbnailFilename) &&
                Objects.equals(thumbnailContentType, that.thumbnailContentType) &&
                Objects.equals(avgRating, that.avgRating);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, title, content, attachmentFilename,
                attachmentContentType, author, viewCount,
                thumbnailFilename, thumbnailContentType, avgRating);
        result = 31 * result + Arrays.hashCode(attachment);
        result = 31 * result + Arrays.hashCode(thumbnail);
        return result;
    }

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", viewCount=" + viewCount +
                ", avgRating=" + avgRating +
                '}';
    }
}