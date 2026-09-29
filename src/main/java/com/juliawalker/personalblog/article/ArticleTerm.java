package com.juliawalker.personalblog.article;

import org.springframework.util.Assert;

/**
 * Category or tag of an article: a free-text name plus the slug used in URLs.
 */
public final class ArticleTerm {

    public static final int MAX_LENGTH = 50;

    private final String name;
    private final Slug slug;

    private ArticleTerm(String name, Slug slug) {
        this.name = name;
        this.slug = slug;
    }

    public static ArticleTerm of(String name) {
        Assert.hasText(name, "term name cannot be blank");
        String stripped = name.strip();
        Assert.isTrue(stripped.length() <= MAX_LENGTH, "term name cannot be longer than " + MAX_LENGTH);
        Slug slug = Slug.fromText(stripped)
                .orElseThrow(() -> new IllegalArgumentException("term name has no usable characters: " + name));
        return new ArticleTerm(stripped, slug);
    }

    public static boolean isValid(String name) {
        return name != null
                && !name.isBlank()
                && name.strip().length() <= MAX_LENGTH
                && Slug.fromText(name).isPresent();
    }

    public String getName() {
        return name;
    }

    public Slug getSlug() {
        return slug;
    }

    // two terms with the same slug are treated as one (e.g. "Java" and "java")
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        return slug.equals(((ArticleTerm) obj).slug);
    }

    @Override
    public int hashCode() {
        return slug.hashCode();
    }

    @Override
    public String toString() {
        return "ArticleTerm{name=" + name + ", slug=" + slug.asString() + "}";
    }

}
