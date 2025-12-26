package com.hoangluongtran0309.personal_blog.post;

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
        return postRepository.findById(id).orElseThrow(() -> new PostNotFoundException(id));
    }
}
