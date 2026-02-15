package com.hoangluongtran0309.personal_blog.infrastructure.exception;

public abstract class NotFoundException extends BusinessException {

    public NotFoundException(String message) {
        super(message);
    }

}
