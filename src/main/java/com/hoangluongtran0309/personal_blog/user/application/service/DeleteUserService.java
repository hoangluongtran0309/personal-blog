package com.hoangluongtran0309.personal_blog.user.application.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.hoangluongtran0309.personal_blog.user.application.usecase.DeleteUserUseCase;
import com.hoangluongtran0309.personal_blog.user.domain.model.UserId;
import com.hoangluongtran0309.personal_blog.user.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeleteUserService implements DeleteUserUseCase {

    private final UserRepository userRepository;

    @Override
    public void delete(UUID id) {
        userRepository.delete(new UserId(id));
    }

}
