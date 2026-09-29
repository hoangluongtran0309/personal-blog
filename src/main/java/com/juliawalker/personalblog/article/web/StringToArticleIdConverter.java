package com.juliawalker.personalblog.article.web;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.juliawalker.personalblog.article.ArticleId;

@Component
public class StringToArticleIdConverter implements Converter<String, ArticleId> {

    @Override
    public ArticleId convert(String source) {
        return ArticleId.fromString(source);
    }

}
