package com.hoangluongtran0309.personal_blog.user.application.usecase;

import java.util.UUID;

import com.hoangluongtran0309.personal_blog.user.application.dto.UpdateUserRequest;

public interface UpdateUserUseCase {

    void update(UUID id, UpdateUserRequest request);

}
