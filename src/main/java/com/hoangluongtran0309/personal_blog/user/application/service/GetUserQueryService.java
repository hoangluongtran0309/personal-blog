package com.hoangluongtran0309.personal_blog.user.application.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hoangluongtran0309.personal_blog.user.application.dto.UserFilter;
import com.hoangluongtran0309.personal_blog.user.application.dto.UserView;
import com.hoangluongtran0309.personal_blog.user.application.mapper.UserViewMapper;
import com.hoangluongtran0309.personal_blog.user.application.usecase.GetUserQueryUseCase;
import com.hoangluongtran0309.personal_blog.user.domain.model.Email;
import com.hoangluongtran0309.personal_blog.user.domain.model.User;
import com.hoangluongtran0309.personal_blog.user.domain.model.UserId;
import com.hoangluongtran0309.personal_blog.user.domain.model.UserName;
import com.hoangluongtran0309.personal_blog.user.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetUserQueryService implements GetUserQueryUseCase {

    private final UserViewMapper mapper;

    private final UserRepository userRepository;

    @Override
    public UserView getUserById(UUID id) {
        User user = userRepository.findById(new UserId(id));
        return mapper.toView(user);
    }

    @Override
    public UserView getUserByUsername(String username) {
        User user = userRepository.findByUsername(new UserName(username));
        return mapper.toView(user);
    }

    @Override
    public UserView getUserByEmail(String email) {
        User user = userRepository.findByEmail(new Email(email));
        return mapper.toView(user);
    }

    @Override
    public Page<UserView> getAllUsers(UserFilter filter, Pageable pageable) {
        return userRepository
                .searchUsers(filter.search(), pageable)
                .map(mapper::toView);
    }

}
