package com.hoangluongtran0309.personal_blog.post.web;

import com.hoangluongtran0309.personal_blog.post.EditPostParameters;
import com.hoangluongtran0309.personal_blog.post.Post;
import com.hoangluongtran0309.personal_blog.post.PostContent;
import com.hoangluongtran0309.personal_blog.post.PostSummary;
import com.hoangluongtran0309.personal_blog.post.PostTitle;

public class EditPostFormData extends AbstractPostFormData {

    private String postId;

    private Long version;

    public static EditPostFormData fromPost(Post post) {
        EditPostFormData result = new EditPostFormData();
        result.setPostId(post.getPostId().asString());
        result.setVersion(post.getVersion());
        result.setPostTitle(post.getPostTitle().toString());
        result.setPostSummary(post.getPostSummary().toString());
        result.setPostContent(post.getPostContent().toString());
        result.setPostStatus(post.getPostStatus());
        result.setPublishDate(post.getPublishDate());
        return result;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public EditPostParameters toParameters() {
        return  new EditPostParameters(
                new PostTitle(getPostTitle()),
                new PostSummary(getPostSummary()),
                new PostContent(getPostContent()),
                getPostStatus(),
                getVersion(),
                getPublishDate());
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }
    
}
