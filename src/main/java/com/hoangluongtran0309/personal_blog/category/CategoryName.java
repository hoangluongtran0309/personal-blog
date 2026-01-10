package com.hoangluongtran0309.personal_blog.category;

import org.springframework.util.Assert;

public class CategoryName {

    private String name;

    protected CategoryName() {

    }

    public CategoryName(String name) {
        Assert.hasText(name, "Category name must not be blank");
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CategoryName other = (CategoryName) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

}
