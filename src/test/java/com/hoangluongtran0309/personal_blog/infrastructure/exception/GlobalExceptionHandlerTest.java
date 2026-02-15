package com.hoangluongtran0309.personal_blog.infrastructure.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private MockHttpServletRequest request;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();

        request = new MockHttpServletRequest();
        request.setScheme("http");
        request.setServerName("localhost");
        request.setServerPort(8080);
        request.setRequestURI("/test-url");
    }

    @Test
    void handleNotFound_shouldReturn404View() {
        NotFoundException ex = mock(NotFoundException.class);
        when(ex.getMessage()).thenReturn("Resource not found");

        ModelAndView modelAndView = handler.handleNotFound(request, ex);

        assertThat(modelAndView.getViewName()).isEqualTo("error/404");
        assertThat(modelAndView.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(modelAndView.getModel().get("errorMessage"))
                .isEqualTo("Resource not found");
        assertThat(modelAndView.getModel().get("url").toString())
                .contains("/test-url");
    }

    @Test
    void handleValidation_shouldReturn400View() {
        ValidationException ex = mock(ValidationException.class);
        when(ex.getMessage()).thenReturn("Validation failed");

        ModelAndView modelAndView = handler.handleValidation(request, ex);

        assertThat(modelAndView.getViewName()).isEqualTo("error/400");
        assertThat(modelAndView.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(modelAndView.getModel().get("errorMessage"))
                .isEqualTo("Validation failed");
        assertThat(modelAndView.getModel().get("url").toString())
                .contains("/test-url");
    }

    @Test
    void handleBusiness_shouldReturn400View() {
        BusinessException ex = mock(BusinessException.class);
        when(ex.getMessage()).thenReturn("Business error");

        ModelAndView modelAndView = handler.handleBusiness(request, ex);

        assertThat(modelAndView.getViewName()).isEqualTo("error/400");
        assertThat(modelAndView.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(modelAndView.getModel().get("errorMessage"))
                .isEqualTo("Business error");
        assertThat(modelAndView.getModel().get("url").toString())
                .contains("/test-url");
    }

    @Test
    void handleSystem_shouldReturn500View() {
        Exception ex = mock(Exception.class);
        when(ex.getMessage()).thenReturn("Unexpected error");

        ModelAndView modelAndView = handler.handleSystem(request, ex);

        assertThat(modelAndView.getViewName()).isEqualTo("error/500");
        assertThat(modelAndView.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(modelAndView.getModel().get("errorMessage"))
                .isEqualTo("Unexpected error");
        assertThat(modelAndView.getModel().get("url").toString())
                .contains("/test-url");
    }

}
