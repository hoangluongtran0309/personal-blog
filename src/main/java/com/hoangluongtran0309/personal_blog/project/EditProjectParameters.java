package com.hoangluongtran0309.personal_blog.project;

import java.time.LocalDate;

public class EditProjectParameters {

    private final ProjectCategory projectCategory;

    private final ProjectTitle projectTitle;

    private final ProjectSummary projectSummary;

    private final ProjectContent projectContent;

    private final ProjectStatus projectStatus;

    private final Long version;

    private final LocalDate publishDate;

    public EditProjectParameters(ProjectCategory projectCategory, ProjectTitle projectTitle, ProjectSummary projectSummary,
            ProjectContent projectContent, ProjectStatus projectStatus, Long version, LocalDate publishDate) {
        this.projectCategory = projectCategory;
        this.projectTitle = projectTitle;
        this.projectSummary = projectSummary;
        this.projectContent = projectContent;
        this.projectStatus = projectStatus;
        this.version = version;
        this.publishDate = publishDate;
    }

    public Long getVersion() {
        return version;
    }

    public void update(Project project) {
        project.setProjectCategory(projectCategory);
        project.setProjectTitle(projectTitle);
        project.setProjectSummary(projectSummary);
        project.setProjectContent(projectContent);
        project.setProjectStatus(projectStatus);
        project.setPublishDate(publishDate);
    }
    
}
