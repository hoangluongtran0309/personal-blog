package com.hoangluongtran0309.personal_blog.user.infrastructure.exception;

import com.hoangluongtran0309.personal_blog.infrastructure.exception.ValidationException;

public class UserAlreadyExistsException extends ValidationException {

    public UserAlreadyExistsException(String message) {
        super(message);
    }

}
