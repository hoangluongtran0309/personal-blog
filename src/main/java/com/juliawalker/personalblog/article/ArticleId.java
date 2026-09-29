package com.juliawalker.personalblog.article;

import java.util.Objects;
import java.util.UUID;

public final class ArticleId {

    private final UUID id;

    public ArticleId(UUID id) {
        this.id = Objects.requireNonNull(id, "id cannot be null");
    }

    public static ArticleId generate() {
        return new ArticleId(UUID.randomUUID());
    }

    public static ArticleId fromString(String value) {
        return new ArticleId(UUID.fromString(value));
    }

    public UUID value() {
        return id;
    }

    public String asString() {
        return id.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        return id.equals(((ArticleId) obj).id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "ArticleId{id=" + id + "}";
    }

}
