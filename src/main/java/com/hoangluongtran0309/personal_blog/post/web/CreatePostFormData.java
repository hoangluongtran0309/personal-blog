package com.hoangluongtran0309.personal_blog.post.web;

import com.hoangluongtran0309.personal_blog.post.CreatePostParameters;
import com.hoangluongtran0309.personal_blog.post.PostContent;
import com.hoangluongtran0309.personal_blog.post.PostSummary;
import com.hoangluongtran0309.personal_blog.post.PostTitle;

public class CreatePostFormData extends AbstractPostFormData {

    public CreatePostParameters toParameters() {
        return  new CreatePostParameters(
                new PostTitle(getPostTitle()),
                new PostSummary(getPostSummary()),
                new PostContent(getPostContent()),
                getPostStatus(),
                getPublishDate());
    }

}
