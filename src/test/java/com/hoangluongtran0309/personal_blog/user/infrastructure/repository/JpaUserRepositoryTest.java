package com.hoangluongtran0309.personal_blog.user.infrastructure.repository;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import com.hoangluongtran0309.personal_blog.infrastructure.persistence.JpaConfig;
import com.hoangluongtran0309.personal_blog.user.domain.model.Address;
import com.hoangluongtran0309.personal_blog.user.domain.model.Bio;
import com.hoangluongtran0309.personal_blog.user.domain.model.Email;
import com.hoangluongtran0309.personal_blog.user.domain.model.Gender;
import com.hoangluongtran0309.personal_blog.user.domain.model.Password;
import com.hoangluongtran0309.personal_blog.user.domain.model.PhoneNumber;
import com.hoangluongtran0309.personal_blog.user.domain.model.SocialLinks;
import com.hoangluongtran0309.personal_blog.user.domain.model.User;
import com.hoangluongtran0309.personal_blog.user.domain.model.UserId;
import com.hoangluongtran0309.personal_blog.user.domain.model.UserName;
import com.hoangluongtran0309.personal_blog.user.infrastructure.mapper.UserMapper;
import com.hoangluongtran0309.personal_blog.user.infrastructure.model.UserEntity;

@DataJpaTest
@ActiveProfiles("data-jpa-test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({ UserMapper.class, JpaConfig.class })
class JpaUserRepositoryTest {

    @Autowired
    private JpaUserRepository jpaUserRepository;

    @Autowired
    private UserMapper mapper;

    private UserEntity saveUser(String username, String email, String firstName, String lastName) {
        User user = User.createUser(
                new UserId(UUID.randomUUID()),
                new UserName(username, firstName, lastName),
                new Password("password123"),
                new Email(email),
                Gender.MALE,
                LocalDate.of(2000, 1, 1),
                new Bio("Title", "Description"),
                new PhoneNumber("0123456789"),
                new Address("Street", "City", "Country"),
                new SocialLinks(
                        "https://github.com/johndoe",
                        "https://x.com/johndoe",
                        "https://facebook.com/johndoe",
                        "https://linkedin.com/in/johndoe"));

        return jpaUserRepository.save(mapper.toEntity(user));
    }

    @Nested
    @DisplayName("findByEmail()")
    class FindByEmail {

        @Test
        @DisplayName("trả về user khi email tồn tại")
        void findByEmail_existingEmail_returnsUser() {
            saveUser("jane123", "jane@mail.com", "Jane", "Doe");

            var result = jpaUserRepository.findByEmail("jane@mail.com");

            assertThat(result).isPresent();
            assertThat(result.get().getUsername()).isEqualTo("jane123");
        }

        @Test
        @DisplayName("trả về empty khi email không tồn tại")
        void findByEmail_unknownEmail_returnsEmpty() {
            var result = jpaUserRepository.findByEmail("unknown@mail.com");

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("searchUsers()")
    class SearchUsers {

        @BeforeEach
        void setUp() {
            saveUser("john1", "john1@mail.com", "John", "Smith");
            saveUser("anna1", "anna@mail.com", "Anna", "Taylor");
        }

        @Test
        @DisplayName("tìm đúng user khi search theo firstname")
        void searchUsers_byFirstname_returnsMatchingUser() {
            Page<UserEntity> page = jpaUserRepository.searchUsers("john", PageRequest.of(0, 10));

            assertThat(page.getTotalElements()).isEqualTo(1);
            assertThat(page.getContent().get(0).getUsername()).isEqualTo("john1");
        }

        @Test
        @DisplayName("trả về tất cả user khi search là null")
        void searchUsers_nullSearch_returnsAllUsers() {
            Page<UserEntity> page = jpaUserRepository.searchUsers(null, PageRequest.of(0, 10));

            assertThat(page.getTotalElements()).isEqualTo(2);
        }

        @Test
        @DisplayName("search không phân biệt hoa thường")
        void searchUsers_uppercaseKeyword_stillMatches() {
            Page<UserEntity> page = jpaUserRepository.searchUsers("JOHN", PageRequest.of(0, 10));

            assertThat(page.getTotalElements()).isEqualTo(1);
            assertThat(page.getContent().get(0).getUsername()).isEqualTo("john1");
        }

        @Test
        @DisplayName("tìm theo lastname trả về đúng user")
        void searchUsers_byLastname_returnsMatchingUser() {
            Page<UserEntity> page = jpaUserRepository.searchUsers("Taylor", PageRequest.of(0, 10));

            assertThat(page.getTotalElements()).isEqualTo(1);
            assertThat(page.getContent().get(0).getUsername()).isEqualTo("anna1");
        }

        @Test
        @DisplayName("tìm theo email trả về đúng user")
        void searchUsers_byEmail_returnsMatchingUser() {
            Page<UserEntity> page = jpaUserRepository.searchUsers("anna@mail.com", PageRequest.of(0, 10));

            assertThat(page.getTotalElements()).isEqualTo(1);
            assertThat(page.getContent().get(0).getUsername()).isEqualTo("anna1");
        }

        @Test
        @DisplayName("tìm theo username trả về đúng user")
        void searchUsers_byUsername_returnsMatchingUser() {
            Page<UserEntity> page = jpaUserRepository.searchUsers("john1", PageRequest.of(0, 10));

            assertThat(page.getTotalElements()).isEqualTo(1);
            assertThat(page.getContent().get(0).getUsername()).isEqualTo("john1");
        }

        @Test
        @DisplayName("keyword không khớp bất kỳ field nào → trả về empty page")
        void searchUsers_noMatch_returnsEmptyPage() {
            Page<UserEntity> page = jpaUserRepository.searchUsers("zzznomatch", PageRequest.of(0, 10));

            assertThat(page.getTotalElements()).isZero();
            assertThat(page.getContent()).isEmpty();
        }

        @Test
        @DisplayName("phân trang đúng — page 1 chỉ có 1 phần tử khi size = 1")
        void searchUsers_pagination_returnsCorrectPage() {
            Page<UserEntity> page = jpaUserRepository.searchUsers(null, PageRequest.of(0, 1));

            assertThat(page.getTotalElements()).isEqualTo(2);
            assertThat(page.getContent()).hasSize(1);
            assertThat(page.getTotalPages()).isEqualTo(2);
        }
    }

}