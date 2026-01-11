package com.hoangluongtran0309.personal_blog.tag;

import java.time.LocalDateTime;

public class Tag {

    private TagId tagId;

    private TagName tagName;

    private TagSlug tagSlug;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    protected Tag() {

    }

    public Tag(TagId tagId, TagName tagName, TagSlug tagSlug) {
        this.tagId = tagId;
        this.tagName = tagName;
        this.tagSlug = tagSlug;
    }

    public TagId getTagId() {
        return tagId;
    }

    public TagName getTagName() {
        return tagName;
    }

    public TagSlug getTagSlug() {
        return tagSlug;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

}
