package com.juliawalker.personalblog.infrastructure.web;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
public class ResolveMessages {

    private final MessageSource messageSource;

    public ResolveMessages(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String resolve(String key, Object... args) {
        return messageSource.getMessage(key, args, LocaleContextHolder.getLocale());
    }

    public String resolve(Locale locale, String key, Object... args) {
        return messageSource.getMessage(key, args, locale);
    }

}