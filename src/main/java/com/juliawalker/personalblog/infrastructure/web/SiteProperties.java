package com.juliawalker.personalblog.infrastructure.web;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Site identity shown in layouts, the hero, the sidebar and the footer. Every value comes
 * from an environment variable ({@code SITE_*}, {@code SOCIAL_*}); blank values are hidden.
 */
@ConfigurationProperties("blog.site")
public record SiteProperties(
        String name,
        String description,
        String author,
        String authorRole,
        String tagline,
        String heroImage,
        String authorAvatar,
        String email,
        Social social) {

    public SiteProperties {
        social = social == null ? new Social(null, null, null, null) : social;
    }

    public boolean hasEmail() {
        return hasText(email);
    }

    public boolean hasContactLinks() {
        return hasEmail() || social.hasAny();
    }

    public record Social(String github, String facebook, String linkedin, String x) {

        public boolean hasAny() {
            return hasText(github) || hasText(facebook) || hasText(linkedin) || hasText(x);
        }

    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

}
