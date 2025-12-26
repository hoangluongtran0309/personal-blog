package com.hoangluongtran0309.personal_blog.post;

public enum PostStatus {

    DRAFT("Draft"),
    PUBLISHED("Published");

    private final String description;

    PostStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return description;
    }

}
