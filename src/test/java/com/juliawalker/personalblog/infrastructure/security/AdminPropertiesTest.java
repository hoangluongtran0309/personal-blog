package com.juliawalker.personalblog.infrastructure.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class AdminPropertiesTest {

    private static final String HASH = "$2a$10$.P/BUT5NLrCvVNnwxVzKFeMJUODthvtD9OPR0rZ9rPQD0RWWyoi2i";

    @Test
    void constructor_validValues_stripsUsername() {
        // Act
        AdminProperties properties = new AdminProperties(" admin ", HASH, null);

        // Assert
        assertThat(properties.username()).isEqualTo("admin");
        assertThat(properties.hasRememberMeKey()).isFalse();
    }

    @Test
    void constructor_missingUsername_failsFast() {
        // Act + Assert
        assertThatThrownBy(() -> new AdminProperties("", HASH, null))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("ADMIN_USERNAME");
    }

    @Test
    void constructor_plainPassword_failsFast() {
        // Act + Assert
        assertThatThrownBy(() -> new AdminProperties("admin", "secret", null))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("ADMIN_PASSWORD_HASH");
    }

}
