package com.hoangluongtran0309.personal_blog.post;

import org.springframework.util.Assert;

import com.hoangluongtran0309.personal_blog.common.Constant;

public class PostSlug {

    private String slug;

    protected PostSlug() {

    }

    public PostSlug(String slug) {
        Assert.isTrue(slug.matches(Constant.SLUG_PATTERN), slug + " is invalid slug");
        Assert.hasText(slug, "Post slug must not be blank");
        this.slug = slug;
    }

    public String getSlug() {
        return slug;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((slug == null) ? 0 : slug.hashCode());
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
        PostSlug other = (PostSlug) obj;
        if (slug == null) {
            if (other.slug != null) {
                return false;
            }
        } else if (!slug.equals(other.slug)) {
            return false;
        }
        return true;
    }

}
