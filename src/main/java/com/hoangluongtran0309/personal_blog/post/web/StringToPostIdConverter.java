package com.hoangluongtran0309.personal_blog.post.web;

import java.util.UUID;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.hoangluongtran0309.personal_blog.post.PostId;

@Component
public class StringToPostIdConverter implements Converter<String, PostId> {

    @Override
    public PostId convert(String source) {
        return new PostId(UUID.fromString(source));
    }
    
}
