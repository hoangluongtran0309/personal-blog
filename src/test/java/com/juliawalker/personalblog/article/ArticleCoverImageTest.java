package com.juliawalker.personalblog.article;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ArticleCoverImageTest {

    @ParameterizedTest
    @ValueSource(strings = { "/images/blog-1.png", "https://example.com/a.png", "http://cdn.example.com/x.jpg?w=800" })
    void isValid_pathOrHttpUrl_returnsTrue(String value) {
        // Act + Assert
        assertThat(ArticleCoverImage.isValid(value)).isTrue();
        assertThat(new ArticleCoverImage(" " + value + " ").asString()).isEqualTo(value);
    }

    @ParameterizedTest
    @ValueSource(strings = { "javascript:alert(1)", "data:image/png;base64,AAA", "//evil.com/a.png",
            "images/a.png", "/a b.png", "/a\"onerror=\"x.png", " " })
    void isValid_otherValues_returnsFalse(String value) {
        // Act + Assert
        assertThat(ArticleCoverImage.isValid(value)).isFalse();
        assertThatThrownBy(() -> new ArticleCoverImage(value)).isInstanceOf(IllegalArgumentException.class);
    }

}
