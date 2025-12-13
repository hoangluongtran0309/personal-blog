package com.hoangluongtran0309.personal_blog.project.web;

import java.util.UUID;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.hoangluongtran0309.personal_blog.project.ProjectId;

@Component
public class StringToProjectIdConverter implements Converter<String, ProjectId> {

    @Override
    public ProjectId convert(String source) {
        return new ProjectId(UUID.fromString(source));
    }

}
