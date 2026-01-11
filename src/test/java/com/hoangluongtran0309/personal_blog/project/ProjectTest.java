package com.hoangluongtran0309.personal_blog.project;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;

public class ProjectTest {

    @Test
    void createProject_WithDefaultDraftStatus_ShouldSuccess() {
        Project project = Project.create(new ProjectId(UUID.randomUUID()), new ProjectTitle("New Project Title"));
        assertNotNull(project);
        assertEquals(ProjectStatus.DRAFT, project.getProjectStatus());
        assertEquals("New Project Title", project.getProjectTitle().getTitle());
    }
    
}
