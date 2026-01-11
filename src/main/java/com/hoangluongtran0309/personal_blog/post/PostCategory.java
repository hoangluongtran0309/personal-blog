package com.hoangluongtran0309.personal_blog.post;

import com.hoangluongtran0309.personal_blog.category.CategoryId;

public class PostCategory {

    private CategoryId categoryId;

    protected PostCategory() {

    }

    public PostCategory(CategoryId categoryId) {
        this.categoryId = categoryId;
    }

    public CategoryId getCategoryId() {
        return categoryId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((categoryId == null) ? 0 : categoryId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        PostCategory other = (PostCategory) obj;
        if (categoryId == null) {
            if (other.categoryId != null) {
                return false;
            }
        } else if (!categoryId.equals(other.categoryId)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return categoryId.toString();
    }

}
