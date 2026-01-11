package com.hoangluongtran0309.personal_blog.project;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.hoangluongtran0309.personal_blog.tag.TagId;

public class ProjectTest {

    @Test
    void createProject_WithDefaultDraftStatus_ShouldSuccess() {
        Project project = Project.create(new ProjectId(UUID.randomUUID()), new ProjectTitle("New Project Title"));
        assertNotNull(project);
        assertEquals(ProjectStatus.DRAFT, project.getProjectStatus());
        assertEquals("New Project Title", project.getProjectTitle().getTitle());
    }

    @Test
    void createProject_WithDefaultDraftStatus_ShouldFailure_WhenValueObjectsIsInvalid() {
        assertThrowsExactly(IllegalArgumentException.class, () -> {
            Project.create(new ProjectId(null), new ProjectTitle("New Project Title"));
        }, "Project ID must not be null");
        assertThrowsExactly(IllegalArgumentException.class, () -> {
            Project.create(new ProjectId(UUID.randomUUID()), new ProjectTitle(""));
        }, "Project title must not be blank");
    }

    @Test
    void updateProject_ShouldSuccess_WhenProjectIsDraftStatus() {
        Project project = Project.create(new ProjectId(UUID.randomUUID()), new ProjectTitle("New Project Title"));
        assertNull(project.getProjectBody());
        assertNull(project.getProjectSlug());
        project.update(new ProjectTitle("New Updated Project Title"), new ProjectBody("New Updated Project Summary", "New Updated Project Content"), new ProjectSlug("new-updated-project-slug"));
        assertEquals("New Updated Project Title", project.getProjectTitle().getTitle());
        assertEquals("New Updated Project Summary", project.getProjectBody().getSummary());
        assertEquals("New Updated Project Content", project.getProjectBody().getContent());
        assertEquals("new-updated-project-slug", project.getProjectSlug().getSlug());
    }

    @Test
    void updateProject_ShouldFailure_WhenProjectIsNotDraftStatus() {
        Project project = new Project(new ProjectId(UUID.randomUUID()), new ProjectTitle("New Project Title"), new ProjectBody("New Project Summary", "New Project Content"), new ProjectSlug("new-project-slug"), LocalDateTime.now(), LocalDateTime.MAX, ProjectStatus.COMPLETED);
        assertThrowsExactly(IllegalStateException.class, () -> {
            project.update(new ProjectTitle("New Updated Project Summary"), new ProjectBody("New Updated Project Summary", "New Updated Project Content"), new ProjectSlug("new-updated-project-slug"));
        }, "Only draft projects can be updated");
    }

    @Test
    void addTagToProject_ShouldSuccess() {
        Project project = Project.create(new ProjectId(UUID.randomUUID()), new ProjectTitle("New Project Title"));
        project.addTag(new ProjectTag(new TagId(UUID.randomUUID())));
        assertFalse(project.getProjectTags().isEmpty());
    }

    @Test
    void removeTagFromProject_ShouldSuccess() {
        Project project = Project.create(new ProjectId(UUID.randomUUID()), new ProjectTitle("New Project Title"));
        TagId tagId = new TagId(UUID.randomUUID());
        project.addTag(new ProjectTag(tagId));
        assertFalse(project.getProjectTags().isEmpty());
        project.removeTag(new ProjectTag(tagId));
        assertTrue(project.getProjectTags().isEmpty());
    }

}
