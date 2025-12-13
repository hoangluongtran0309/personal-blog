package com.hoangluongtran0309.personal_blog.db;

import com.hoangluongtran0309.personal_blog.post.PostId;
import com.hoangluongtran0309.personal_blog.project.ProjectId;

public interface UniqueIdGenerator {

    PostId nextPostId();
    
    ProjectId nextProjectId();

}
