package com.hoangluongtran0309.personal_blog.project;

import org.springframework.stereotype.Repository;

import com.hoangluongtran0309.personal_blog.db.UniqueIdGenerator;

@Repository
public class ProjectRepositoryCustomImpl implements ProjectRepositoryCustom {

    private final UniqueIdGenerator generator;

    public ProjectRepositoryCustomImpl(UniqueIdGenerator generator) {
        this.generator = generator;
    }

    @Override
    public ProjectId nextProjectId() {
        return generator.nextProjectId();
    }

}
