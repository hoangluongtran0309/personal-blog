package com.hoangluongtran0309.personal_blog.user.infrastructure.exception;

import com.hoangluongtran0309.personal_blog.infrastructure.exception.NotFoundException;

public class UserNotFoundException extends NotFoundException {

    public UserNotFoundException(String message) {
        super(message);
    }

}
