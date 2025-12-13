package com.hoangluongtran0309.personal_blog.project;

import java.time.LocalDate;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Table(name = "projects")
@EntityListeners(AuditingEntityListener.class)
public class Project {

    @EmbeddedId
    @AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "project_id"))})
    private ProjectId projectId;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "value", column = @Column(name = "project_category"))})
    private ProjectCategory projectCategory;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "value", column = @Column(name = "project_title"))})
    private ProjectTitle projectTitle;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "value", column = @Column(name = "project_summary"))})
    private ProjectSummary projectSummary;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "value", column = @Column(name = "project_content"))})
    private ProjectContent projectContent;

    @Enumerated(EnumType.STRING)
    @Column(name = "project_status")
    private ProjectStatus projectStatus;

    @Version
    @Column(name = "project_version")
    private Long version;

    @Column(name = "project_publish_date")
    private LocalDate publishDate;

    protected Project() {

    }

    public Project(ProjectId projectId, ProjectCategory projectCategory, ProjectTitle projectTitle,
            ProjectSummary projectSummary, ProjectContent projectContent, ProjectStatus projectStatus,
            LocalDate publishDate) {
        this.projectId = projectId;
        this.projectCategory = projectCategory;
        this.projectTitle = projectTitle;
        this.projectSummary = projectSummary;
        this.projectContent = projectContent;
        this.projectStatus = projectStatus;
        this.publishDate = publishDate;
    }

    public ProjectId getProjectId() {
        return projectId;
    }

    public ProjectCategory getProjectCategory() {
        return projectCategory;
    }

    public void setProjectCategory(ProjectCategory projectCategory) {
        this.projectCategory = projectCategory;
    }

    public ProjectTitle getProjectTitle() {
        return projectTitle;
    }

    public void setProjectTitle(ProjectTitle projectTitle) {
        this.projectTitle = projectTitle;
    }

    public ProjectSummary getProjectSummary() {
        return projectSummary;
    }

    public void setProjectSummary(ProjectSummary projectSummary) {
        this.projectSummary = projectSummary;
    }

    public ProjectContent getProjectContent() {
        return projectContent;
    }

    public void setProjectContent(ProjectContent projectContent) {
        this.projectContent = projectContent;
    }

    public ProjectStatus getProjectStatus() {
        return projectStatus;
    }

    public void setProjectStatus(ProjectStatus projectStatus) {
        this.projectStatus = projectStatus;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public LocalDate getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(LocalDate publishDate) {
        this.publishDate = publishDate;
    }

    @Override
    public String toString() {
        return "Project [projectId=" + projectId + ", projectCategory=" + projectCategory + ", projectTitle="
                + projectTitle + ", projectSummary=" + projectSummary + ", projectContent=" + projectContent
                + ", projectStatus=" + projectStatus + ", version=" + version + ", publishDate=" + publishDate + "]";
    }

}
