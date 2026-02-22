package com.hoangluongtran0309.personal_blog.user.application.dto;

import org.springframework.web.multipart.MultipartFile;

public record CreateUserRequest(
        String username,
        String firstname,
        String lastname,
        String password,
        String email,
        MultipartFile avatar,
        String gender,
        String dateOfBirth,
        String phoneNumber,
        String headline,
        String bio,
        String street,
        String city,
        String country,
        String githubUrl,
        String xUrl,
        String facebookUrl,
        String linkedinUrl) {

}
