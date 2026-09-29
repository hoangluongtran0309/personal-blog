package com.juliawalker.personalblog.article;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ArticleService {

    /** Published articles (publishedAt <= now), newest first. */
    Page<Article> getPublished(Pageable pageable);

    Page<Article> getPublishedByCategory(Slug categorySlug, Pageable pageable);

    Page<Article> getPublishedByTag(Slug tagSlug, Pageable pageable);

    /**
     * @throws ArticleNotFoundException when missing, a draft, or scheduled in the future
     */
    Article getPublishedBySlug(Slug slug);

    /** Distinct categories of published articles, sorted by name. */
    List<ArticleTerm> getPublishedCategories();

    /** Distinct tags of published articles, sorted by name. */
    List<ArticleTerm> getPublishedTags();

    /** Every article including drafts and scheduled ones, most recently updated first. */
    Page<Article> getAll(Pageable pageable);

    Optional<Article> getById(ArticleId articleId);

    /** Status of the article at the current time. */
    ArticleStatus getStatus(Article article);

    Article create(ArticleParameters parameters);

    Article update(ArticleId articleId, ArticleParameters parameters);

    void delete(ArticleId articleId);

}
