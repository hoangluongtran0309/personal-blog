package com.hoangluongtran0309.personal_blog.project.web;

import java.time.LocalDate;

import com.hoangluongtran0309.personal_blog.project.ProjectStatus;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class AbstractProjectFormData {

    @Size(min = 5, max = 50)
    @NotNull
    private String projectCategory;

    @Size(min = 5, max = 100)
    @NotNull
    private String projectTitle;

    @Size(min = 20, max = 1000)
    @NotNull
    private String projectSummary;

    @Size(min = 20, max = 10000)
    @NotNull
    private String projectContent;

    @NotNull
    private ProjectStatus projectStatus;

    @NotNull
    private LocalDate publishDate;

    public String getProjectCategory() {
        return projectCategory;
    }

    public void setProjectCategory(String projectCategory) {
        this.projectCategory = projectCategory;
    }

    public String getProjectTitle() {
        return projectTitle;
    }

    public void setProjectTitle(String projectTitle) {
        this.projectTitle = projectTitle;
    }

    public String getProjectSummary() {
        return projectSummary;
    }

    public void setProjectSummary(String projectSummary) {
        this.projectSummary = projectSummary;
    }

    public String getProjectContent() {
        return projectContent;
    }

    public void setProjectContent(String projectContent) {
        this.projectContent = projectContent;
    }

    public ProjectStatus getProjectStatus() {
        return projectStatus;
    }

    public void setProjectStatus(ProjectStatus projectStatus) {
        this.projectStatus = projectStatus;
    }

    public LocalDate getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(LocalDate publishDate) {
        this.publishDate = publishDate;
    }

}
