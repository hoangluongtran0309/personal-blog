package com.hoangluongtran0309.personal_blog.project;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.hoangluongtran0309.personal_blog.config.TestConfig;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestConfig.class)
@ActiveProfiles("test")
public class ProjectRepositoryTest {

    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @PersistenceContext
    EntityManager entityManager;

    @BeforeEach
    void validatePreconditions() {
        assertThat(projectRepository.count()).isZero();
    }

    @Test
    public void testSaveproject() {

        ProjectId id = projectRepository.nextProjectId();

        projectRepository.save(new Project(id,
                new ProjectCategory("project category"),
                new ProjectTitle("project"),
                new ProjectSummary("Lorem ipsum"),
                new ProjectContent("Lorem ipsum dolor sit amet consectetuer adipiscing elit sed diam nonummy nibh"),
                ProjectStatus.PUBLISHED, LocalDate.now()
        ));

        entityManager.flush();

        assertThat(jdbcTemplate.queryForObject("SELECT project_id FROM projects", UUID.class)).isEqualTo(id.getId());

        assertThat(jdbcTemplate.queryForObject("SELECT project_category FROM projects", String.class)).isEqualTo("project category");

        assertThat(jdbcTemplate.queryForObject("SELECT project_title FROM projects", String.class)).isEqualTo("project");

        assertThat(jdbcTemplate.queryForObject("SELECT project_summary FROM projects", String.class)).isEqualTo("Lorem ipsum");

        assertThat(jdbcTemplate.queryForObject("SELECT project_content FROM projects", String.class)).isEqualTo("Lorem ipsum dolor sit amet consectetuer adipiscing elit sed diam nonummy nibh");

        assertThat(ProjectStatus.valueOf(jdbcTemplate.queryForObject("SELECT project_status FROM projects", String.class))).isEqualTo(ProjectStatus.PUBLISHED);

    }

    @Test
    void testDeleteproject() {

       ProjectId id = projectRepository.nextProjectId();

      
        projectRepository.save(new Project(id,
                new ProjectCategory("project category"),
                new ProjectTitle("project"),
                new ProjectSummary("Lorem ipsum"),
                new ProjectContent("Lorem ipsum dolor sit amet consectetuer adipiscing elit sed diam nonummy nibh"),
                ProjectStatus.PUBLISHED, LocalDate.now()
        ));

        entityManager.flush();
        assertThat(projectRepository.count()).isOne();

        projectRepository.deleteById(id);

        entityManager.flush();
        entityManager.clear();
        assertThat(projectRepository.count()).isZero();
    }

    @Test
    void testFindAll() {
        saveprojects(10);
        assertThat(projectRepository.findAll()).hasSize(10).extracting(project -> project.getProjectTitle().getValue()).containsExactly("Project0", "Project1", "Project2", "Project3", "Project4", "Project5", "Project6", "Project7", "Project8", "Project9");
    }

    private void saveprojects(int numberOfprojects) {
        for (int i = 0; i < numberOfprojects; i++) {
            projectRepository.save(new Project(projectRepository.nextProjectId(), new ProjectCategory(String.format("Project category%d", i)) , new ProjectTitle(String.format("Project%d", i)), new ProjectSummary("%d - Lorem ipsum"), new ProjectContent(String.format("%d - Lorem ipsum dolor sit amet consectetuer adipiscing elit sed diam nonummy nibh", i)), (i % 2 == 0) ? ProjectStatus.PUBLISHED : ProjectStatus.DRAFT, LocalDate.now()));
        }
    }

}