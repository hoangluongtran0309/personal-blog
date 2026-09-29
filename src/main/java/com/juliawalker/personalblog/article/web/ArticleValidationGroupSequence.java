package com.juliawalker.personalblog.article.web;

import com.juliawalker.personalblog.infrastructure.validation.ValidationGroupOne;
import com.juliawalker.personalblog.infrastructure.validation.ValidationGroupTwo;

import jakarta.validation.GroupSequence;
import jakarta.validation.groups.Default;

@GroupSequence({ Default.class, ValidationGroupOne.class, ValidationGroupTwo.class })
public interface ArticleValidationGroupSequence {

}
