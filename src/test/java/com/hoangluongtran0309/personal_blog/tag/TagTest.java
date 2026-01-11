package com.hoangluongtran0309.personal_blog.tag;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.UUID;

import org.junit.jupiter.api.Test;

public class TagTest {

    @Test
    void createTag_ShouldSuccess() {
        Tag tag = Tag.create(new TagId(UUID.randomUUID()), new TagName("New Tag"), new TagSlug("new-tag"));
        assertNotNull(tag);
        assertEquals("New Tag", tag.getTagName().getName());
        assertEquals("new-tag", tag.getTagSlug().getSlug());
    }

}
