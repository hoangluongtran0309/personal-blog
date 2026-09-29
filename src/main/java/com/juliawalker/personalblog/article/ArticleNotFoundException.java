package com.juliawalker.personalblog.article;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.juliawalker.personalblog.infrastructure.exception.NotFoundException;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ArticleNotFoundException extends NotFoundException {

    public ArticleNotFoundException(ArticleId articleId) {
        super("article.error.notFound.byId", articleId.asString());
    }

    public ArticleNotFoundException(Slug slug) {
        super("article.error.notFound.bySlug", slug.asString());
    }

}
