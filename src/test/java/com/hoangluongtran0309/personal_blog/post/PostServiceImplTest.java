package com.hoangluongtran0309.personal_blog.post;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PostServiceImplTest {

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostServiceImpl postServiceImpl;

    @Test
    void testCreatePost() {

        PostId postId = new PostId(UUID.randomUUID());

        CreatePostParameters parameters = new CreatePostParameters(new PostTitle("Post title"), new PostSummary("Post summary"),
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

}
