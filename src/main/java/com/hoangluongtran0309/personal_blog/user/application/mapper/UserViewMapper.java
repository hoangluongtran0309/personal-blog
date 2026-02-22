package com.hoangluongtran0309.personal_blog.user.application.mapper;

import java.util.Base64;

import org.springframework.stereotype.Component;

import com.hoangluongtran0309.personal_blog.user.application.dto.UserView;
import com.hoangluongtran0309.personal_blog.user.domain.model.User;

@Component
public class UserViewMapper {

    public UserView toView(User user) {
        return new UserView(
                user.getId().getValue(),
                user.getUsername().getUsername(),
                user.getUsername().getFirstname(),
                user.getUsername().getLastname(),
                user.getEmail().getValue(),
                user.getAvatar() != null
                        ? Base64.getEncoder().encodeToString(user.getAvatar().getValue())
                        : null,
                user.getGender().name(),
                user.getDateOfBirth().toString(),
                user.getPhoneNumber().getValue(),
                user.getBio().getHeadline(),
                user.getBio().getBio(),
                user.getAddress().getStreet(),
                user.getAddress().getCity(),
                user.getAddress().getCountry(),
                user.getSocialLinks().getGithubUrl(),
                user.getSocialLinks().getxUrl(),
                user.getSocialLinks().getFacebookUrl(),
                user.getSocialLinks().getLinkedinUrl());
    }

}
