package com.juliawalker.personalblog.article;

import java.util.Locale;

import org.springframework.util.Assert;

public final class ArticleTitle {

    public static final int MAX_LENGTH = 255;

    private final String title;

    public ArticleTitle(String title) {
        Assert.hasText(title, "title cannot be blank");
        String stripped = title.strip();
        Assert.isTrue(stripped.length() <= MAX_LENGTH, "title cannot be longer than " + MAX_LENGTH);
        this.title = stripped;
    }

    public String asString() {
        return title;
    }

    // case-insensitive comparison so "Hello" and "hello" cannot both exist
    public boolean sameAs(ArticleTitle other) {
        return other != null && title.toLowerCase(Locale.ROOT).equals(other.title.toLowerCase(Locale.ROOT));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        return title.equals(((ArticleTitle) obj).title);
    }

    @Override
    public int hashCode() {
        return title.hashCode();
    }

    @Override
    public String toString() {
        return "ArticleTitle{title=" + title + "}";
    }

}
