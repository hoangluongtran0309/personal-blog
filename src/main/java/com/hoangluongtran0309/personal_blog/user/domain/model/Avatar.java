package com.hoangluongtran0309.personal_blog.user.domain.model;

import java.util.Arrays;

public class Avatar {

    private byte[] value;

    protected Avatar() {

    }

    public Avatar(byte[] value) {
        this.value = value;
    }

    public byte[] getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(value);
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
        Avatar other = (Avatar) obj;
        return Arrays.equals(value, other.value);
    }

}
