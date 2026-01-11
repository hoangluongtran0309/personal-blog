package com.hoangluongtran0309.personal_blog.project;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class Project {

    private ProjectId projectId;

    private ProjectTitle projectTitle;

    private ProjectBody projectBody;

    private ProjectSlug projectSlug;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private ProjectStatus projectStatus;

    private Set<ProjectTag> projectTags;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    protected Project() {

    }

    public Project(ProjectId projectId, ProjectTitle projectTitle, ProjectBody projectBody, ProjectSlug projectSlug,
            LocalDateTime startTime, LocalDateTime endTime, ProjectStatus projectStatus) {
        this.projectId = projectId;
        this.projectTitle = projectTitle;
        this.projectBody = projectBody;
        this.projectSlug = projectSlug;
        this.startTime = startTime;
        this.endTime = endTime;
        this.projectStatus = projectStatus;
        this.projectTags = new HashSet<>();
    }

    public ProjectId getProjectId() {
        return projectId;
    }

    public ProjectTitle getProjectTitle() {
        return projectTitle;
    }

    public ProjectBody getProjectBody() {
        return projectBody;
    }

    public ProjectSlug getProjectSlug() {
        return projectSlug;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public ProjectStatus getProjectStatus() {
        return projectStatus;
    }

    public Set<ProjectTag> getProjectTags() {
        return projectTags;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public static Project create(ProjectId projectId, ProjectTitle projectTitle) {
        return new Project(projectId, projectTitle, null, null, LocalDateTime.now(), null, ProjectStatus.DRAFT);
    }

    public void update(ProjectTitle projectTitle, ProjectBody projectBody, ProjectSlug projectSlug) {
        assertUpdatable();
        this.projectTitle = projectTitle;
        this.projectBody = projectBody;
        this.projectSlug = projectSlug;
    }

    public void assertUpdatable() {
        if (projectStatus != ProjectStatus.DRAFT) {
            throw new IllegalStateException("Only draft projects can be updated");
        }
    }

    public void addTag(ProjectTag projectTag) {
        assertUpdatable();
        this.projectTags.add(projectTag);
    }

    public void removeTag(ProjectTag projectTag) {
        assertUpdatable();
        this.projectTags.remove(projectTag);
    }

    public void complete(LocalDateTime endTime) {
        if (projectStatus == ProjectStatus.COMPLETED) {
            throw new IllegalStateException("Project is already completed");
        }
        this.endTime = endTime;
        this.projectStatus = ProjectStatus.COMPLETED;
    }

}
