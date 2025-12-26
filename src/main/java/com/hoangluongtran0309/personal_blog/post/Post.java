package com.hoangluongtran0309.personal_blog.post;

import java.time.LocalDate;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Table(name = "posts")
public class Post {

    @EmbeddedId
    @AttributeOverrides({
            @AttributeOverride(name = "id", column = @Column(name = "post_id")) })
    private PostId postId;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "post_title")) })
    private PostTitle postTitle;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "post_summary")) })
    private PostSummary postSummary;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "post_content")) })
    private PostContent postContent;

    @Enumerated(EnumType.STRING)
    @Column(name = "post_status")
    private PostStatus postStatus;

    @Version
    @Column(name = "post_version")
    private Long version;

    @Column(name = "post_publish_date")
    private LocalDate publishDate;

    protected Post() {

    }

    public Post(PostId postId, PostTitle postTitle, PostSummary postSummary, PostContent postContent,
            PostStatus postStatus, LocalDate publishDate) {
        this.postId = postId;
        this.postTitle = postTitle;
        this.postSummary = postSummary;
        this.postContent = postContent;
        this.postStatus = postStatus;
        this.publishDate = publishDate;
    }

    public PostId getPostId() {
        return postId;
    }

    public PostTitle getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(PostTitle postTitle) {
        this.postTitle = postTitle;
    }

    public PostSummary getPostSummary() {
        return postSummary;
    }

    public void setPostSummary(PostSummary postSummary) {
        this.postSummary = postSummary;
    }

    public PostContent getPostContent() {
        return postContent;
    }

    public void setPostContent(PostContent postContent) {
        this.postContent = postContent;
    }

    public PostStatus getPostStatus() {
        return postStatus;
    }

    public void setPostStatus(PostStatus postStatus) {
        this.postStatus = postStatus;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public LocalDate getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(LocalDate publishDate) {
        this.publishDate = publishDate;
    }

}
