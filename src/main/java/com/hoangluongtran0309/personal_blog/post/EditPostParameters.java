package com.hoangluongtran0309.personal_blog.post;

import java.time.LocalDate;

public class EditPostParameters {

    private final PostTitle postTitle;

    private final PostSummary postSummary;

    private final PostContent postContent;

    private final PostStatus postStatus;

    private final LocalDate publishDate;

    private final Long version;

    public EditPostParameters(PostTitle postTitle, PostSummary postSummary, PostContent postContent,
            PostStatus postStatus, LocalDate publishDate, Long version) {
        this.postTitle = postTitle;
        this.postSummary = postSummary;
        this.postContent = postContent;
        this.postStatus = postStatus;
        this.publishDate = publishDate;
        this.version = version;
    }

    public Long getVersion() {
        return version;
    }

    public void update(Post post) {
        post.setPostTitle(postTitle);
        post.setPostSummary(postSummary);
        post.setPostContent(postContent);
        post.setPostStatus(postStatus);
        post.setPublishDate(publishDate);
    }

}
