package com.hoangluongtran0309.personal_blog.user.domain.model;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class UserTest {

    private UserId userId;
    private UserName userName;
    private Password password;
    private Email email;
    private Gender gender;
    private LocalDate dateOfBirth;
    private Bio bio;
    private PhoneNumber phoneNumber;
    private Address address;
    private SocialLinks socialLinks;

    @BeforeEach
    void setUp() {
        userId = new UserId(UUID.randomUUID());
        userName = new UserName("john_doe", "John", "Doe");
        password = new Password("password123");
        email = new Email("john.doe@gmail.com");
        gender = Gender.MALE;
        dateOfBirth = LocalDate.of(2000, 1, 1);
        bio = new Bio("Software Developer", "Passionate backend developer.");
        phoneNumber = new PhoneNumber("0123456789");
        address = new Address("123 Main Street", "New York", "USA");
        socialLinks = new SocialLinks(
                "https://github.com/johndoe",
                "https://x.com/johndoe",
                "https://facebook.com/johndoe",
                "https://linkedin.com/in/johndoe");
    }

    private User createDefaultUser() {
        return User.createUser(userId, userName, password, email,
                gender, dateOfBirth, bio, phoneNumber, address, socialLinks);
    }

    @Nested
    @DisplayName("createAdmin()")
    class CreateAdmin {

        @Test
        @DisplayName("gán role ADMIN và lưu đúng id")
        void createAdmin_assignsAdminRoleAndCorrectId() {
            User user = User.createAdmin(userId, userName, password, email,
                    gender, dateOfBirth, bio, phoneNumber, address, socialLinks);

            assertThat(user.getRoles()).containsExactly(UserRole.ADMIN);
            assertThat(user.getId()).isEqualTo(userId);
        }
    }

    @Nested
    @DisplayName("createUser()")
    class CreateUser {

        @Test
        @DisplayName("gán role USER")
        void createUser_assignsUserRole() {
            User user = createDefaultUser();

            assertThat(user.getRoles()).containsExactly(UserRole.USER);
        }

        @Test
        @DisplayName("ném NullPointerException khi id là null")
        void createUser_nullId_throwsNullPointerException() {
            assertThrows(NullPointerException.class, () -> User.createUser(null, userName, password, email,
                    gender, dateOfBirth, bio, phoneNumber, address, socialLinks));
        }

        @Test
        @DisplayName("ném IllegalArgumentException khi username là blank")
        void createUser_blankUsername_throwsIllegalArgumentException() {
            assertThrows(IllegalArgumentException.class,
                    () -> User.createUser(userId, new UserName("", "John", "Doe"), password, email,
                            gender, dateOfBirth, bio, phoneNumber, address, socialLinks));
        }

        @Test
        @DisplayName("ném NullPointerException khi email là null")
        void createUser_nullEmail_throwsNullPointerException() {
            assertThrows(NullPointerException.class, () -> User.createUser(userId, userName, password, null,
                    gender, dateOfBirth, bio, phoneNumber, address, socialLinks));
        }

        @Test
        @DisplayName("ném NullPointerException khi password là null")
        void createUser_nullPassword_throwsNullPointerException() {
            assertThrows(NullPointerException.class, () -> User.createUser(userId, userName, null, email,
                    gender, dateOfBirth, bio, phoneNumber, address, socialLinks));
        }
    }

    @Nested
    @DisplayName("update()")
    class Update {

        @Test
        @DisplayName("cập nhật tên, email và giới tính mới")
        void update_changesBasicInfo() {
            User user = createDefaultUser();

            UserName newName = new UserName("johnny_doe", "Johnny", "Doe");
            Email newEmail = new Email("johnny.doe@gmail.com");

            user.update(newName, newEmail, Gender.FEMALE,
                    LocalDate.of(1999, 5, 5),
                    new Bio("Senior Developer", "Now focusing on system design."),
                    new PhoneNumber("0987654321"),
                    new Address("456 Broadway", "New York", "USA"),
                    socialLinks);

            assertThat(user.getUsername()).isEqualTo(newName);
            assertThat(user.getEmail()).isEqualTo(newEmail);
            assertThat(user.getGender()).isEqualTo(Gender.FEMALE);
        }

        @Test
        @DisplayName("cập nhật ngày sinh mới")
        void update_changesDateOfBirth() {
            User user = createDefaultUser();

            LocalDate newDob = LocalDate.of(1995, 8, 20);
            user.update(userName, email, gender, newDob, bio, phoneNumber, address, socialLinks);

            assertThat(user.getDateOfBirth()).isEqualTo(newDob);
        }

        @Test
        @DisplayName("cập nhật địa chỉ mới")
        void update_changesAddress() {
            User user = createDefaultUser();

            Address newAddress = new Address("789 Elm Street", "Los Angeles", "USA");
            user.update(userName, email, gender, dateOfBirth, bio, phoneNumber, newAddress, socialLinks);

            assertThat(user.getAddress()).isEqualTo(newAddress);
        }
    }

    @Nested
    @DisplayName("changeAvatar()")
    class ChangeAvatar {

        @Test
        @DisplayName("cập nhật avatar mới")
        void changeAvatar_updatesAvatar() {
            User user = createDefaultUser();

            Avatar avatar = new Avatar(new byte[2 * 1024 * 1024]);
            user.changeAvatar(avatar);

            assertThat(user.getAvatar()).isEqualTo(avatar);
        }

        @Test
        @DisplayName("ném NullPointerException khi avatar là null")
        void changeAvatar_nullAvatar_throwsNullPointerException() {
            User user = createDefaultUser();

            assertThrows(NullPointerException.class, () -> user.changeAvatar(null));
        }
    }

    @Nested
    @DisplayName("changePassword()")
    class ChangePassword {

        @Test
        @DisplayName("cập nhật password mới")
        void changePassword_updatesPassword() {
            User user = createDefaultUser();

            Password newPassword = new Password("newPassword123");
            user.changePassword(newPassword);

            assertThat(user.getPassword()).isEqualTo(newPassword);
        }

        @Test
        @DisplayName("ném NullPointerException khi password là null")
        void changePassword_nullPassword_throwsNullPointerException() {
            User user = createDefaultUser();

            assertThrows(NullPointerException.class, () -> user.changePassword(null));
        }
    }

}