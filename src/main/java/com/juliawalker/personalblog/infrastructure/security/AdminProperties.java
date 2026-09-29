package com.juliawalker.personalblog.infrastructure.security;

import java.util.regex.Pattern;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * The single admin account, taken from {@code ADMIN_USERNAME} and {@code ADMIN_PASSWORD_HASH}.
 */
@ConfigurationProperties("blog.admin")
public record AdminProperties(String username, String passwordHash, String rememberMeKey) {

    private static final Pattern BCRYPT = Pattern.compile("\\A\\$2[aby]?\\$\\d\\d\\$[./0-9A-Za-z]{53}");

    // missing or invalid admin configuration fails at startup instead of running with an empty account
    public AdminProperties {
        if (username == null || username.isBlank()) {
            throw new IllegalStateException("ADMIN_USERNAME must be set");
        }
        if (passwordHash == null || !BCRYPT.matcher(passwordHash).matches()) {
            throw new IllegalStateException("ADMIN_PASSWORD_HASH must be set to a BCrypt hash");
        }
        username = username.strip();
    }

    public boolean hasRememberMeKey() {
        return rememberMeKey != null && !rememberMeKey.isBlank();
    }

}
