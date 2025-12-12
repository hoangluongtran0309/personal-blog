package com.hoangluongtran0309.personal_blog.post;

import java.time.LocalDate;

public class EditPostParameters {

    private final PostTitle postTitle;

    private final PostSummary postSummary;

    private final PostContent postContent;

    private final PostStatus postStatus;

    private final Long version;

    private final LocalDate publishDate;

    public EditPostParameters(PostTitle postTitle, PostSummary postSummary, PostContent postContent,
            PostStatus postStatus, Long version, LocalDate publishDate) {
        this.postTitle = postTitle;
        this.postSummary = postSummary;
        this.postContent = postContent;
        this.postStatus = postStatus;
        this.version = version;
        this.publishDate = publishDate;
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
