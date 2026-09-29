package com.juliawalker.personalblog.infrastructure.markdown;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MarkdownServiceTest {

    private MarkdownService markdownService;

    @BeforeEach
    void setUp() {
        markdownService = new MarkdownService();
    }

    @Test
    void toHtml_nullMarkdown_returnsEmptyString() {
        // Arrange

        // Act
        String result = markdownService.toHtml(null);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void toHtml_blankMarkdown_returnsEmptyString() {
        // Arrange

        // Act
        String result = markdownService.toHtml(" \n\t ");

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void toHtml_basicMarkdown_rendersHeadingAndEmphasis() {
        // Arrange
        String markdown = "# Title\n\nThis is **important**.";

        // Act
        String result = markdownService.toHtml(markdown);

        // Assert
        assertThat(result)
                .contains("<h1")
                .contains(">Title</a></h1>")
                .contains("<strong>important</strong>");
    }

    @Test
    void toHtml_softLineBreak_rendersConfiguredBreakElement() {
        // Arrange
        String markdown = "first line\nsecond line";

        // Act
        String result = markdownService.toHtml(markdown);

        // Assert
        assertThat(result).contains("first line<br>\nsecond line");
    }

    @Test
    void toHtml_tableMarkdown_rendersTableExtension() {
        // Arrange
        String markdown = "| Name | Value |\n| --- | --- |\n| Java | 17 |";

        // Act
        String result = markdownService.toHtml(markdown);

        // Assert
        assertThat(result)
                .contains("<table>")
                .contains("<th>Name</th>")
                .contains("<td>Java</td>");
    }

    @Test
    void toHtml_strikethroughMarkdown_rendersStrikethroughExtension() {
        // Arrange
        String markdown = "~~deprecated~~";

        // Act
        String result = markdownService.toHtml(markdown);

        // Assert
        assertThat(result).contains("<del>deprecated</del>");
    }

    @Test
    void toHtml_taskListMarkdown_rendersTaskListExtension() {
        // Arrange
        String markdown = "- [x] completed\n- [ ] pending";

        // Act
        String result = markdownService.toHtml(markdown);

        // Assert
        assertThat(result)
                .contains("type=\"checkbox\"")
                .contains("checked disabled")
                .contains("completed")
                .contains("pending");
    }

    @Test
    void toHtml_plainUrl_rendersAutolinkExtension() {
        // Arrange
        String markdown = "Visit https://example.com";

        // Act
        String result = markdownService.toHtml(markdown);

        // Assert
        assertThat(result).contains("<a href=\"https://example.com\">https://example.com</a>");
    }

    @Test
    void toHtml_heading_rendersAnchorLinkExtension() {
        // Arrange
        String markdown = "## Project Overview";

        // Act
        String result = markdownService.toHtml(markdown);

        // Assert
        assertThat(result)
                .contains("href=\"#project-overview\"")
                .contains("id=\"project-overview\"")
                .contains(">Project Overview</a>");
    }

    @Test
    void toHtml_scriptTag_isRemoved() {
        // Arrange
        String markdown = "Hello <script>alert('x')</script> world";

        // Act
        String result = markdownService.toHtml(markdown);

        // Assert
        assertThat(result).doesNotContain("<script").doesNotContain("alert");
    }

    @Test
    void toHtml_eventHandlerAttribute_isRemoved() {
        // Arrange
        String markdown = "<img src=\"https://example.com/a.png\" onerror=\"alert(1)\">";

        // Act
        String result = markdownService.toHtml(markdown);

        // Assert
        assertThat(result).contains("src=\"https://example.com/a.png\"").doesNotContain("onerror");
    }

    @Test
    void toHtml_javascriptLink_dropsHref() {
        // Arrange
        String markdown = "[click](javascript:alert(1))";

        // Act
        String result = markdownService.toHtml(markdown);

        // Assert
        assertThat(result).contains("click").doesNotContain("javascript:");
    }

    @Test
    void toHtml_relativeLink_isKept() {
        // Arrange
        String markdown = "[other](/articles/other-post)";

        // Act
        String result = markdownService.toHtml(markdown);

        // Assert
        assertThat(result).contains("<a href=\"/articles/other-post\">other</a>");
    }

    @Test
    void toHtml_fencedCode_keepsLanguageClass() {
        // Arrange
        String markdown = "```java\nint x = 1;\n```";

        // Act
        String result = markdownService.toHtml(markdown);

        // Assert
        assertThat(result).contains("<pre><code class=\"language-java\">int x = 1;");
    }

}
