package com.hoangluongtran0309.personal_blog.post;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
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

    @Test
    void updatePost_ShouldSuccess_WhenPostIsDraftStatus() {
        Post post = Post.create(new PostId(UUID.randomUUID()), new PostTitle("New Post Title"));
        assertNull(post.getPostBody());
        assertNull(post.getPostSlug());
        post.update(new PostTitle("New Updated Post Title"), new PostBody("New Updated Post Summary", "New Updated Post Content"), new PostSlug("new-updated-post-slug"));
        assertEquals("New Updated Post Title", post.getPostTitle().getTitle());
        assertEquals("New Updated Post Summary", post.getPostBody().getSummary());
        assertEquals("New Updated Post Content", post.getPostBody().getContent());
        assertEquals("new-updated-post-slug", post.getPostSlug().getSlug());
    }

    @Test
    void updatePost_ShouldFailure_WhenPostIsNotDraftStatus() {
        Post post = new Post(new PostId(UUID.randomUUID()), new PostTitle("New Post Title"), new PostBody("New Post Summary", "New Post Content"), new PostSlug("new-post-slug"), LocalDateTime.now(), PostStatus.PUBLISHED);
        assertThrowsExactly(IllegalStateException.class, () -> {
            post.update(new PostTitle("New Updated Post Summary"), new PostBody("New Updated Post Summary", "New Updated Post Content"), new PostSlug("new-updated-post-slug"));
        }, "Only draft posts can be updated");
    }

}
