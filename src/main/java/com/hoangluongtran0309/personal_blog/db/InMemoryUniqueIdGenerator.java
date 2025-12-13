package com.hoangluongtran0309.personal_blog.db;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.hoangluongtran0309.personal_blog.post.PostId;
import com.hoangluongtran0309.personal_blog.project.ProjectId;

@Component
public class InMemoryUniqueIdGenerator implements UniqueIdGenerator {

    @Override
    public PostId nextPostId() {
        return new PostId(UUID.randomUUID());
    }

    @Override
    public ProjectId nextProjectId() {
        return new ProjectId(UUID.randomUUID());
    }

}
