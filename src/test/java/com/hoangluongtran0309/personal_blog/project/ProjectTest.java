package com.hoangluongtran0309.personal_blog.project;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import org.junit.jupiter.api.Test;

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

}
