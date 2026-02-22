package com.hoangluongtran0309.personal_blog.user.domain.model;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;

public class User {

    private UserId id;

    private UserName username;

    private Password password;

    private Set<UserRole> roles;

    private Email email;

    private Avatar avatar;

    private Gender gender;

    private LocalDate dateOfBirth;

    private Bio bio;

    private PhoneNumber phoneNumber;

    private Address address;

    private SocialLinks socialLinks;

    protected User() {

    }

    public User(UserId id, UserName username, Password password, Set<UserRole> roles, Email email,
            Gender gender, LocalDate dateOfBirth, Bio bio, PhoneNumber phoneNumber, Address address,
            SocialLinks socialLinks) {
        this.id = Objects.requireNonNull(id);
        this.username = Objects.requireNonNull(username);
        this.password = Objects.requireNonNull(password);
        this.roles = Objects.requireNonNull(roles);
        this.email = Objects.requireNonNull(email);
        this.gender = Objects.requireNonNull(gender);
        this.dateOfBirth = Objects.requireNonNull(dateOfBirth);
        this.bio = Objects.requireNonNull(bio);
        this.phoneNumber = Objects.requireNonNull(phoneNumber);
        this.address = Objects.requireNonNull(address);
        this.socialLinks = Objects.requireNonNull(socialLinks);
    }

    public UserId getId() {
        return id;
    }

    public UserName getUsername() {
        return username;
    }

    public Password getPassword() {
        return password;
    }

    public Set<UserRole> getRoles() {
        return roles;
    }

    public Email getEmail() {
        return email;
    }

    public Avatar getAvatar() {
        return avatar;
    }

    public Gender getGender() {
        return gender;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public Bio getBio() {
        return bio;
    }

    public PhoneNumber getPhoneNumber() {
        return phoneNumber;
    }

    public Address getAddress() {
        return address;
    }

    public SocialLinks getSocialLinks() {
        return socialLinks;
    }

    public static User createAdmin(UserId id, UserName username, Password password, Email email,
            Gender gender, LocalDate dateOfBirth, Bio bio, PhoneNumber phoneNumber, Address address,
            SocialLinks socialLinks) {
        return new User(id, username, password, Set.of(UserRole.ADMIN), email, gender, dateOfBirth, bio,
                phoneNumber, address, socialLinks);
    }

    public static User createUser(UserId id, UserName username, Password password, Email email,
            Gender gender, LocalDate dateOfBirth, Bio bio, PhoneNumber phoneNumber, Address address,
            SocialLinks socialLinks) {
        return new User(id, username, password, Set.of(UserRole.USER), email, gender, dateOfBirth, bio,
                phoneNumber, address, socialLinks);
    }

    public void update(UserName username, Email email,
            Gender gender, LocalDate dateOfBirth, Bio bio, PhoneNumber phoneNumber, Address address,
            SocialLinks socialLinks) {
        this.username = Objects.requireNonNull(username);
        this.email = Objects.requireNonNull(email);
        this.gender = Objects.requireNonNull(gender);
        this.dateOfBirth = Objects.requireNonNull(dateOfBirth);
        this.bio = Objects.requireNonNull(bio);
        this.phoneNumber = Objects.requireNonNull(phoneNumber);
        this.address = Objects.requireNonNull(address);
        this.socialLinks = Objects.requireNonNull(socialLinks);
    }

    public void changeAvatar(Avatar avatar) {
        this.avatar = Objects.requireNonNull(avatar);
    }

    public void changePassword(Password password) {
        this.password = Objects.requireNonNull(password);
    }

}
