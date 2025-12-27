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
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.hoangluongtran0309.personal_blog.config.TestConfig;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestConfig.class)
public class PostRepositoryTest {

        @Autowired
        PostRepository postRepository;

        @Autowired
        JdbcTemplate jdbcTemplate;

        @PersistenceContext
        EntityManager entityManager;

        @Container
        static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

        @DynamicPropertySource
        static void overrideProps(DynamicPropertyRegistry registry) {
                registry.add("spring.datasource.url", postgres::getJdbcUrl);
                registry.add("spring.datasource.username", postgres::getUsername);
                registry.add("spring.datasource.password", postgres::getPassword);
        }

        @BeforeEach
        void validatePreconditions() {
                assertThat(postRepository.count()).isZero();
        }

        @Test
        public void testCreatePost() {

                PostId id = postRepository.nextPostId();

                postRepository.save(new Post(id, new PostTitle("post-1"),
                                new PostSummary("s1"),
                                new PostContent("c1"), PostStatus.PUBLISHED, LocalDate.now()));

                entityManager.flush();

                assertThat(jdbcTemplate.queryForObject("SELECT post_id FROM posts", UUID.class)).isEqualTo(id.getId());
                assertThat(jdbcTemplate.queryForObject("SELECT post_title FROM posts", String.class))
                                .isEqualTo("post-1");
                assertThat(jdbcTemplate.queryForObject("SELECT post_summary FROM posts", String.class)).isEqualTo("s1");
                assertThat(jdbcTemplate.queryForObject("SELECT post_content FROM posts", String.class)).isEqualTo("c1");
                assertThat(PostStatus
                                .valueOf(jdbcTemplate.queryForObject("SELECT post_status FROM posts", String.class)))
                                .isEqualTo(PostStatus.PUBLISHED);
        }

        @Test
        void testUpdatePost() {

                PostId id = postRepository.nextPostId();

                Post post = new Post(id, new PostTitle("post-1"), new PostSummary("s1"),
                                new PostContent("c1"), PostStatus.DRAFT, LocalDate.now());

                postRepository.save(post);
                entityManager.flush();

                post.setPostTitle(new PostTitle("post-1-updated"));
                post.setPostSummary(new PostSummary("s2"));
                post.setPostContent(new PostContent("c2"));
                post.setPostStatus(PostStatus.PUBLISHED);

                postRepository.save(post);
                entityManager.flush();

                assertThat(
                                jdbcTemplate.queryForObject("SELECT post_title FROM posts WHERE post_id = ?",
                                                String.class, id.getId()))
                                .isEqualTo("post-1-updated");
                assertThat(jdbcTemplate.queryForObject("SELECT post_summary FROM posts WHERE post_id = ?", String.class,
                                id.getId())).isEqualTo("s2");
                assertThat(jdbcTemplate.queryForObject("SELECT post_content FROM posts WHERE post_id = ?", String.class,
                                id.getId())).isEqualTo("c2");
                assertThat(PostStatus
                                .valueOf(jdbcTemplate.queryForObject("SELECT post_status FROM posts WHERE post_id = ?",
                                                String.class, id.getId())))
                                .isEqualTo(PostStatus.PUBLISHED);
        }

        // @Test
        // void
        // testUpdatePost_shouldThrowOptimisticLockingFailureException_whenVersionMismatch()
        // throws Exception {

        // PostId id = postRepository.nextPostId();

        // postRepository.save(new Post(id, new PostTitle("post-1"),
        // new PostSummary("s1"),
        // new PostContent("c1"), PostStatus.PUBLISHED, LocalDate.now()));

        // entityManager.flush();
        // entityManager.clear();

        // ExecutorService executor = Executors.newFixedThreadPool(2);
        // CountDownLatch latch = new CountDownLatch(1);

        // Callable<Void> tx1 = () -> {
        // Post post1 = postRepository.findById(id).orElseThrow();
        // latch.await();
        // post1.setPostSummary(new PostSummary("tx1"));
        // entityManager.flush(); // should fail
        // return null;
        // };

        // Callable<Void> tx2 = () -> {
        // Post post2 = postRepository.findById(id).orElseThrow();
        // post2.setPostSummary(new PostSummary("tx2"));
        // entityManager.flush(); // commit first
        // latch.countDown();
        // return null;
        // };

        // Future<Void> f1 = executor.submit(tx1);
        // Future<Void> f2 = executor.submit(tx2);

        // f2.get();

        // assertThatThrownBy(f1::get).hasCauseInstanceOf(OptimisticLockingFailureException.class);

        // executor.shutdown();
        // }

        @Test
        void testDeletePost() {
                PostId id = postRepository.nextPostId();

                postRepository.save(new Post(id, new PostTitle("post-1"),
                                new PostSummary("s1"),
                                new PostContent("c1"), PostStatus.PUBLISHED, LocalDate.now()));

                entityManager.flush();
                assertThat(postRepository.count()).isOne();

                postRepository.deleteById(id);

                entityManager.flush();
                entityManager.clear();

                assertThat(postRepository.count()).isZero();
        }
}
