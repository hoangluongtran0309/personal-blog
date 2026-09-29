package com.juliawalker.personalblog.article;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * An article stored as one Markdown file. Instances are immutable: every change
 * produces a new instance, so the in-memory index of {@link ArticleStore} can hand
 * out objects without defensive copies.
 */
public final class Article {

    public static final int EXCERPT_LENGTH = 200;
    public static final int WORDS_PER_MINUTE = 200;

    private static final Pattern MARKDOWN_SYNTAX = Pattern.compile("[#>*_`~\\[\\]()!|-]");
    private static final Pattern HTML_TAG = Pattern.compile("<[^>]*>");
    private static final Pattern WHITESPACE = Pattern.compile("\\s+");

    private final ArticleId id;
    private final ArticleTitle title;
    private final Slug slug;
    private final ArticleSummary summary;
    private final ArticleContent content;
    private final ArticleTerm category;
    private final List<ArticleTerm> tags;
    private final LocalDateTime publishedAt;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final ArticleCoverImage coverImage;

    public Article(ArticleId id, ArticleTitle title, Slug slug, ArticleSummary summary,
            ArticleContent content, ArticleTerm category, List<ArticleTerm> tags,
            LocalDateTime publishedAt, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this(id, title, slug, summary, content, category, tags, publishedAt, createdAt, updatedAt, null);
    }

    public Article(ArticleId id, ArticleTitle title, Slug slug, ArticleSummary summary,
            ArticleContent content, ArticleTerm category, List<ArticleTerm> tags,
            LocalDateTime publishedAt, LocalDateTime createdAt, LocalDateTime updatedAt,
            ArticleCoverImage coverImage) {
        this.id = Objects.requireNonNull(id, "id cannot be null");
        this.title = Objects.requireNonNull(title, "title cannot be null");
        this.slug = Objects.requireNonNull(slug, "slug cannot be null");
        this.summary = summary;
        this.content = Objects.requireNonNull(content, "content cannot be null");
        this.category = category;
        this.tags = tags == null ? List.of() : List.copyOf(tags.stream().distinct().toList());
        this.publishedAt = publishedAt;
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt cannot be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt cannot be null");
        this.coverImage = coverImage;
    }

    public Article revise(ArticleTitle newTitle, Slug newSlug, ArticleSummary newSummary,
            ArticleContent newContent, ArticleTerm newCategory, List<ArticleTerm> newTags,
            LocalDateTime newPublishedAt, LocalDateTime revisedAt) {
        return new Article(id, newTitle, newSlug, newSummary, newContent, newCategory, newTags,
                newPublishedAt, createdAt, revisedAt, coverImage);
    }

    public Article withCoverImage(ArticleCoverImage newCoverImage) {
        return new Article(id, title, slug, summary, content, category, tags,
                publishedAt, createdAt, updatedAt, newCoverImage);
    }

    public boolean isDraft() {
        return publishedAt == null;
    }

    public boolean isPublishedAt(LocalDateTime now) {
        return publishedAt != null && !publishedAt.isAfter(now);
    }

    public boolean isScheduledAt(LocalDateTime now) {
        return publishedAt != null && publishedAt.isAfter(now);
    }

    public ArticleStatus statusAt(LocalDateTime now) {
        if (isDraft()) {
            return ArticleStatus.DRAFT;
        }
        return isScheduledAt(now) ? ArticleStatus.SCHEDULED : ArticleStatus.PUBLISHED;
    }

    public boolean hasCategory(Slug categorySlug) {
        return category != null && category.getSlug().equals(categorySlug);
    }

    public boolean hasTag(Slug tagSlug) {
        return tags.stream().anyMatch(tag -> tag.getSlug().equals(tagSlug));
    }

    // the excerpt is not stored in the file; it is computed from the summary or content when displayed
    public String getExcerpt() {
        if (summary != null) {
            return summary.asString();
        }
        String plain = WHITESPACE.matcher(MARKDOWN_SYNTAX.matcher(HTML_TAG.matcher(content.asString()).replaceAll(" ")).replaceAll(" "))
                .replaceAll(" ")
                .strip();
        if (plain.length() <= EXCERPT_LENGTH) {
            return plain;
        }
        return plain.substring(0, EXCERPT_LENGTH).strip() + "…";
    }

    public int getReadingMinutes() {
        String text = content.asString().strip();
        int words = text.isEmpty() ? 0 : WHITESPACE.split(text).length;
        return Math.max(1, (int) Math.ceil(words / (double) WORDS_PER_MINUTE));
    }

    public ArticleId getId() {
        return id;
    }

    public ArticleTitle getTitle() {
        return title;
    }

    public Slug getSlug() {
        return slug;
    }

    public ArticleCoverImage getCoverImage() {
        return coverImage;
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

    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Article other = (Article) obj;
        return id.equals(other.id)
                && title.equals(other.title)
                && slug.equals(other.slug)
                && Objects.equals(summary, other.summary)
                && Objects.equals(coverImage, other.coverImage)
                && content.equals(other.content)
                && Objects.equals(category, other.category)
                && tags.equals(other.tags)
                && Objects.equals(publishedAt, other.publishedAt)
                && createdAt.equals(other.createdAt)
                && updatedAt.equals(other.updatedAt);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "Article{id=" + id.asString() + ", slug=" + slug.asString() + "}";
    }

}
