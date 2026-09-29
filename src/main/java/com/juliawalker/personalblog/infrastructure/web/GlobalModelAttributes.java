package com.juliawalker.personalblog.infrastructure.web;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Model attributes every page layout needs.
 */
@ControllerAdvice
public class GlobalModelAttributes {

    private final SiteProperties siteProperties;

    public GlobalModelAttributes(SiteProperties siteProperties) {
        this.siteProperties = siteProperties;
    }

    @ModelAttribute("site")
    public SiteProperties site() {
        return siteProperties;
    }

    // Thymeleaf 3.1 no longer has #request; layouts use this path for the language switch links.
    // Error pages are forwarded to /error, so recover the original request path.
    @ModelAttribute("currentPath")
    public String currentPath(HttpServletRequest request) {
        Object errorUri = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
        String uri = errorUri instanceof String original ? original : request.getRequestURI();
        return uri.substring(Math.min(request.getContextPath().length(), uri.length()));
    }

}
