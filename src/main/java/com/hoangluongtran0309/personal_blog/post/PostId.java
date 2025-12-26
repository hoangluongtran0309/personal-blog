package com.hoangluongtran0309.personal_blog.post;

import java.util.UUID;

import org.springframework.util.Assert;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class PostId {

    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    protected PostId() {

    }

    public PostId(UUID id) {
        Assert.notNull(id, "Post ID must not be null");
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    public String asString() {
        return id.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
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
        PostId other = (PostId) obj;
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return id.toString();
    }

}
