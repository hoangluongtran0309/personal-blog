package com.hoangluongtran0309.personal_blog.category;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;

public class CategoryTest {

    @Test
    void createCategory_ShouldSuccess() {
        Category category = Category.create(new CategoryId(UUID.randomUUID()), new CategoryName("New Category"),
                new CategorySlug("new-category"));
        assertNotNull(category);
        assertEquals("New Category", category.getCategoryName().getName());
        assertEquals("new-category", category.getCategorySlug().getSlug());
    }

}
