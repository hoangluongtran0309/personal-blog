package com.hoangluongtran0309.personal_blog.user.application.usecase;

import com.hoangluongtran0309.personal_blog.user.application.dto.CreateUserRequest;
import com.hoangluongtran0309.personal_blog.user.domain.model.User;

public interface CreateUserUseCase {

    User createAdmin(CreateUserRequest request);

    User createUser(CreateUserRequest request);

}
