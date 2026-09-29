package com.juliawalker.personalblog.article;

import java.time.LocalDateTime;
import java.util.List;

public class ArticleParameters {

    private final ArticleTitle title;
    private final Slug slug;
    private final ArticleSummary summary;
    private final ArticleContent content;
    private final ArticleTerm category;
    private final List<ArticleTerm> tags;
    private final LocalDateTime publishedAt;
    private final ArticleCoverImage coverImage;

    /**
     * @param slug        {@code null} to generate it from the title
     * @param summary     optional
     * @param category    optional
     * @param tags        optional
     * @param publishedAt {@code null} for a draft
     */
    public ArticleParameters(ArticleTitle title, Slug slug, ArticleSummary summary,
            ArticleContent content, ArticleTerm category, List<ArticleTerm> tags, LocalDateTime publishedAt) {
        this(title, slug, summary, content, category, tags, publishedAt, null);
    }

    /**
     * @param coverImage  optional
     */
    public ArticleParameters(ArticleTitle title, Slug slug, ArticleSummary summary,
            ArticleContent content, ArticleTerm category, List<ArticleTerm> tags, LocalDateTime publishedAt,
            ArticleCoverImage coverImage) {
        this.title = title;
        this.slug = slug;
        this.summary = summary;
        this.content = content;
        this.category = category;
        this.tags = tags == null ? List.of() : List.copyOf(tags);
        this.publishedAt = publishedAt;
        this.coverImage = coverImage;
    }

    public ArticleTitle getTitle() {
        return title;
    }

    public Slug getSlug() {
        return slug;
    }

    public ArticleSummary getSummary() {
        return summary;
    }

    public ArticleContent getContent() {
        return content;
    }

    public ArticleTerm getCategory() {
        return category;
    }

    public List<ArticleTerm> getTags() {
        return tags;
    }

    public ArticleCoverImage getCoverImage() {
        return coverImage;
    }

    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }

}
