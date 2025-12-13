package com.hoangluongtran0309.personal_blog.project;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProjectService {

    Project createProject(CreateProjectParameters parameters);

    Project getProjectById(ProjectId id);

    void updateProject(ProjectId id, EditProjectParameters parameters);

    void deleteProject(ProjectId id);

    Page<Project> getAllProjects(Pageable pageable);

    Page<Project> getAllPublishedProjects(Pageable pageable);
    
}
