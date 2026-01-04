package com.hoangluongtran0309.personal_blog.category;

import java.time.LocalDateTime;

public class Category {

    private CategoryId categoryId;

    private CategoryName categoryName;

    private CategorySlug categorySlug;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    protected Category() {

    }

    public CategoryId getCategoryId() {
        return categoryId;
    }

    public CategoryName getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(CategoryName categoryName) {
        this.categoryName = categoryName;
    }

    public CategorySlug getCategorySlug() {
        return categorySlug;
    }

    public void setCategorySlug(CategorySlug categorySlug) {
        this.categorySlug = categorySlug;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

}
