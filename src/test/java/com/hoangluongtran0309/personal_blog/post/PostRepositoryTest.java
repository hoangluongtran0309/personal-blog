package com.hoangluongtran0309.personal_blog.post;

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
public class PostRepositoryTest {

    @Autowired
    PostRepository postRepository;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @PersistenceContext
    EntityManager entityManager;

    @BeforeEach
    void validatePreconditions() {
        assertThat(postRepository.count()).isZero();
    }

    @Test
    public void testSavePost() {

        PostId id = postRepository.nextPostId();

        postRepository.save(new Post(id,
                new PostTitle("blog"),
                new PostSummary("Lorem ipsum"),
                new PostContent("Lorem ipsum dolor sit amet consectetuer adipiscing elit sed diam nonummy nibh"),
                PostStatus.PUBLISHED, LocalDate.now()
        ));

        entityManager.flush();

        assertThat(jdbcTemplate.queryForObject("SELECT post_id FROM posts", UUID.class)).isEqualTo(id.getId());

        assertThat(jdbcTemplate.queryForObject("SELECT post_title FROM posts", String.class)).isEqualTo("blog");

        assertThat(jdbcTemplate.queryForObject("SELECT post_summary FROM posts", String.class)).isEqualTo("Lorem ipsum");

        assertThat(jdbcTemplate.queryForObject("SELECT post_content FROM posts", String.class)).isEqualTo("Lorem ipsum dolor sit amet consectetuer adipiscing elit sed diam nonummy nibh");

        assertThat(PostStatus.valueOf(jdbcTemplate.queryForObject("SELECT post_status FROM posts", String.class))).isEqualTo(PostStatus.PUBLISHED);

    }

    @Test
    void testDeletePost() {
        PostId id = postRepository.nextPostId();

        postRepository.save(new Post(id,
                new PostTitle("blog"),
                new PostSummary("Lorem ipsum"),
                new PostContent("Lorem ipsum dolor sit amet consectetuer adipiscing elit sed diam nonummy nibh"),
                PostStatus.DRAFT, LocalDate.now()
        ));

        entityManager.flush();
        assertThat(postRepository.count()).isOne();

        postRepository.deleteById(id);

        entityManager.flush();
        entityManager.clear();
        assertThat(postRepository.count()).isZero();
    }

    @Test
    void testFindAll() {
        savePosts(10);
        assertThat(postRepository.findAll()).hasSize(10).extracting(post -> post.getPostTitle().getValue()).containsExactly("Blog0", "Blog1", "Blog2", "Blog3", "Blog4", "Blog5", "Blog6", "Blog7", "Blog8", "Blog9");
    }

    private void savePosts(int numberOfPosts) {
        for (int i = 0; i < numberOfPosts; i++) {
            postRepository.save(new Post(postRepository.nextPostId(), new PostTitle(String.format("Blog%d", i)), new PostSummary("%d - Lorem ipsum"), new PostContent(String.format("%d - Lorem ipsum dolor sit amet consectetuer adipiscing elit sed diam nonummy nibh", i)), (i % 2 == 0) ? PostStatus.PUBLISHED : PostStatus.DRAFT, LocalDate.now()));
        }
    }

}

