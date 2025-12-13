package com.hoangluongtran0309.personal_blog.project;

import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hoangluongtran0309.personal_blog.project.web.ProjectNotFoundException;

@Service
@Transactional
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository ProjectRepository;

    public ProjectServiceImpl(ProjectRepository ProjectRepository) {
        this.ProjectRepository = ProjectRepository;
    }

    @Override
    public Project createProject(CreateProjectParameters parameters) {
        ProjectId id = ProjectRepository.nextProjectId();
        Project project = new Project(id, parameters.getProjectCategory(), parameters.getProjectTitle(), parameters.getProjectSummary(), parameters.getProjectContent(), parameters.getProjectStatus(), parameters.getPublishDate());
        return ProjectRepository.save(project);
    }

    @Override
    public Project getProjectById(ProjectId id) {
        return ProjectRepository.findById(id).orElseThrow(() -> new ProjectNotFoundException(id));
    }

    @Override
    public void updateProject(ProjectId id, EditProjectParameters parameters) {
        Project Project = ProjectRepository.findById(id).orElseThrow(() -> new ProjectNotFoundException(id));

        if (!Objects.equals(Project.getVersion(), parameters.getVersion())) {
            throw new ObjectOptimisticLockingFailureException(Project.class, id.asString());
        }

        parameters.update(Project);

    }

    @Override
    public void deleteProject(ProjectId id) {
        ProjectRepository.deleteById(id);
    }

    @Override
    public Page<Project> getAllProjects(Pageable pageable) {
        return ProjectRepository.findAll(pageable);
    }

    @Override
    public Page<Project> getAllPublishedProjects(Pageable pageable) {
        return ProjectRepository.findByProjectStatus(ProjectStatus.PUBLISHED, pageable);
    }

}
