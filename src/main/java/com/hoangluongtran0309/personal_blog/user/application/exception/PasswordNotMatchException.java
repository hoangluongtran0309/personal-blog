package com.hoangluongtran0309.personal_blog.user.application.exception;

public class PasswordNotMatchException extends RuntimeException {

    public PasswordNotMatchException(String message) {
        super(message);
    }

}
