package com.juliawalker.personalblog.article;

public enum ArticleStatus {

    /** No publication date. */
    DRAFT,

    /** Publication date in the future. */
    SCHEDULED,

    /** Publication date now or in the past. Only these articles are visible to guests. */
    PUBLISHED

}
