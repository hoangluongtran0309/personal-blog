package com.hoangluongtran0309.personal_blog.category;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

    @Test
    void createCategory_ShouldFailure_WhenValueObjectsIsInvalid() {
        assertThrowsExactly(IllegalArgumentException.class, () -> {
            Category.create(new CategoryId(null), new CategoryName("New Category"), new CategorySlug("new-category"));
        }, "Category ID must not be null");
        assertThrowsExactly(IllegalArgumentException.class, () -> {
            Category.create(new CategoryId(UUID.randomUUID()), new CategoryName(""), new CategorySlug("new-category"));
        }, "Category name must not be blank");
        assertThrowsExactly(IllegalArgumentException.class, () -> {
            Category.create(new CategoryId(UUID.randomUUID()), new CategoryName("New Category"), new CategorySlug(""));
        }, "Category slug must not be blank");
        assertThrowsExactly(IllegalArgumentException.class, () -> {
            Category.create(new CategoryId(UUID.randomUUID()), new CategoryName("New Category"),
                    new CategorySlug("new slug"));
        }, "new slug is invalid slug");
    }

    @Test
    void addChildCategory_ShouldSuccess() {
        Category parentCategory = Category.create(new CategoryId(UUID.randomUUID()),
                new CategoryName("New Parent Category"),
                new CategorySlug("new-parent-category"));
        Category childCategory = Category.create(new CategoryId(UUID.randomUUID()),
                new CategoryName("New Child Category"),
                new CategorySlug("new-child-category"));
        parentCategory.addChild(childCategory);
        assertEquals("New Parent Category", childCategory.getParentCategory().getCategoryName().getName());
        assertFalse(parentCategory.getChildrenCategories().isEmpty());
    }

    @Test
    void addChildCategory_ShouldFailure_WhenChildCategoryIsInvalid() {
        Category parentCategory = Category.create(new CategoryId(UUID.randomUUID()),
                new CategoryName("New Parent Category"),
                new CategorySlug("new-parent-category"));
        assertThrowsExactly(IllegalArgumentException.class, () -> {
            parentCategory.addChild(null);
        }, "Child category must not be null");
        assertThrowsExactly(IllegalArgumentException.class, () -> {
            parentCategory.addChild(parentCategory);
        }, "Category cannot be child of itself");
    }

    @Test
    void removeChildCategory_ShouldSuccess() {
        Category parentCategory = Category.create(new CategoryId(UUID.randomUUID()),
                new CategoryName("New Parent Category"),
                new CategorySlug("new-parent-category"));
        Category childCategory = Category.create(new CategoryId(UUID.randomUUID()),
                new CategoryName("New Child Category"),
                new CategorySlug("new-child-category"));
        parentCategory.addChild(childCategory);
        assertFalse(parentCategory.getChildrenCategories().isEmpty());
        parentCategory.removeChild(childCategory);
        assertTrue(parentCategory.getChildrenCategories().isEmpty());
        assertNull(childCategory.getParentCategory());
    }

    @Test
    void removeChildCategory_ShouldFailure_WhenChildCategoryIsInvalid() {
        Category parentCategory = Category.create(new CategoryId(UUID.randomUUID()),
                new CategoryName("New Parent Category"),
                new CategorySlug("new-parent-category"));
        Category childCategory = Category.create(new CategoryId(UUID.randomUUID()),
                new CategoryName("New Child Category"),
                new CategorySlug("new-child-category"));
        assertThrowsExactly(IllegalArgumentException.class, () -> {
            parentCategory.removeChild(null);
        }, "Child category must not be null");
        assertThrowsExactly(IllegalArgumentException.class, () -> {
            parentCategory.removeChild(childCategory);
        }, "The given category is not a child of this category");
    }

}
