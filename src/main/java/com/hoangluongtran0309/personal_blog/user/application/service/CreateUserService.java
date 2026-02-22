package com.hoangluongtran0309.personal_blog.user.application.service;

import java.io.IOException;
import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.hoangluongtran0309.personal_blog.user.application.dto.CreateUserRequest;
import com.hoangluongtran0309.personal_blog.user.application.exception.AvatarStorageException;
import com.hoangluongtran0309.personal_blog.user.application.usecase.CreateUserUseCase;
import com.hoangluongtran0309.personal_blog.user.domain.model.Address;
import com.hoangluongtran0309.personal_blog.user.domain.model.Avatar;
import com.hoangluongtran0309.personal_blog.user.domain.model.Bio;
import com.hoangluongtran0309.personal_blog.user.domain.model.Email;
import com.hoangluongtran0309.personal_blog.user.domain.model.Gender;
import com.hoangluongtran0309.personal_blog.user.domain.model.Password;
import com.hoangluongtran0309.personal_blog.user.domain.model.PhoneNumber;
import com.hoangluongtran0309.personal_blog.user.domain.model.SocialLinks;
import com.hoangluongtran0309.personal_blog.user.domain.model.User;
import com.hoangluongtran0309.personal_blog.user.domain.model.UserId;
import com.hoangluongtran0309.personal_blog.user.domain.model.UserName;
import com.hoangluongtran0309.personal_blog.user.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CreateUserService implements CreateUserUseCase {

    private final UserRepository userRepository;

    @Override
    public User createAdmin(CreateUserRequest request) {
        UserId id = userRepository.nextId();
        User user = User.createAdmin(id,
                new UserName(request.username(), request.firstname(), request.lastname()),
                new Password(request.password()),
                new Email(request.email()),
                Gender.valueOf(request.gender()), LocalDate.parse(request.dateOfBirth()),
                new Bio(request.headline(), request.bio()), new PhoneNumber(request.phoneNumber()),
                new Address(request.street(), request.city(), request.country()),
                new SocialLinks(request.githubUrl(), request.xUrl(), request.facebookUrl(), request.linkedinUrl()));

        storeAvatarIfPresent(request, user);

        return userRepository.save(user);
    }

    @Override
    public User createUser(CreateUserRequest request) {
        UserId id = userRepository.nextId();

        User user = User.createUser(id,
                new UserName(request.username(), request.firstname(), request.lastname()),
                new Password(request.password()),
                new Email(request.email()),
                Gender.valueOf(request.gender()), LocalDate.parse(request.dateOfBirth()),
                new Bio(request.headline(), request.bio()), new PhoneNumber(request.phoneNumber()),
                new Address(request.street(), request.city(), request.country()),
                new SocialLinks(request.githubUrl(), request.xUrl(), request.facebookUrl(), request.linkedinUrl()));

        storeAvatarIfPresent(request, user);

        return userRepository.save(user);
    }

    private void storeAvatarIfPresent(CreateUserRequest request, User user) {
        MultipartFile avatar = request.avatar();

        if (avatar != null && !avatar.isEmpty()) {
            try {
                user.changeAvatar(new Avatar(avatar.getBytes()));
            } catch (IOException e) {
                throw new AvatarStorageException("Failed to store avatar");
            }
        }
    }

}
