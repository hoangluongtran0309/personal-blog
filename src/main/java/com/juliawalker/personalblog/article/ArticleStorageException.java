package com.juliawalker.personalblog.article;

/**
 * Filesystem failure while reading or writing article files. Not user-correctable.
 */
public class ArticleStorageException extends RuntimeException {

    public ArticleStorageException(String message, Throwable cause) {
        super(message, cause);
    }

}
