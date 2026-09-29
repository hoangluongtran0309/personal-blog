package com.juliawalker.personalblog.article.web;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.format.annotation.DateTimeFormat;

import com.juliawalker.personalblog.article.Article;
import com.juliawalker.personalblog.article.ArticleContent;
import com.juliawalker.personalblog.article.ArticleCoverImage;
import com.juliawalker.personalblog.article.ArticleParameters;
import com.juliawalker.personalblog.article.ArticleSummary;
import com.juliawalker.personalblog.article.ArticleTerm;
import com.juliawalker.personalblog.article.ArticleTitle;
import com.juliawalker.personalblog.article.Slug;
import com.juliawalker.personalblog.infrastructure.validation.ValidationGroupOne;
import com.juliawalker.personalblog.infrastructure.validation.ValidationGroupTwo;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Backing object of the Add and Edit Article forms.
 */
public class ArticleFormData {

    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm";

    @NotBlank(message = "{validation.field.required}")
    @Size(max = ArticleTitle.MAX_LENGTH, groups = ValidationGroupOne.class, message = "{article.validation.title.maxLength}")
    private String title;

    @Size(max = Slug.MAX_LENGTH, groups = ValidationGroupOne.class, message = "{article.validation.slug.maxLength}")
    @Pattern(regexp = "^$|^[a-z0-9]+(?:(?:-|_)+[a-z0-9]+)*$", groups = ValidationGroupTwo.class,
            message = "{article.validation.slug.invalid}")
    private String slug;

    @Size(max = ArticleSummary.MAX_LENGTH, groups = ValidationGroupOne.class,
            message = "{article.validation.summary.maxLength}")
    private String summary;

    @NotBlank(message = "{validation.field.required}")
    private String content;

    // the datetime-local input submits "2026-09-28T10:00"; empty = draft
    @DateTimeFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime publishedAt;

    @Size(max = ArticleTerm.MAX_LENGTH, groups = ValidationGroupOne.class,
            message = "{article.validation.category.maxLength}")
    private String category;

    private String tags;

    private String coverImage;

    public static ArticleFormData fromArticle(Article article) {
        ArticleFormData formData = new ArticleFormData();
        formData.setTitle(article.getTitle().asString());
        formData.setSlug(article.getSlug().asString());
        formData.setSummary(article.getSummary() == null ? null : article.getSummary().asString());
        formData.setContent(article.getContent().asString());
        formData.setPublishedAt(article.getPublishedAt());
        formData.setCategory(article.getCategory() == null ? null : article.getCategory().getName());
        formData.setCoverImage(article.getCoverImage() == null ? null : article.getCoverImage().asString());
        formData.setTags(article.getTags().stream().map(ArticleTerm::getName).collect(Collectors.joining(", ")));
        return formData;
    }

    @AssertTrue(groups = ValidationGroupTwo.class, message = "{article.validation.category.invalid}")
    public boolean isCategoryValid() {
        return isBlank(category) || ArticleTerm.isValid(category);
    }

    @AssertTrue(groups = ValidationGroupTwo.class, message = "{article.validation.coverImage.invalid}")
    public boolean isCoverImageValid() {
        return isBlank(coverImage) || ArticleCoverImage.isValid(coverImage);
    }

    @AssertTrue(groups = ValidationGroupTwo.class, message = "{article.validation.tags.invalid}")
    public boolean isTagsValid() {
        return tagNames().stream().allMatch(ArticleTerm::isValid);
    }

    public ArticleParameters toParameters() {
        return new ArticleParameters(
                new ArticleTitle(title),
                isBlank(slug) ? null : new Slug(slug.strip()),
                isBlank(summary) ? null : new ArticleSummary(summary),
                new ArticleContent(content),
                isBlank(category) ? null : ArticleTerm.of(category),
                tagNames().stream().map(ArticleTerm::of).toList(),
                publishedAt,
                isBlank(coverImage) ? null : new ArticleCoverImage(coverImage));
    }

    private List<String> tagNames() {
        if (isBlank(tags)) {
            return List.of();
        }
        return Arrays.stream(tags.split(","))
                .map(String::strip)
                .filter(name -> !name.isEmpty())
                .toList();
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(LocalDateTime publishedAt) {
        this.publishedAt = publishedAt;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

}
