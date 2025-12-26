package com.hoangluongtran0309.personal_blog.post;

import java.time.LocalDate;

public class CreatePostParameters {

    private final PostTitle postTitle;

    private final PostSummary postSummary;

    private final PostContent postContent;

    private final PostStatus postStatus;

    private final LocalDate publishDate;

    public CreatePostParameters(PostTitle postTitle, PostSummary postSummary, PostContent postContent,
            PostStatus postStatus, LocalDate publishDate) {
        this.postTitle = postTitle;
        this.postSummary = postSummary;
        this.postContent = postContent;
        this.postStatus = postStatus;
        this.publishDate = publishDate;
    }

    public PostTitle getPostTitle() {
        return postTitle;
    }

    public PostSummary getPostSummary() {
        return postSummary;
    }

    public PostContent getPostContent() {
        return postContent;
    }

    public PostStatus getPostStatus() {
        return postStatus;
    }

    public LocalDate getPublishDate() {
        return publishDate;
    }

}
