package com.hoangluongtran0309.personal_blog.db;

import com.hoangluongtran0309.personal_blog.post.PostId;

public interface UniqueIdGenerator {

    PostId nextPostId();
    
}
