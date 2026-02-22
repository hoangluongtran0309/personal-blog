package com.hoangluongtran0309.personal_blog.user.application.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.hoangluongtran0309.personal_blog.user.application.dto.ChangePasswordRequest;
import com.hoangluongtran0309.personal_blog.user.application.exception.PasswordNotMatchException;
import com.hoangluongtran0309.personal_blog.user.application.usecase.ChangePasswordUseCase;
import com.hoangluongtran0309.personal_blog.user.domain.model.Password;
import com.hoangluongtran0309.personal_blog.user.domain.model.User;
import com.hoangluongtran0309.personal_blog.user.domain.model.UserId;
import com.hoangluongtran0309.personal_blog.user.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChangePasswordService implements ChangePasswordUseCase {

    private final UserRepository userRepository;

    @Override
    public void change(UUID id, ChangePasswordRequest request) {
        User user = userRepository.findById(new UserId(id));
        if (!request.oldPassword().equals(user.getPassword().getValue())) {
            throw new PasswordNotMatchException("Password not match");
        }

        if (!request.newPassword().equals(request.confirmPassword())) {
            throw new PasswordNotMatchException("Password not match");
        }

        user.changePassword(new Password(request.newPassword()));
    }

}
