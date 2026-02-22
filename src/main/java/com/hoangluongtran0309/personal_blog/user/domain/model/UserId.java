package com.hoangluongtran0309.personal_blog.user.domain.model;

import java.util.UUID;

public class UserId {

    private UUID value;

    protected UserId() {

    }

    public UserId(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException();
        }

        this.value = value;
    }

    public UUID getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((value == null) ? 0 : value.hashCode());
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
        UserId other = (UserId) obj;
        if (value == null) {
            if (other.value != null)
                return false;
        } else if (!value.equals(other.value))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return value.toString();
    }

}
