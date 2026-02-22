package com.hoangluongtran0309.personal_blog.user.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.hoangluongtran0309.personal_blog.user.domain.model.Email;
import com.hoangluongtran0309.personal_blog.user.domain.model.User;
import com.hoangluongtran0309.personal_blog.user.domain.model.UserId;
import com.hoangluongtran0309.personal_blog.user.domain.model.UserName;

public interface UserRepository {

    UserId nextId();

    boolean existsByUsernameAndIdNot(UserName username, UserId id);

    boolean existsByEmailAndIdNot(Email email, UserId id);

    User findById(UserId id);

    User findByUsername(UserName username);

    User findByEmail(Email email);

    Page<User> searchUsers(String keyword, Pageable pageable);

    User save(User user);

    void delete(UserId userId);

}
