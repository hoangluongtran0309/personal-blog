package com.hoangluongtran0309.personal_blog.user.infrastructure.mapper;

import org.springframework.stereotype.Component;

import com.hoangluongtran0309.personal_blog.user.domain.model.Address;
import com.hoangluongtran0309.personal_blog.user.domain.model.Avatar;
import com.hoangluongtran0309.personal_blog.user.domain.model.Bio;
import com.hoangluongtran0309.personal_blog.user.domain.model.Email;
import com.hoangluongtran0309.personal_blog.user.domain.model.Password;
import com.hoangluongtran0309.personal_blog.user.domain.model.PhoneNumber;
import com.hoangluongtran0309.personal_blog.user.domain.model.SocialLinks;
import com.hoangluongtran0309.personal_blog.user.domain.model.User;
import com.hoangluongtran0309.personal_blog.user.domain.model.UserId;
import com.hoangluongtran0309.personal_blog.user.domain.model.UserName;
import com.hoangluongtran0309.personal_blog.user.infrastructure.model.UserEntity;

@Component
public class UserMapper {

    public UserEntity toEntity(User user) {
        UserEntity entity = new UserEntity();
        entity.setId(user.getId().getValue());
        entity.setUsername(user.getUsername().getUsername());
        entity.setFirstname(user.getUsername().getFirstname());
        entity.setLastname(user.getUsername().getLastname());
        entity.setPassword(user.getPassword().getValue());
        entity.setEmail(user.getEmail().getValue());
        entity.setRoles(user.getRoles());

        entity.setAvatar(
                user.getAvatar() != null ? user.getAvatar().getValue() : null);

        entity.setGender(user.getGender());
        entity.setDateOfBirth(user.getDateOfBirth());
        entity.setHeadline(user.getBio().getHeadline());
        entity.setBio(user.getBio().getBio());
        entity.setPhoneNumber(user.getPhoneNumber().getValue());
        entity.setStreet(user.getAddress().getStreet());
        entity.setCity(user.getAddress().getCity());
        entity.setCountry(user.getAddress().getCountry());
        entity.setGithubUrl(user.getSocialLinks().getGithubUrl());
        entity.setXUrl(user.getSocialLinks().getxUrl());
        entity.setFacebookUrl(user.getSocialLinks().getFacebookUrl());
        entity.setLinkedinUrl(user.getSocialLinks().getLinkedinUrl());

        return entity;
    }

    public User toDomain(UserEntity entity) {
        User user = new User(
                new UserId(entity.getId()),
                new UserName(entity.getUsername(), entity.getFirstname(), entity.getLastname()),
                new Password(entity.getPassword()), entity.getRoles(),
                new Email(entity.getEmail()),
                entity.getGender(), entity.getDateOfBirth(),
                new Bio(entity.getHeadline(), entity.getBio()),
                new PhoneNumber(entity.getPhoneNumber()),
                new Address(entity.getStreet(), entity.getCity(), entity.getCountry()),
                new SocialLinks(entity.getGithubUrl(), entity.getXUrl(), entity.getFacebookUrl(),
                        entity.getLinkedinUrl()));

        if (entity.getAvatar() != null) {
            user.changeAvatar(new Avatar(entity.getAvatar()));
        }

        return user;
    }

}
