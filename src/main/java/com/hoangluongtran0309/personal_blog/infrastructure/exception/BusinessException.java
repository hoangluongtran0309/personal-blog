package com.hoangluongtran0309.personal_blog.infrastructure.exception;

public abstract class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }

}
