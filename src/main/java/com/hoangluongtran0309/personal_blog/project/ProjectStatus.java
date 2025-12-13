package com.hoangluongtran0309.personal_blog.project;

public enum ProjectStatus {

    DRAFT("Draft"),
    PUBLISHED("Published");

    private final String description;

    ProjectStatus(String description) {
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
