package com.hoangluongtran0309.personal_blog.project;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public interface ProjectRepository extends CrudRepository<Project, ProjectId>, ProjectRepositoryCustom, PagingAndSortingRepository<Project, ProjectId> {

    Page<Project> findByProjectStatus(ProjectStatus projectStatus, Pageable pageable);

}
