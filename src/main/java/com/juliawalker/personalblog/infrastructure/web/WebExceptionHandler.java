package com.juliawalker.personalblog.infrastructure.web;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.servlet.http.HttpServletResponse;

@ControllerAdvice
public class WebExceptionHandler {

    // a malformed id/slug in the URL means the page does not exist; sendError reuses the error/404 page
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public void handleTypeMismatch(HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.NOT_FOUND.value());
    }

}
