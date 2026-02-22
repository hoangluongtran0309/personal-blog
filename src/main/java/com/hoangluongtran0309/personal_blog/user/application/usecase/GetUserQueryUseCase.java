package com.hoangluongtran0309.personal_blog.user.application.usecase;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.hoangluongtran0309.personal_blog.user.application.dto.UserFilter;
import com.hoangluongtran0309.personal_blog.user.application.dto.UserView;

public interface GetUserQueryUseCase {

    UserView getUserById(UUID id);

    UserView getUserByUsername(String username);

    UserView getUserByEmail(String email);

    Page<UserView> getAllUsers(UserFilter filter, Pageable pageable);

}