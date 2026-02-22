package com.hoangluongtran0309.personal_blog.user.application.service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.hoangluongtran0309.personal_blog.user.application.dto.UpdateUserRequest;
import com.hoangluongtran0309.personal_blog.user.application.exception.AvatarStorageException;
import com.hoangluongtran0309.personal_blog.user.application.usecase.UpdateUserUseCase;
import com.hoangluongtran0309.personal_blog.user.domain.model.Address;
import com.hoangluongtran0309.personal_blog.user.domain.model.Avatar;
import com.hoangluongtran0309.personal_blog.user.domain.model.Bio;
import com.hoangluongtran0309.personal_blog.user.domain.model.Email;
import com.hoangluongtran0309.personal_blog.user.domain.model.Gender;
import com.hoangluongtran0309.personal_blog.user.domain.model.PhoneNumber;
import com.hoangluongtran0309.personal_blog.user.domain.model.SocialLinks;
import com.hoangluongtran0309.personal_blog.user.domain.model.User;
import com.hoangluongtran0309.personal_blog.user.domain.model.UserId;
import com.hoangluongtran0309.personal_blog.user.domain.model.UserName;
import com.hoangluongtran0309.personal_blog.user.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UpdateUserService implements UpdateUserUseCase {

    private final UserRepository userRepository;

    @Override
    public void update(UUID id, UpdateUserRequest request) {
        User user = userRepository.findById(new UserId(id));

        user.update(new UserName(request.username(), request.firstname(), request.lastname()),
                new Email(request.email()), Gender.valueOf(request.gender()),
                LocalDate.parse(request.dateOfBirth()), new Bio(request.headline(), request.bio()),
                new PhoneNumber(request.phoneNumber()),
                new Address(request.street(), request.city(), request.country()),
                new SocialLinks(request.githubUrl(), request.xUrl(), request.facebookUrl(), request.linkedinUrl()));

        storeAvatarIfPresent(request, user);

        userRepository.save(user);
    }

    private void storeAvatarIfPresent(UpdateUserRequest request, User user) {
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
