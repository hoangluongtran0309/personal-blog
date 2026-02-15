package com.hoangluongtran0309.personal_blog.infrastructure.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ModelAndView handleNotFound(HttpServletRequest request, NotFoundException ex) {
        return build("error/404", request, ex, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ValidationException.class)
    public ModelAndView handleValidation(HttpServletRequest request, ValidationException ex) {
        return build("error/400", request, ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BusinessException.class)
    public ModelAndView handleBusiness(HttpServletRequest request, BusinessException ex) {
        return build("error/400", request, ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleSystem(HttpServletRequest request, Exception ex) {
        log.error("Unhandled exception", ex);
        return build("error/500", request, ex, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ModelAndView build(String view,
            HttpServletRequest request,
            Exception ex,
            HttpStatus status) {

        ModelAndView modelAndView = new ModelAndView(view);

        modelAndView.setStatus(status);
        modelAndView.addObject("url", request.getRequestURL());
        modelAndView.addObject("errorMessage", ex.getMessage());

        return modelAndView;
    }

}
