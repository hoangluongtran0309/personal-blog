package com.hoangluongtran0309.personal_blog.post.web;

import java.time.LocalDate;

import com.hoangluongtran0309.personal_blog.post.PostStatus;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class AbstractPostFormData {

    @Size(min = 5, max = 100)
    @NotNull
    private String postTitle;

    @Size(min = 20, max = 1000)
    @NotNull
    private String postSummary;

    @Size(min = 20, max = 10000)
    @NotNull
    private String postContent;

    @NotNull
    private PostStatus postStatus;

    @NotNull
    private LocalDate publishDate;

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public String getPostSummary() {
        return postSummary;
    }

    public void setPostSummary(String postSummary) {
        this.postSummary = postSummary;
    }

    public String getPostContent() {
        return postContent;
    }

    public void setPostContent(String postContent) {
        this.postContent = postContent;
    }

    public PostStatus getPostStatus() {
        return postStatus;
    }

    public void setPostStatus(PostStatus postStatus) {
        this.postStatus = postStatus;
    }

    public LocalDate getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(LocalDate publishDate) {
        this.publishDate = publishDate;
    }

}
