package com.hoangluongtran0309.personal_blog.project;

import java.time.LocalDate;

public class CreateProjectParameters {

    private final ProjectCategory projectCategory;

    private final ProjectTitle projectTitle;

    private final ProjectSummary projectSummary;

    private final ProjectContent projectContent;

    private final ProjectStatus projectStatus;

    private final LocalDate publishDate;

    public CreateProjectParameters(ProjectCategory projectCategory, ProjectTitle projectTitle, ProjectSummary projectSummary,
            ProjectContent projectContent, ProjectStatus projectStatus, LocalDate publishDate) {
        this.projectCategory = projectCategory;
        this.projectTitle = projectTitle;
        this.projectSummary = projectSummary;
        this.projectContent = projectContent;
        this.projectStatus = projectStatus;
        this.publishDate = publishDate;
    }

    public ProjectCategory getProjectCategory() {
        return projectCategory;
    }

    public ProjectTitle getProjectTitle() {
        return projectTitle;
    }

    public ProjectSummary getProjectSummary() {
        return projectSummary;
    }

    public ProjectContent getProjectContent() {
        return projectContent;
    }

    public ProjectStatus getProjectStatus() {
        return projectStatus;
    }

    public LocalDate getPublishDate() {
        return publishDate;
    }

}
