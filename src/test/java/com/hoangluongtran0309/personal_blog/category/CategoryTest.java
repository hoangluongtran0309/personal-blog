package com.hoangluongtran0309.personal_blog.category;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CategoryTest {

    private CategoryId categoryId;

    @BeforeEach
    void setUp() {
        categoryId = new CategoryId(UUID.randomUUID());

    }

    @Test
    void testCreateCategorySuccessfully() {
        Category category = Category.createCategory(categoryId, new CategoryName("New Category"),
                new CategorySlug("new-category"));
        assertNotNull(category);
        assertEquals("New Category", category.getCategoryName().getName());
        assertEquals("new-category", category.getCategorySlug().getSlug());
    }

}
