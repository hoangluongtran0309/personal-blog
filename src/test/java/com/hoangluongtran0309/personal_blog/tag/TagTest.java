package com.hoangluongtran0309.personal_blog.tag;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import org.junit.jupiter.api.Test;

public class TagTest {

    @Test
    void createTag_ShouldSuccess() {
        Tag tag = Tag.create(new TagId(UUID.randomUUID()), new TagName("New Tag"), new TagSlug("new-tag"));
        assertNotNull(tag);
        assertEquals("New Tag", tag.getTagName().getName());
        assertEquals("new-tag", tag.getTagSlug().getSlug());
    }

    @Test
    void createTag_ShouldFailure_WhenValueObjectsIsInvalid() {
        assertThrowsExactly(IllegalArgumentException.class, () -> {
            Tag.create(new TagId(null), new TagName("New Tag"), new TagSlug("new-Tag"));
        }, "Tag ID must not be null");
        assertThrowsExactly(IllegalArgumentException.class, () -> {
            Tag.create(new TagId(UUID.randomUUID()), new TagName(""), new TagSlug("new-tag"));
        }, "Tag name must not be blank");
        assertThrowsExactly(IllegalArgumentException.class, () -> {
            Tag.create(new TagId(UUID.randomUUID()), new TagName("New Tag"), new TagSlug(""));
        }, "Tag slug must not be blank");
        assertThrowsExactly(IllegalArgumentException.class, () -> {
            Tag.create(new TagId(UUID.randomUUID()), new TagName("New Tag"),
                    new TagSlug("new slug"));
        }, "new slug is invalid slug");
    }

    @Test
    void updateTag_ShouldSuccess() {
        Tag tag = Tag.create(new TagId(UUID.randomUUID()), new TagName("New Tag"), new TagSlug("new-tag"));
        tag.update(new TagName("New Updated Tag"), new TagSlug("new-updated-tag"));
        assertNotNull(tag);
        assertEquals("New Updated Tag", tag.getTagName().getName());
        assertEquals("new-updated-tag", tag.getTagSlug().getSlug());
    }

}
