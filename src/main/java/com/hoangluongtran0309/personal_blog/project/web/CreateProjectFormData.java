package com.hoangluongtran0309.personal_blog.project.web;

import com.hoangluongtran0309.personal_blog.project.CreateProjectParameters;
import com.hoangluongtran0309.personal_blog.project.ProjectCategory;
import com.hoangluongtran0309.personal_blog.project.ProjectContent;
import com.hoangluongtran0309.personal_blog.project.ProjectSummary;
import com.hoangluongtran0309.personal_blog.project.ProjectTitle;

public class CreateProjectFormData extends AbstractProjectFormData {

    public CreateProjectParameters toParameters() {
        return  new CreateProjectParameters(
                new ProjectCategory(getProjectCategory()),
                new ProjectTitle(getProjectTitle()),
                new ProjectSummary(getProjectSummary()),
                new ProjectContent(getProjectContent()),
                getProjectStatus(),
                getPublishDate());
    }

}
