package com.hoangluongtran0309.personal_blog.user.web.form;

import org.springframework.web.multipart.MultipartFile;

import com.hoangluongtran0309.personal_blog.user.application.dto.CreateUserRequest;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUserFormData {

    private MultipartFile avatar;

    private String avatarBase64Encoded;

    @NotBlank
    private String username;

    @NotBlank
    private String firstname;

    @NotBlank
    private String lastname;

    @NotBlank
    private String password;

    @NotBlank
    private String email;

    @NotBlank
    private String gender;

    @NotBlank
    private String dateOfBirth;

    @NotBlank
    private String phoneNumber;

    @NotBlank
    private String headline;

    @NotBlank
    private String bio;

    @NotBlank
    private String street;

    @NotBlank
    private String city;

    @NotBlank
    private String country;

    @NotBlank
    private String githubUrl;

    @NotBlank
    private String xUrl;

    @NotBlank
    private String facebookUrl;

    @NotBlank
    private String linkedinUrl;

    public CreateUserRequest toRequest() {
        return new CreateUserRequest(username, firstname, lastname, password, email, avatar, gender, dateOfBirth,
                phoneNumber, headline, bio, street, city, country, githubUrl, xUrl, facebookUrl, linkedinUrl);
    }

}
