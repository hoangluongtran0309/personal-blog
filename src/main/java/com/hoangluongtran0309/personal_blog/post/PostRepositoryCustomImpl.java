package com.hoangluongtran0309.personal_blog.post;

import org.springframework.stereotype.Repository;

import com.hoangluongtran0309.personal_blog.db.UniqueIdGenerator;

@Repository
public class PostRepositoryCustomImpl implements PostRepositoryCustom {

    private final UniqueIdGenerator generator;

    public PostRepositoryCustomImpl(UniqueIdGenerator generator) {
        this.generator = generator;
    }

    @Override
    public PostId nextPostId() {
        return generator.nextPostId();
    }
    
}
