package com.juliawalker.personalblog.article;

import java.util.List;
import java.util.Optional;

/**
 * Storage seam of the article feature. Implementations must be safe for concurrent use.
 */
public interface ArticleStore {

    ArticleId nextId();

    List<Article> findAll();

    Optional<Article> findById(ArticleId articleId);

    Optional<Article> findBySlug(Slug slug);

    /**
     * Creates or replaces the article with the same id.
     *
     * @throws ArticleAlreadyExistsException   when another article already uses the slug
     * @throws ArticleStorageException         when the file cannot be written
     */
    Article save(Article article);

    /**
     * @return {@code true} if an article was deleted, {@code false} if none existed
     */
    boolean delete(ArticleId articleId);

}
