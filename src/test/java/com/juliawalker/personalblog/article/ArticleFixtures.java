package com.juliawalker.personalblog.article;

import java.time.LocalDateTime;
import java.util.List;

final class ArticleFixtures {

    static final LocalDateTime NOW = LocalDateTime.of(2026, 9, 28, 10, 0);

    private ArticleFixtures() {
    }

    static Article article(String slug, LocalDateTime publishedAt) {
        return article(ArticleId.generate(), slug, publishedAt);
    }

    static Article article(ArticleId id, String slug, LocalDateTime publishedAt) {
        return new Article(
                id,
                new ArticleTitle("Title of " + slug),
                new Slug(slug),
                null,
                new ArticleContent("# Heading\n\nBody of " + slug),
                ArticleTerm.of("Java"),
                List.of(ArticleTerm.of("spring"), ArticleTerm.of("thymeleaf")),
                publishedAt,
                NOW.minusDays(10),
                NOW.minusDays(1));
    }

    static ArticleParameters createParameters(String title, String slug) {
        return new ArticleParameters(
                new ArticleTitle(title),
                slug == null ? null : new Slug(slug),
                null,
                new ArticleContent("Body"),
                ArticleTerm.of("Java"),
                List.of(ArticleTerm.of("spring")),
                NOW.minusHours(1));
    }

    static ArticleParameters updateParameters(String title, String slug, LocalDateTime publishedAt) {
        return new ArticleParameters(
                new ArticleTitle(title),
                slug == null ? null : new Slug(slug),
                new ArticleSummary("Updated summary"),
                new ArticleContent("Updated body"),
                null,
                List.of(),
                publishedAt);
    }

}
