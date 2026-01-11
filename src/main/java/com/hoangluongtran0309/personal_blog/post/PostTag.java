package com.hoangluongtran0309.personal_blog.post;

import com.hoangluongtran0309.personal_blog.tag.TagId;

public class PostTag {

    private TagId tagId;

    protected PostTag() {

    }

    public PostTag(TagId tagId) {
        this.tagId = tagId;
    }

    public TagId getTagId() {
        return tagId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((tagId == null) ? 0 : tagId.hashCode());
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
        PostTag other = (PostTag) obj;
        if (tagId == null) {
            if (other.tagId != null) {
                return false;
            }
        } else if (!tagId.equals(other.tagId)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return tagId.toString();
    }

}
