package com.hoangluongtran0309.personal_blog.project.web;

import com.hoangluongtran0309.personal_blog.project.EditProjectParameters;
import com.hoangluongtran0309.personal_blog.project.Project;
import com.hoangluongtran0309.personal_blog.project.ProjectCategory;
import com.hoangluongtran0309.personal_blog.project.ProjectContent;
import com.hoangluongtran0309.personal_blog.project.ProjectSummary;
import com.hoangluongtran0309.personal_blog.project.ProjectTitle;

public class EditProjectFormData extends AbstractProjectFormData {

    private String projectId;

    private Long version;

    public static EditProjectFormData fromProject(Project project) {
        EditProjectFormData result = new EditProjectFormData();
        result.setProjectId(project.getProjectId().asString());
        result.setVersion(project.getVersion());
        result.setProjectCategory(project.getProjectCategory().toString());
        result.setProjectTitle(project.getProjectTitle().toString());
        result.setProjectSummary(project.getProjectSummary().toString());
        result.setProjectContent(project.getProjectContent().toString());
        result.setProjectStatus(project.getProjectStatus());
        result.setPublishDate(project.getPublishDate());
        return result;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public EditProjectParameters toParameters() {
        return  new EditProjectParameters(
                new ProjectCategory(getProjectCategory()),
                new ProjectTitle(getProjectTitle()),
                new ProjectSummary(getProjectSummary()),
                new ProjectContent(getProjectContent()),
                getProjectStatus(),
                getVersion(),
                getPublishDate());
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

}
