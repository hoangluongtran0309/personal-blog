package com.juliawalker.personalblog.article.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.juliawalker.personalblog.article.Article;
import com.juliawalker.personalblog.article.ArticleAlreadyExistsException;
import com.juliawalker.personalblog.article.ArticleContent;
import com.juliawalker.personalblog.article.ArticleId;
import com.juliawalker.personalblog.article.ArticleParameters;
import com.juliawalker.personalblog.article.ArticleService;
import com.juliawalker.personalblog.article.ArticleStatus;
import com.juliawalker.personalblog.article.ArticleTerm;
import com.juliawalker.personalblog.article.ArticleTitle;
import com.juliawalker.personalblog.article.Slug;
import com.juliawalker.personalblog.infrastructure.security.SecurityConfiguration;
import com.juliawalker.personalblog.infrastructure.web.ResolveMessages;

@WebMvcTest(AdminArticleController.class)
@Import({ SecurityConfiguration.class, ResolveMessages.class })
class AdminArticleControllerTest {

    private static final LocalDateTime NOW = LocalDateTime.of(2026, 9, 28, 10, 0);

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ArticleService articleService;

    @Test
    void dashboard_anonymous_redirectsToLogin() throws Exception {
        // Act + Assert
        mockMvc.perform(get("/admin"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void dashboard_userWithoutAdminRole_isForbidden() throws Exception {
        // Act + Assert
        mockMvc.perform(get("/admin"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void dashboard_admin_listsAllArticlesWithStatus() throws Exception {
        // Arrange
        Article draft = article("draft", "Draft article", null);
        Article published = article("live", "Live article", NOW.minusDays(1));
        when(articleService.getAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(draft, published)));
        when(articleService.getStatus(draft)).thenReturn(ArticleStatus.DRAFT);
        when(articleService.getStatus(published)).thenReturn(ArticleStatus.PUBLISHED);

        // Act + Assert
        mockMvc.perform(get("/admin").param("lang", "en"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/dashboard"))
                .andExpect(content().string(containsString("Draft article")))
                .andExpect(content().string(containsString(">Draft<")))
                .andExpect(content().string(containsString(">Published<")))
                .andExpect(content().string(containsString("/admin/articles/" + draft.getId().asString() + "/edit")))
                .andExpect(content().string(containsString("/admin/articles/" + draft.getId().asString() + "/delete")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createForm_admin_rendersEmptyForm() throws Exception {
        // Act + Assert
        mockMvc.perform(get("/admin/articles/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/articles/form"))
                .andExpect(content().string(containsString("name=\"title\"")))
                .andExpect(content().string(containsString("name=\"content\"")))
                .andExpect(content().string(containsString("type=\"datetime-local\"")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_validForm_createsArticleAndRedirects() throws Exception {
        // Arrange
        when(articleService.create(any())).thenAnswer(invocation -> article("my-post", "My post", NOW));

        // Act + Assert
        mockMvc.perform(post("/admin/articles/new").with(csrf())
                .param("title", "My post")
                .param("content", "# Hello")
                .param("publishedAt", "2026-09-28T10:00")
                .param("category", "Java")
                .param("tags", "spring, , thymeleaf")
                .param("coverImage", "/images/blog-1.png"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"))
                .andExpect(flash().attributeExists("successMessage"));

        ArgumentCaptor<ArticleParameters> captor = ArgumentCaptor.forClass(ArticleParameters.class);
        verify(articleService).create(captor.capture());
        ArticleParameters parameters = captor.getValue();
        assertThat(parameters.getTitle().asString()).isEqualTo("My post");
        assertThat(parameters.getSlug()).isNull();
        assertThat(parameters.getPublishedAt()).isEqualTo(NOW);
        assertThat(parameters.getCategory()).isEqualTo(ArticleTerm.of("Java"));
        assertThat(parameters.getCoverImage().asString()).isEqualTo("/images/blog-1.png");
        assertThat(parameters.getTags())
                .containsExactly(ArticleTerm.of("spring"), ArticleTerm.of("thymeleaf"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_emptyPublishedAt_createsDraft() throws Exception {
        // Arrange
        when(articleService.create(any())).thenAnswer(invocation -> article("d", "D", null));

        // Act
        mockMvc.perform(post("/admin/articles/new").with(csrf())
                .param("title", "D")
                .param("content", "x")
                .param("publishedAt", ""))
                .andExpect(status().is3xxRedirection());

        // Assert
        ArgumentCaptor<ArticleParameters> captor = ArgumentCaptor.forClass(ArticleParameters.class);
        verify(articleService).create(captor.capture());
        assertThat(captor.getValue().getPublishedAt()).isNull();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_missingTitleAndContent_showsFieldErrors() throws Exception {
        // Act + Assert
        mockMvc.perform(post("/admin/articles/new").with(csrf())
                .param("title", " ")
                .param("content", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/articles/form"))
                .andExpect(model().attributeHasFieldErrors("formData", "title", "content"));
        verify(articleService, never()).create(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_invalidSlugAndTags_showsFieldErrors() throws Exception {
        // Act + Assert
        mockMvc.perform(post("/admin/articles/new").with(csrf())
                .param("title", "T")
                .param("content", "c")
                .param("slug", "Not A Slug")
                .param("tags", "ok, ???"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("formData", "slug", "tagsValid"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_javascriptCoverImage_showsFieldError() throws Exception {
        // Act + Assert
        mockMvc.perform(post("/admin/articles/new").with(csrf())
                .param("title", "T")
                .param("content", "c")
                .param("coverImage", "javascript:alert(1)"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("formData", "coverImageValid"));
        verify(articleService, never()).create(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_invalidDate_showsFieldError() throws Exception {
        // Act + Assert
        mockMvc.perform(post("/admin/articles/new").with(csrf())
                .param("title", "T")
                .param("content", "c")
                .param("publishedAt", "yesterday"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("formData", "publishedAt"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_duplicateSlug_showsErrorOnSlugField() throws Exception {
        // Arrange
        when(articleService.create(any())).thenThrow(new ArticleAlreadyExistsException(new Slug("taken")));

        // Act + Assert
        mockMvc.perform(post("/admin/articles/new").with(csrf())
                .param("title", "T")
                .param("content", "c")
                .param("slug", "taken"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrorCode("formData", "slug", "article.error.slugAlreadyExists"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_withoutCsrf_isForbidden() throws Exception {
        // Act + Assert
        mockMvc.perform(post("/admin/articles/new").param("title", "T").param("content", "c"))
                .andExpect(status().isForbidden());
        verify(articleService, never()).create(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void editForm_existingArticle_prefillsForm() throws Exception {
        // Arrange
        Article article = article("my-post", "My post", NOW);
        when(articleService.getById(article.getId())).thenReturn(Optional.of(article));
        when(articleService.getStatus(article)).thenReturn(ArticleStatus.PUBLISHED);

        // Act + Assert
        mockMvc.perform(get("/admin/articles/{id}/edit", article.getId().asString()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("value=\"My post\"")))
                .andExpect(content().string(containsString("value=\"2026-09-28T10:00\"")))
                .andExpect(content().string(containsString("value=\"spring, thymeleaf\"")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void edit_validForm_updatesArticle() throws Exception {
        // Arrange
        Article article = article("my-post", "My post", NOW);
        when(articleService.getById(article.getId())).thenReturn(Optional.of(article));
        when(articleService.update(eq(article.getId()), any())).thenReturn(article);

        // Act + Assert
        mockMvc.perform(post("/admin/articles/{id}/edit", article.getId().asString()).with(csrf())
                .param("title", "My post")
                .param("slug", "my-post")
                .param("content", "Updated"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"));
        verify(articleService).update(eq(article.getId()), any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void editForm_unknownArticle_returns404() throws Exception {
        // Arrange
        ArticleId id = ArticleId.generate();
        when(articleService.getById(id)).thenReturn(Optional.empty());

        // Act + Assert
        mockMvc.perform(get("/admin/articles/{id}/edit", id.asString()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void editForm_malformedId_returns404() throws Exception {
        // Act + Assert
        mockMvc.perform(get("/admin/articles/not-a-uuid/edit"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteConfirmation_existingArticle_rendersConfirmationPage() throws Exception {
        // Arrange
        Article article = article("my-post", "My post", NOW);
        when(articleService.getById(article.getId())).thenReturn(Optional.of(article));
        when(articleService.getStatus(article)).thenReturn(ArticleStatus.PUBLISHED);

        // Act + Assert
        mockMvc.perform(get("/admin/articles/{id}/delete", article.getId().asString()))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/articles/delete"))
                .andExpect(content().string(containsString("My post")));
        verify(articleService, never()).delete(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_existingArticle_deletesAndRedirects() throws Exception {
        // Arrange
        Article article = article("my-post", "My post", NOW);
        when(articleService.getById(article.getId())).thenReturn(Optional.of(article));

        // Act + Assert
        mockMvc.perform(post("/admin/articles/{id}/delete", article.getId().asString()).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"))
                .andExpect(flash().attributeExists("successMessage"));
        verify(articleService).delete(article.getId());
    }

    private static Article article(String slug, String title, LocalDateTime publishedAt) {
        return new Article(ArticleId.generate(), new ArticleTitle(title), new Slug(slug), null,
                new ArticleContent("Body"), ArticleTerm.of("Java"),
                List.of(ArticleTerm.of("spring"), ArticleTerm.of("thymeleaf")), publishedAt, NOW, NOW);
    }

}
