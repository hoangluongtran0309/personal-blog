package com.hoangluongtran0309.personal_blog.user.application.dto;

public record UserFilter(
        String search) {

    public boolean hasSearch() {
        return search != null && !search.isBlank();
    }

}
