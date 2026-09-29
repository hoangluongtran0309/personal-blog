package com.juliawalker.personalblog.article;

import org.springframework.util.Assert;

public final class ArticleContent {

    private final String content;

    public ArticleContent(String content) {
        Assert.hasText(content, "content cannot be blank");
        this.content = content;
    }

    public String asString() {
        return content;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        return content.equals(((ArticleContent) obj).content);
    }

    @Override
    public int hashCode() {
        return content.hashCode();
    }

    @Override
    public String toString() {
        return "ArticleContent{length=" + content.length() + "}";
    }

}
