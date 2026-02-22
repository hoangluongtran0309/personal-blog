package com.hoangluongtran0309.personal_blog.user.infrastructure.model;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

import com.hoangluongtran0309.personal_blog.infrastructure.persistence.BaseEntity;
import com.hoangluongtran0309.personal_blog.user.domain.model.Gender;
import com.hoangluongtran0309.personal_blog.user.domain.model.UserRole;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserEntity extends BaseEntity {

    @Id
    @Column
    private UUID id;

    @Column
    private String username;

    @Column
    private String firstname;

    @Column
    private String lastname;

    @Column
    private String password;

    @Column
    private String email;

    @ElementCollection(targetClass = UserRole.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "roles")
    private Set<UserRole> roles;

    @Column
    private byte[] avatar;

    @Column
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column
    private LocalDate dateOfBirth;

    @Column
    private String headline;

    @Column
    private String bio;

    @Column
    private String phoneNumber;

    @Column
    private String street;

    @Column
    private String city;

    @Column
    private String country;

    @Column
    private String githubUrl;

    @Column
    private String xUrl;

    @Column
    private String facebookUrl;

    @Column
    private String linkedinUrl;

}
