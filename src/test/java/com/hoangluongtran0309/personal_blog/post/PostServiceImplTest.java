package com.hoangluongtran0309.personal_blog.post;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

@ExtendWith(MockitoExtension.class)
public class PostServiceImplTest {

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostServiceImpl postServiceImpl;

    @Test
    void testCreatePost() {

        PostId postId = new PostId(UUID.randomUUID());

        CreatePostParameters parameters = new CreatePostParameters(new PostTitle("Post title"),
                new PostSummary("Post summary"),
                new PostContent("Post content"), PostStatus.DRAFT, LocalDate.now());

        when(postRepository.nextPostId()).thenReturn(postId);
        when(postRepository.save(any(Post.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Post result = postServiceImpl.createPost(parameters);

        assertNotNull(result);
        assertEquals(postId, result.getPostId());
        assertEquals("Post title", result.getPostTitle().getValue());
        assertEquals("Post summary", result.getPostSummary().getValue());
        assertEquals("Post content", result.getPostContent().getValue());
        assertEquals(PostStatus.DRAFT, result.getPostStatus());
        assertEquals(parameters.getPublishDate(), result.getPublishDate());

        verify(postRepository).nextPostId();
        verify(postRepository).save(any(Post.class));
    }

    @Test
    void testGetPostById() {

        PostId postId = new PostId(UUID.randomUUID());

        Post post = new Post(postId, new PostTitle("Post title"), new PostSummary("Post summary"),
                new PostContent("Post content"), PostStatus.DRAFT, LocalDate.now());

        when(postRepository.findById(postId)).thenReturn(Optional.ofNullable(post));

        Post result = postServiceImpl.getPostById(postId);

        assertNotNull(result);
        assertEquals(postId, result.getPostId());
        assertEquals("Post title", result.getPostTitle().getValue());
        assertEquals("Post summary", result.getPostSummary().getValue());
        assertEquals("Post content", result.getPostContent().getValue());
        assertEquals(PostStatus.DRAFT, result.getPostStatus());
        assertEquals(post.getPublishDate(), result.getPublishDate());

        verify(postRepository).findById(postId);
    }

    @Test
    void testGetPostById_shouldThrowException_WhenNotExists() {

        PostId postId = new PostId(UUID.randomUUID());

        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        assertThrows(PostNotFoundException.class, () -> {
            postServiceImpl.getPostById(postId);
        });
    }

    @Test
    void testUpdatePost() {

        PostId postId = new PostId(UUID.randomUUID());

        Post post = new Post(postId, new PostTitle("Post title"), new PostSummary("Post summary"),
                new PostContent("Post content"), PostStatus.DRAFT, LocalDate.of(2025, 1, 1));

        post.setVersion(1L);

        when(postRepository.findById(postId)).thenReturn(Optional.ofNullable(post));

        EditPostParameters parameters = new EditPostParameters(new PostTitle("New Post title"),
                new PostSummary("New Post summary"), new PostContent("New Post content"), PostStatus.PUBLISHED,
                LocalDate.now(), post.getVersion());

        postServiceImpl.updatePost(postId, parameters);

        Post result = postServiceImpl.getPostById(postId);

        assertNotNull(result);
        assertEquals(postId, result.getPostId());
        assertEquals("New Post title", result.getPostTitle().getValue());
        assertEquals("New Post summary", result.getPostSummary().getValue());
        assertEquals("New Post content", result.getPostContent().getValue());
        assertEquals(PostStatus.PUBLISHED, result.getPostStatus());
        assertEquals(post.getPublishDate(), result.getPublishDate());
        assertEquals(1L, result.getVersion());
    }

    @Test
    void testUpdatePost_shouldThrowOptimisticLockingFailureException_whenVersionMismatch() {

        PostId postId = new PostId(UUID.randomUUID());

        Post post = new Post(postId, new PostTitle("Post title"), new PostSummary("Post summary"),
                new PostContent("Post content"), PostStatus.DRAFT, LocalDate.of(2025, 1, 1));

        post.setVersion(1L);

        when(postRepository.findById(postId)).thenReturn(Optional.ofNullable(post));

        EditPostParameters parameters = new EditPostParameters(new PostTitle("New Post title"),
                new PostSummary("New Post summary"), new PostContent("New Post content"), PostStatus.PUBLISHED,
                LocalDate.now(), 2L);

        assertThrows(ObjectOptimisticLockingFailureException.class, () -> {
            postServiceImpl.updatePost(postId, parameters);
        });
    }


    @Test
    void testDeletePost() {

        PostId postId = new PostId(UUID.randomUUID());

        Post post = new Post(postId, new PostTitle("Post title"), new PostSummary("Post summary"),
                new PostContent("Post content"), PostStatus.DRAFT, LocalDate.of(2025, 1, 1));

        postServiceImpl.deletePost(postId);

        assertThrows(PostNotFoundException.class, () -> {
            postServiceImpl.getPostById(post.getPostId());
        });

        verify(postRepository, times(1)).deleteById(postId);
    }
}
