package com.hoangluongtran0309.personal_blog.user.application.dto;

import java.util.UUID;

public record UserView(
        UUID id,
        String username,
        String firstname,
        String lastname,
        String email,
        String avatar,
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
