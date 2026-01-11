package com.hoangluongtran0309.personal_blog.post;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import org.junit.jupiter.api.Test;

public class PostTest {

    @Test
    void createPost_WithDefaultDraftStatus_ShouldSuccess() {
        Post post = Post.create(new PostId(UUID.randomUUID()), new PostTitle("New Post Title"));
        assertNotNull(post);
        assertEquals(PostStatus.DRAFT, post.getPostStatus());
        assertEquals("New Post Title", post.getPostTitle().getTitle());
    }

    @Test
    void createPost_WithDefaultDraftStatus_ShouldFailure_WhenValueObjectsIsInvalid() {
        assertThrowsExactly(IllegalArgumentException.class, () -> {
            Post.create(new PostId(null), new PostTitle("New Post Title"));
        }, "Post ID must not be null");
        assertThrowsExactly(IllegalArgumentException.class, () -> {
            Post.create(new PostId(UUID.randomUUID()), new PostTitle(""));

        }, "Post title must not be blank");
    }

}
