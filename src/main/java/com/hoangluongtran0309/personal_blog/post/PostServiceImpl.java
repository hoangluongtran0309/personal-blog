package com.hoangluongtran0309.personal_blog.post;

import java.util.Objects;

import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;

    public PostServiceImpl(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Override
    public Post createPost(CreatePostParameters parameters) {
        PostId id = postRepository.nextPostId();
        Post post = new Post(id, parameters.getPostTitle(), parameters.getPostSummary(), parameters.getPostContent(),
                parameters.getPostStatus(), parameters.getPublishDate());
        return postRepository.save(post);
    }

    @Override
    public Post getPostById(PostId id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException("Post not found with" + id.toString()));
    }

    @Override
    public void updatePost(PostId id, EditPostParameters parameters) {
        Post post = postRepository.findById(id).orElseThrow(() -> new PostNotFoundException("Post not found with" + id.toString()));
        if (!Objects.equals(post.getVersion(), parameters.getVersion())) {
            throw new ObjectOptimisticLockingFailureException(Post.class, id.asString());
        }
        parameters.update(post);
    }
}
