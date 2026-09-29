package com.juliawalker.personalblog.article;

import java.util.regex.Pattern;

import org.springframework.util.Assert;

/**
 * Optional cover image of an article: an absolute http(s) URL or a site-relative path
 * such as {@code /images/blog-1.png}. Other schemes (javascript:, data:, ...) are rejected
 * because the value is printed into {@code src} attributes.
 */
public final class ArticleCoverImage {

    public static final int MAX_LENGTH = 500;

    private static final Pattern VALID = Pattern.compile("^(https?://[^\\s\"'<>]+|/(?!/)[^\\s\"'<>]*)$",
            Pattern.CASE_INSENSITIVE);

    private final String url;

    public ArticleCoverImage(String url) {
        Assert.isTrue(isValid(url), "cover image must be an http(s) URL or a path starting with /");
        this.url = url.strip();
    }

    public static boolean isValid(String value) {
        if (value == null || value.isBlank()) {
            return false;
        }
        String stripped = value.strip();
        return stripped.length() <= MAX_LENGTH && VALID.matcher(stripped).matches();
    }

    public String asString() {
        return url;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        return url.equals(((ArticleCoverImage) obj).url);
    }

    @Override
    public int hashCode() {
        return url.hashCode();
    }

    @Override
    public String toString() {
        return "ArticleCoverImage{url=" + url + "}";
    }

}
