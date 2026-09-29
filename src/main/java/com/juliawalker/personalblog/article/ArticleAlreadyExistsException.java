package com.juliawalker.personalblog.article;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.juliawalker.personalblog.infrastructure.exception.BusinessException;

@ResponseStatus(HttpStatus.CONFLICT)
public class ArticleAlreadyExistsException extends BusinessException {

    public ArticleAlreadyExistsException(Slug slug) {
        super("article.error.slugAlreadyExists", slug.asString());
    }

    public ArticleAlreadyExistsException(ArticleTitle title) {
        super("article.error.titleAlreadyExists", title.asString());
    }

}
