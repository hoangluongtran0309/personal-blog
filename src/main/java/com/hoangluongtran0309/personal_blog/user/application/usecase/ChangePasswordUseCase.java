package com.hoangluongtran0309.personal_blog.user.application.usecase;

import java.util.UUID;

import com.hoangluongtran0309.personal_blog.user.application.dto.ChangePasswordRequest;

public interface ChangePasswordUseCase {

    void change(UUID id, ChangePasswordRequest request);

}
