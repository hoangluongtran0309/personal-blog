package com.hoangluongtran0309.personal_blog.post;

public interface PostService {

    Post createPost(CreatePostParameters parameters);

    Post getPostById(PostId id);

    void updatePost(PostId id, EditPostParameters parameters);

    void deletePost(PostId id);
}
