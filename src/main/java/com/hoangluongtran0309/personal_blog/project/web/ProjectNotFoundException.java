package com.hoangluongtran0309.personal_blog.project.web;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.hoangluongtran0309.personal_blog.project.ProjectId;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ProjectNotFoundException extends RuntimeException {

    public ProjectNotFoundException(ProjectId id) {
        super(String.format("Project with id %d not found", id));
    }
    
}
