package com.hoangluongtran0309.personal_blog.user.web.form;

import com.hoangluongtran0309.personal_blog.user.application.dto.ChangePasswordRequest;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordFormData {

    @NotBlank
    private String oldPassword;

    @NotBlank
    private String newPassword;

    @NotBlank
    private String confirmPassword;

    public ChangePasswordRequest toRequest() {
        return new ChangePasswordRequest(oldPassword, newPassword, confirmPassword);
    }

}
