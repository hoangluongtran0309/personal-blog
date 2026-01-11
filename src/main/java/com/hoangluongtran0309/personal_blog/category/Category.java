package com.hoangluongtran0309.personal_blog.category;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class Category {

    private CategoryId categoryId;

    private CategoryName categoryName;

    private CategorySlug categorySlug;

    private Category parentCategory;

    private Set<Category> childrenCategories;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    protected Category() {

    }

    public Category(CategoryId categoryId, CategoryName categoryName, CategorySlug categorySlug) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.categorySlug = categorySlug;
        this.childrenCategories = new HashSet<>();
    }

    public CategoryId getCategoryId() {
        return categoryId;
    }

    public CategoryName getCategoryName() {
        return categoryName;
    }

    public CategorySlug getCategorySlug() {
        return categorySlug;
    }

    public Category getParentCategory() {
        return parentCategory;
    }

    public Set<Category> getChildrenCategories() {
        return childrenCategories;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public static Category create(CategoryId categoryId, CategoryName categoryName, CategorySlug categorySlug) {
        return new Category(categoryId, categoryName, categorySlug);
    }

    public void addChild(Category childCategory) {
        if (childCategory == null) {
            throw new IllegalArgumentException("Child category must not be null");
        }
        if (this == childCategory) {
            throw new IllegalArgumentException("Category cannot be child of itself");
        }
        childCategory.parentCategory = this;
        this.childrenCategories.add(childCategory);
    }

    public void removeChild(Category childCategory) {
        this.childrenCategories.remove(childCategory);
    }

}
