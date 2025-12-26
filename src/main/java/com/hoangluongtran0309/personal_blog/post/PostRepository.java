package com.hoangluongtran0309.personal_blog.post;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public interface PostRepository extends CrudRepository<Post, PostId>, PostRepositoryCustom {

}
