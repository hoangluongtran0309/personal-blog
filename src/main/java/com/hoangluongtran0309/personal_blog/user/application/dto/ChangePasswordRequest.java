package com.hoangluongtran0309.personal_blog.user.application.dto;

public record ChangePasswordRequest(
        String oldPassword,
        String newPassword,
        String confirmPassword) {

}
