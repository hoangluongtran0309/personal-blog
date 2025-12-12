package com.hoangluongtran0309.personal_blog.post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostService {

    Post createPost(CreatePostParameters parameters);

    Post getPostById(PostId id);

    void updatePost(PostId id, EditPostParameters parameters);

    void deletePost(PostId id);

    Page<Post> getAllPosts(Pageable pageable);

    Page<Post> getAllPublishedPosts(Pageable pageable);
    
}
