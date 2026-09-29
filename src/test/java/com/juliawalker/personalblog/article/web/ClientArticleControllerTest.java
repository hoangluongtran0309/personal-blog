package com.juliawalker.personalblog.article.web;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.juliawalker.personalblog.article.Article;
import com.juliawalker.personalblog.article.ArticleContent;
import com.juliawalker.personalblog.article.ArticleId;
import com.juliawalker.personalblog.article.ArticleNotFoundException;
import com.juliawalker.personalblog.article.ArticleService;
import com.juliawalker.personalblog.article.ArticleTerm;
import com.juliawalker.personalblog.article.ArticleTitle;
import com.juliawalker.personalblog.article.Slug;
import com.juliawalker.personalblog.infrastructure.markdown.MarkdownService;
import com.juliawalker.personalblog.infrastructure.security.SecurityConfiguration;

@WebMvcTest(ClientArticleController.class)
@Import({ SecurityConfiguration.class, MarkdownService.class })
class ClientArticleControllerTest {

    private static final LocalDateTime PUBLISHED_AT = LocalDateTime.of(2026, 9, 20, 9, 0);

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ArticleService articleService;

    @Test
    void home_publishedArticles_rendersListWithDate() throws Exception {
        // Arrange
        Article article = article("hello-world", "Hello world", "Body **bold**");
        when(articleService.getPublished(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(article), PageRequest.of(0, 6), 1));
        when(articleService.getPublishedCategories()).thenReturn(List.of(ArticleTerm.of("Java")));
        when(articleService.getPublishedTags()).thenReturn(List.of(ArticleTerm.of("spring")));

        // Act + Assert
        mockMvc.perform(get("/").param("lang", "en"))
                .andExpect(status().isOk())
                .andExpect(view().name("client/home"))
                .andExpect(content().string(containsString("Hello world")))
                .andExpect(content().string(containsString("href=\"/articles/hello-world\"")))
                .andExpect(content().string(containsString("Sep 20, 2026")))
                .andExpect(content().string(containsString("href=\"/category/java\"")));
    }

    @Test
    void home_negativePage_requestsFirstPage() throws Exception {
        // Arrange
        when(articleService.getPublished(PageRequest.of(0, ClientArticleController.PAGE_SIZE)))
                .thenReturn(new PageImpl<>(List.of()));

        // Act + Assert
        mockMvc.perform(get("/").param("page", "-3"))
                .andExpect(status().isOk());
    }

    @Test
    void category_knownCategory_filtersArticles() throws Exception {
        // Arrange
        when(articleService.getPublishedCategories()).thenReturn(List.of(ArticleTerm.of("Java")));
        when(articleService.getPublishedByCategory(new Slug("java"), PageRequest.of(0, 6)))
                .thenReturn(new PageImpl<>(List.of(article("a", "Java article", "x"))));

        // Act + Assert
        mockMvc.perform(get("/category/java"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("currentCategory", ArticleTerm.of("Java")))
                .andExpect(content().string(containsString("Java article")));
    }

    @Test
    void category_unknownCategory_returns404() throws Exception {
        // Arrange
        when(articleService.getPublishedCategories()).thenReturn(List.of());

        // Act + Assert
        mockMvc.perform(get("/category/unknown"))
                .andExpect(status().isNotFound());
    }

    @Test
    void tag_knownTag_filtersArticles() throws Exception {
        // Arrange
        when(articleService.getPublishedTags()).thenReturn(List.of(ArticleTerm.of("spring")));
        when(articleService.getPublishedByTag(new Slug("spring"), PageRequest.of(0, 6)))
                .thenReturn(new PageImpl<>(List.of()));

        // Act + Assert
        mockMvc.perform(get("/tag/spring"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("currentTag", ArticleTerm.of("spring")));
    }

    @Test
    void article_publishedArticle_rendersSanitizedMarkdown() throws Exception {
        // Arrange
        Article article = article("hello-world", "Hello world", "## Intro\n\nText <script>alert(1)</script>");
        when(articleService.getPublishedBySlug(new Slug("hello-world"))).thenReturn(article);

        // Act + Assert
        mockMvc.perform(get("/articles/hello-world"))
                .andExpect(status().isOk())
                .andExpect(view().name("client/articles/detail"))
                .andExpect(content().string(containsString("id=\"intro\"")))
                .andExpect(content().string(containsString("datetime=\"2026-09-20T09:00\"")))
                .andExpect(content().string(not(containsString("alert(1)"))));
    }

    @Test
    void article_draftOrMissing_returns404() throws Exception {
        // Arrange
        when(articleService.getPublishedBySlug(new Slug("draft")))
                .thenThrow(new ArticleNotFoundException(new Slug("draft")));

        // Act + Assert
        mockMvc.perform(get("/articles/draft"))
                .andExpect(status().isNotFound());
    }

    @Test
    void article_malformedSlug_returns404() throws Exception {
        // Act + Assert
        mockMvc.perform(get("/articles/Not A Slug"))
                .andExpect(status().isNotFound());
    }

    private static Article article(String slug, String title, String content) {
        return new Article(ArticleId.generate(), new ArticleTitle(title), new Slug(slug), null,
                new ArticleContent(content), ArticleTerm.of("Java"), List.of(ArticleTerm.of("spring")),
                PUBLISHED_AT, PUBLISHED_AT, PUBLISHED_AT);
    }

}
