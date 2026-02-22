package com.hoangluongtran0309.personal_blog.user.web.form;

import org.springframework.web.multipart.MultipartFile;

import com.hoangluongtran0309.personal_blog.user.application.dto.UpdateUserRequest;
import com.hoangluongtran0309.personal_blog.user.application.dto.UserView;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserFormData {

    private String id;

    private String avatarBase64Encoded;

    private MultipartFile avatar;

    @NotBlank
    private String username;

    @NotBlank
    private String firstname;

    @NotBlank
    private String lastname;

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

    public UpdateUserRequest toRequest() {
        return new UpdateUserRequest(username, firstname, lastname, email, avatar, gender, dateOfBirth,
                phoneNumber,
                headline,
                bio, street, city, country, githubUrl, xUrl, facebookUrl, linkedinUrl);
    }

    public static UpdateUserFormData fromData(UserView userView) {
        UpdateUserFormData data = new UpdateUserFormData();
        data.setId(userView.id().toString());
        data.setUsername(userView.username());
        data.setFirstname(userView.firstname());
        data.setLastname(userView.lastname());
        data.setEmail(userView.email());
        data.setGender(userView.gender());
        data.setDateOfBirth(userView.dateOfBirth());
        data.setPhoneNumber(userView.phoneNumber());
        data.setHeadline(userView.headline());
        data.setBio(userView.bio());
        data.setStreet(userView.street());
        data.setCity(userView.city());
        data.setCountry(userView.country());
        data.setGithubUrl(userView.githubUrl());
        data.setXUrl(userView.xUrl());
        data.setFacebookUrl(userView.facebookUrl());
        data.setLinkedinUrl(userView.linkedinUrl());

        if (userView.avatar() != null) {
            data.setAvatarBase64Encoded(userView.avatar());
        }

        return data;
    }

}
