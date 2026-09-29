package com.juliawalker.personalblog.article;

import static com.juliawalker.personalblog.article.ArticleFixtures.NOW;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

class ArticleFileFormatTest {

    private final ArticleFileFormat fileFormat = new ArticleFileFormat();

    @Test
    void writeThenRead_fullArticle_returnsEqualArticle() {
        // Arrange
        Article article = new Article(ArticleId.generate(), new ArticleTitle("Xin chào: \"YAML\" #1"),
                new Slug("xin-chao-yaml-1"), new ArticleSummary("Tóm tắt: có dấu hai chấm"),
                new ArticleContent("# Title\n\n---\n\nText after a horizontal rule\n"),
                ArticleTerm.of("Lập trình"), List.of(ArticleTerm.of("spring"), ArticleTerm.of("C#")),
                NOW, NOW.minusDays(2), NOW.minusDays(1));

        // Act
        Article read = fileFormat.read(fileFormat.write(article));

        // Assert
        assertThat(read).isEqualTo(article);
        assertThat(read.getCategory().getName()).isEqualTo("Lập trình");
        assertThat(read.getTags()).extracting(ArticleTerm::getName).containsExactly("spring", "C#");
        assertThat(read.getContent().asString()).isEqualTo(article.getContent().asString());
    }

    @Test
    void writeThenRead_draftWithoutOptionalFields_returnsEqualArticle() {
        // Arrange
        Article article = new Article(ArticleId.generate(), new ArticleTitle("Draft"), new Slug("draft"), null,
                new ArticleContent("Body"), null, List.of(), null, NOW, NOW);

        // Act
        Article read = fileFormat.read(fileFormat.write(article));

        // Assert
        assertThat(read).isEqualTo(article);
        assertThat(read.isDraft()).isTrue();
        assertThat(read.getSummary()).isNull();
        assertThat(read.getCategory()).isNull();
    }

    @Test
    void write_article_startsWithFrontMatterAndEndsWithBody() {
        // Arrange
        Article article = ArticleFixtures.article("layout", NOW);

        // Act
        String text = fileFormat.write(article);

        // Assert
        assertThat(text).startsWith("---\nid: " + article.getId().asString() + "\n");
        assertThat(text).contains("\nslug: layout\n", "\n---\n# Heading");
        assertThat(text).endsWith("Body of layout");
    }

    @Test
    void read_handWrittenFileWithCrlfAndUnquotedTimestamps_parsesDates() {
        // Arrange
        String text = """
                ---
                id: 7f3c0b9e-1d2a-4c3b-9f8e-0a1b2c3d4e5f
                title: Hand written
                slug: hand-written
                tags: [java, spring]
                publishedAt: 2026-09-28T10:00:00
                createdAt: 2026-09-20T08:00
                updatedAt: 2026-09-28T10:00
                ---
                Body
                """.replace("\n", "\r\n");

        // Act
        Article article = fileFormat.read(text);

        // Assert
        assertThat(article.getPublishedAt()).isEqualTo(LocalDateTime.of(2026, 9, 28, 10, 0));
        assertThat(article.getCreatedAt()).isEqualTo(LocalDateTime.of(2026, 9, 20, 8, 0));
        assertThat(article.getTags()).extracting(ArticleTerm::getName).containsExactly("java", "spring");
        assertThat(article.getContent().asString()).isEqualTo("Body\n");
    }

    @Test
    void read_missingOpeningDelimiter_throwsIllegalArgumentException() {
        // Act + Assert
        assertThatThrownBy(() -> fileFormat.read("id: x\n---\nBody"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void read_missingClosingDelimiter_throwsIllegalArgumentException() {
        // Act + Assert
        assertThatThrownBy(() -> fileFormat.read("---\nid: x\nBody"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void read_missingRequiredField_throwsIllegalArgumentException() {
        // Arrange
        String text = "---\ntitle: No id\nslug: no-id\ncreatedAt: 2026-09-20T08:00\n"
                + "updatedAt: 2026-09-20T08:00\n---\nBody";

        // Act + Assert
        assertThatThrownBy(() -> fileFormat.read(text))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("id");
    }

    @Test
    void read_yamlTypeTag_isRejectedBySafeConstructor() {
        // Arrange
        String text = "---\nid: !!java.io.File [\"/tmp\"]\n---\nBody";

        // Act + Assert
        assertThatThrownBy(() -> fileFormat.read(text)).isInstanceOf(RuntimeException.class);
    }

    @Test
    void writeThenRead_withCoverImage_keepsCoverImage() {
        // Arrange
        Article article = ArticleFixtures.article("cover", NOW)
                .withCoverImage(new ArticleCoverImage("/images/blog-1.png"));

        // Act
        String text = fileFormat.write(article);
        Article read = fileFormat.read(text);

        // Assert
        assertThat(text).contains("\ncoverImage: /images/blog-1.png\n");
        assertThat(read).isEqualTo(article);
        assertThat(read.getCoverImage().asString()).isEqualTo("/images/blog-1.png");
    }

}
