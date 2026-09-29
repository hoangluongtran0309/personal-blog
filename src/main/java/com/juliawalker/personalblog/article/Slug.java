package com.juliawalker.personalblog.article;

import java.text.Normalizer;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Pattern;

import org.springframework.util.Assert;

public final class Slug {

    public static final int MAX_LENGTH = 255;

    private static final Pattern VALID = Pattern.compile("^[a-z0-9]+(?:(?:-|_)+[a-z0-9]+)*$");
    private static final Pattern COMBINING_MARKS = Pattern.compile("\\p{M}+");
    private static final Pattern NON_ALPHANUMERIC = Pattern.compile("[^a-z0-9]+");

    private final String slug;

    public Slug(String slug) {
        Assert.hasText(slug, "slug cannot be blank");
        Assert.isTrue(slug.length() <= MAX_LENGTH, "slug cannot be longer than " + MAX_LENGTH);
        Assert.isTrue(VALID.matcher(slug).matches(), "slug has invalid format: " + slug);
        this.slug = slug;
    }

    public static boolean isValid(String value) {
        return value != null && value.length() <= MAX_LENGTH && VALID.matcher(value).matches();
    }

    // strip Vietnamese diacritics (including đ/Đ) so the slug is ASCII only and URL-safe
    public static Optional<Slug> fromText(String text) {
        if (text == null) {
            return Optional.empty();
        }
        String normalized = Normalizer.normalize(text.replace('đ', 'd').replace('Đ', 'D'), Normalizer.Form.NFD);
        String ascii = COMBINING_MARKS.matcher(normalized).replaceAll("").toLowerCase(Locale.ROOT);
        String candidate = NON_ALPHANUMERIC.matcher(ascii).replaceAll("-").replaceAll("^-+|-+$", "");
        if (candidate.length() > MAX_LENGTH) {
            candidate = candidate.substring(0, MAX_LENGTH).replaceAll("-+$", "");
        }
        return candidate.isEmpty() ? Optional.empty() : Optional.of(new Slug(candidate));
    }

    public String asString() {
        return slug;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        return slug.equals(((Slug) obj).slug);
    }

    @Override
    public int hashCode() {
        return slug.hashCode();
    }

    @Override
    public String toString() {
        return "Slug{slug=" + slug + "}";
    }

}
