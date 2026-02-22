package com.hoangluongtran0309.personal_blog.user.infrastructure.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.hoangluongtran0309.personal_blog.user.domain.model.Email;
import com.hoangluongtran0309.personal_blog.user.domain.model.User;
import com.hoangluongtran0309.personal_blog.user.domain.model.UserId;
import com.hoangluongtran0309.personal_blog.user.domain.model.UserName;
import com.hoangluongtran0309.personal_blog.user.domain.repository.UserRepository;
import com.hoangluongtran0309.personal_blog.user.infrastructure.exception.UserAlreadyExistsException;
import com.hoangluongtran0309.personal_blog.user.infrastructure.exception.UserNotFoundException;
import com.hoangluongtran0309.personal_blog.user.infrastructure.mapper.UserMapper;
import com.hoangluongtran0309.personal_blog.user.infrastructure.model.UserEntity;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepository {

    private final UserMapper mapper;

    private final JpaUserRepository jpaUserRepository;

    @Override
    public UserId nextId() {
        return new UserId(UUID.randomUUID());
    }

    @Override
    public boolean existsByUsernameAndIdNot(UserName username, UserId id) {
        return jpaUserRepository.existsByUsernameAndIdNot(username.getUsername(), id.getValue());
    }

    @Override
    public boolean existsByEmailAndIdNot(Email email, UserId id) {
        return jpaUserRepository.existsByEmailAndIdNot(email.getValue(), id.getValue());
    }

    @Override
    public User findById(UserId id) {
        UserEntity entity = jpaUserRepository.findById(id.getValue())
                .orElseThrow(() -> new UserNotFoundException("User not found with ID " + id.getValue().toString()));
        return mapper.toDomain(entity);
    }

    @Override
    public User findByUsername(UserName username) {
        UserEntity entity = jpaUserRepository.findByUsername(username.getUsername())
                .orElseThrow(() -> new UserNotFoundException("User not found with username " + username.getUsername()));
        return mapper.toDomain(entity);
    }

    @Override
    public User findByEmail(Email email) {
        UserEntity entity = jpaUserRepository.findByEmail(email.getValue())
                .orElseThrow(() -> new UserNotFoundException("User not found with email " + email.getValue()));
        return mapper.toDomain(entity);
    }

    @Override
    public Page<User> searchUsers(String keyword, Pageable pageable) {
        return jpaUserRepository
                .searchUsers(keyword, pageable)
                .map(mapper::toDomain);
    }

    @Override
    public User save(User user) {
        if (existsByUsernameAndIdNot(user.getUsername(), user.getId())) {
            throw new UserAlreadyExistsException(
                    "User already existed with username " + user.getUsername().getUsername());
        }

        if (existsByEmailAndIdNot(user.getEmail(), user.getId())) {
            throw new UserAlreadyExistsException("User already existed with email " + user.getEmail().getValue());
        }

        UserEntity entity = jpaUserRepository.save(mapper.toEntity(user));
        return mapper.toDomain(entity);
    }

    @Override
    public void delete(UserId userId) {
        jpaUserRepository.deleteById(userId.getValue());
    }

}
