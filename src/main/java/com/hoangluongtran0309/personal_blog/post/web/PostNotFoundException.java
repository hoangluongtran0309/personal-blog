package com.hoangluongtran0309.personal_blog.post.web;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.hoangluongtran0309.personal_blog.post.PostId;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class PostNotFoundException extends RuntimeException {

    public PostNotFoundException(PostId id) {
        super(String.format("Post with id %d not found", id));
    }
    
}
