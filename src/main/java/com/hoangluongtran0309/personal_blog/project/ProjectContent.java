package com.hoangluongtran0309.personal_blog.project;

import java.util.Objects;

import org.springframework.util.Assert;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class ProjectContent {

    @Column(name = "value", columnDefinition = "text")
    private String value;

    protected ProjectContent() {

    }

    public ProjectContent(String value) {
        Assert.hasText(value, "Project content must not be blank");
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 11 * hash + Objects.hashCode(this.value);
        return hash;
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
        final ProjectContent other = (ProjectContent) obj;
        return Objects.equals(this.value, other.value);
    }

    @Override
    public String toString() {
        return value;
    }

}
