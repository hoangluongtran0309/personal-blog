package com.juliawalker.personalblog.article;

import org.springframework.util.Assert;

public final class ArticleSummary {

    public static final int MAX_LENGTH = 500;

    private final String summary;

    public ArticleSummary(String summary) {
        Assert.hasText(summary, "summary cannot be blank");
        String stripped = summary.strip();
        Assert.isTrue(stripped.length() <= MAX_LENGTH, "summary cannot be longer than " + MAX_LENGTH);
        this.summary = stripped;
    }

    public String asString() {
        return summary;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        return summary.equals(((ArticleSummary) obj).summary);
    }

    @Override
    public int hashCode() {
        return summary.hashCode();
    }

    @Override
    public String toString() {
        return "ArticleSummary{summary=" + summary + "}";
    }

}
