package com.juliawalker.personalblog.article.web;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.juliawalker.personalblog.article.ArticleTerm;

class TopicIconsTest {

    @Test
    void of_knownTopics_returnsMatchingIcon() {
        // Act + Assert
        assertThat(TopicIcons.of(ArticleTerm.of("Database"))).isEqualTo("server-outline");
        assertThat(TopicIcons.of(ArticleTerm.of("Web Performance"))).isEqualTo("rocket-outline");
        assertThat(TopicIcons.of(ArticleTerm.of("Accessibility"))).isEqualTo("accessibility-outline");
        assertThat(TopicIcons.of(ArticleTerm.of("Java"))).isEqualTo("cafe-outline");
    }

    @Test
    void of_unknownTopic_returnsSameIconEveryTime() {
        // Act + Assert
        assertThat(TopicIcons.of(ArticleTerm.of("Gardening"))).isEqualTo(TopicIcons.of(ArticleTerm.of("gardening")));
        assertThat(TopicIcons.of(null)).isEqualTo("folder-outline");
    }

}
