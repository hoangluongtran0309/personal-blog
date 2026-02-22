package com.hoangluongtran0309.personal_blog.user.infrastructure.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hoangluongtran0309.personal_blog.user.infrastructure.model.UserEntity;

public interface JpaUserRepository
        extends JpaRepository<UserEntity, UUID> {

    boolean existsByUsernameAndIdNot(String username, UUID id);

    boolean existsByEmailAndIdNot(String email, UUID id);

    Optional<UserEntity> findByUsername(String username);

    Optional<UserEntity> findByEmail(String email);

    @Query("""
            SELECT u FROM UserEntity u
            WHERE (:search IS NULL OR :search = ''
                OR LOWER(u.firstname) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(u.lastname)  LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(u.email)     LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(u.username)  LIKE LOWER(CONCAT('%', :search, '%')))
            """)
    Page<UserEntity> searchUsers(@Param("search") String search, Pageable pageable);

}
