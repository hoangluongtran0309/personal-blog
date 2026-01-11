package com.hoangluongtran0309.personal_blog.post;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class Post {

    private PostId postId;

    private PostTitle postTitle;

    private PostBody postBody;

    private PostSlug postSlug;

    private LocalDateTime publishDate;

    private PostStatus postStatus;

    private Set<PostCategory> postCategories;

    private Set<PostTag> postTags;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    protected Post() {

    }

    public Post(PostId postId, PostTitle postTitle, PostBody postBody, PostSlug postSlug, LocalDateTime publishDate,
            PostStatus postStatus) {
        this.postId = postId;
        this.postTitle = postTitle;
        this.postBody = postBody;
        this.postSlug = postSlug;
        this.publishDate = publishDate;
        this.postStatus = postStatus;
        this.postCategories = new HashSet<>();
        this.postTags = new HashSet<>();

    }

    public PostId getPostId() {
        return postId;
    }

    public PostTitle getPostTitle() {
        return postTitle;
    }

    public PostBody getPostBody() {
        return postBody;
    }

    public PostSlug getPostSlug() {
        return postSlug;
    }

    public LocalDateTime getPublishDate() {
        return publishDate;
    }

    public PostStatus getPostStatus() {
        return postStatus;
    }

    public Set<PostCategory> getPostCategories() {
        return postCategories;
    }

    public Set<PostTag> getPostTags() {
        return postTags;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public static Post create(PostId postId, PostTitle postTitle) {
        return new Post(postId, postTitle, null, null, null, PostStatus.DRAFT);
    }

}
