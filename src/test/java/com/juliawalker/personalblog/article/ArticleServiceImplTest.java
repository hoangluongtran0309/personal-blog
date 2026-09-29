package com.juliawalker.personalblog.article;

import static com.juliawalker.personalblog.article.ArticleFixtures.NOW;
import static com.juliawalker.personalblog.article.ArticleFixtures.article;
import static com.juliawalker.personalblog.article.ArticleFixtures.createParameters;
import static com.juliawalker.personalblog.article.ArticleFixtures.updateParameters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class ArticleServiceImplTest {

    @Mock
    private ArticleStore articleStore;

    private ArticleServiceImpl articleService;

    @BeforeEach
    void setUp() {
        Clock clock = Clock.fixed(NOW.toInstant(ZoneOffset.UTC), ZoneOffset.UTC);
        articleService = new ArticleServiceImpl(articleStore, clock);
    }

    @Test
    void getPublished_mixedArticles_returnsOnlyPublishedNewestFirst() {
        // Arrange
        Article older = article("older", NOW.minusDays(2));
        Article newer = article("newer", NOW.minusDays(1));
        Article draft = article("draft", null);
        Article scheduled = article("scheduled", NOW.plusMinutes(1));
        when(articleStore.findAll()).thenReturn(List.of(older, draft, newer, scheduled));

        // Act
        Page<Article> page = articleService.getPublished(PageRequest.of(0, 10));

        // Assert
        assertThat(page.getContent()).containsExactly(newer, older);
        assertThat(page.getTotalElements()).isEqualTo(2);
    }

    @Test
    void getPublished_secondPage_returnsRemainingItems() {
        // Arrange
        Article first = article("first", NOW.minusDays(1));
        Article second = article("second", NOW.minusDays(2));
        Article third = article("third", NOW.minusDays(3));
        when(articleStore.findAll()).thenReturn(List.of(third, first, second));

        // Act
        Page<Article> page = articleService.getPublished(PageRequest.of(1, 2));

        // Assert
        assertThat(page.getContent()).containsExactly(third);
        assertThat(page.getTotalPages()).isEqualTo(2);
    }

    @Test
    void getPublished_pageBeyondEnd_returnsEmptyPage() {
        // Arrange
        when(articleStore.findAll()).thenReturn(List.of(article("only", NOW.minusDays(1))));

        // Act
        Page<Article> page = articleService.getPublished(PageRequest.of(5, 10));

        // Assert
        assertThat(page.getContent()).isEmpty();
        assertThat(page.getTotalElements()).isEqualTo(1);
    }

    @Test
    void getPublished_unpaged_returnsAll() {
        // Arrange
        when(articleStore.findAll()).thenReturn(List.of(article("a", NOW), article("b", NOW)));

        // Act
        Page<Article> page = articleService.getPublished(Pageable.unpaged());

        // Assert
        assertThat(page.getContent()).hasSize(2);
    }

    @Test
    void getPublishedByCategory_matchingSlug_excludesDraftsAndOtherCategories() {
        // Arrange
        Article java = article("java", NOW.minusDays(1));
        Article draftJava = article("draft-java", null);
        Article other = article("other", NOW.minusDays(1)).revise(new ArticleTitle("Other"), new Slug("other"),
                null, new ArticleContent("c"), ArticleTerm.of("Kotlin"), List.of(), NOW.minusDays(1), NOW);
        when(articleStore.findAll()).thenReturn(List.of(java, draftJava, other));

        // Act
        Page<Article> page = articleService.getPublishedByCategory(new Slug("java"), PageRequest.of(0, 10));

        // Assert
        assertThat(page.getContent()).containsExactly(java);
    }

    @Test
    void getPublishedByTag_matchingSlug_returnsPublishedWithTag() {
        // Arrange
        Article tagged = article("tagged", NOW.minusDays(1));
        Article untagged = article("untagged", NOW.minusDays(1)).revise(new ArticleTitle("U"), new Slug("u"),
                null, new ArticleContent("c"), null, List.of(), NOW.minusDays(1), NOW);
        when(articleStore.findAll()).thenReturn(List.of(tagged, untagged));

        // Act
        Page<Article> page = articleService.getPublishedByTag(new Slug("spring"), PageRequest.of(0, 10));

        // Assert
        assertThat(page.getContent()).containsExactly(tagged);
    }

    @Test
    void getPublishedBySlug_published_returnsArticle() {
        // Arrange
        Article article = article("published", NOW);
        when(articleStore.findBySlug(new Slug("published"))).thenReturn(Optional.of(article));

        // Act + Assert
        assertThat(articleService.getPublishedBySlug(new Slug("published"))).isEqualTo(article);
    }

    @Test
    void getPublishedBySlug_draft_throwsArticleNotFoundException() {
        // Arrange
        when(articleStore.findBySlug(new Slug("draft"))).thenReturn(Optional.of(article("draft", null)));

        // Act + Assert
        assertThatThrownBy(() -> articleService.getPublishedBySlug(new Slug("draft")))
                .isInstanceOf(ArticleNotFoundException.class);
    }

    @Test
    void getPublishedBySlug_scheduled_throwsArticleNotFoundException() {
        // Arrange
        when(articleStore.findBySlug(new Slug("later")))
                .thenReturn(Optional.of(article("later", NOW.plusDays(1))));

        // Act + Assert
        assertThatThrownBy(() -> articleService.getPublishedBySlug(new Slug("later")))
                .isInstanceOf(ArticleNotFoundException.class);
    }

    @Test
    void getPublishedBySlug_unknown_throwsArticleNotFoundException() {
        // Arrange
        when(articleStore.findBySlug(new Slug("missing"))).thenReturn(Optional.empty());

        // Act + Assert
        assertThatThrownBy(() -> articleService.getPublishedBySlug(new Slug("missing")))
                .isInstanceOf(ArticleNotFoundException.class);
    }

    @Test
    void getPublishedCategoriesAndTags_ignoreDraftsAndDeduplicateBySlug() {
        // Arrange
        Article published = article("published", NOW.minusDays(1));
        Article lowercaseJava = article("lower", NOW.minusDays(1)).revise(new ArticleTitle("L"), new Slug("l"),
                null, new ArticleContent("c"), ArticleTerm.of("java"), List.of(ArticleTerm.of("Api")),
                NOW.minusDays(1), NOW);
        Article draft = article("draft", null).revise(new ArticleTitle("D"), new Slug("d"), null,
                new ArticleContent("c"), ArticleTerm.of("Secret"), List.of(ArticleTerm.of("hidden")), null, NOW);
        when(articleStore.findAll()).thenReturn(List.of(published, lowercaseJava, draft));

        // Act
        List<ArticleTerm> categories = articleService.getPublishedCategories();
        List<ArticleTerm> tags = articleService.getPublishedTags();

        // Assert
        assertThat(categories).extracting(ArticleTerm::getName).containsExactly("Java");
        assertThat(tags).extracting(ArticleTerm::getName).containsExactly("Api", "spring", "thymeleaf");
    }

    @Test
    void getAll_includesDraftsAndScheduled_mostRecentlyUpdatedFirst() {
        // Arrange
        Article draft = article("draft", null);
        Article updatedLater = article("later", NOW.plusDays(1)).revise(new ArticleTitle("Later"),
                new Slug("later"), null, new ArticleContent("c"), null, List.of(), NOW.plusDays(1), NOW);
        when(articleStore.findAll()).thenReturn(List.of(draft, updatedLater));

        // Act
        Page<Article> page = articleService.getAll(PageRequest.of(0, 10));

        // Assert
        assertThat(page.getContent()).containsExactly(updatedLater, draft);
    }

    @Test
    void create_withoutSlug_generatesSlugFromTitleAndSetsTimestamps() {
        // Arrange
        ArticleId id = ArticleId.generate();
        when(articleStore.nextId()).thenReturn(id);
        when(articleStore.findAll()).thenReturn(List.of());
        when(articleStore.save(any(Article.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Article created = articleService.create(createParameters("Xin chào Spring", null));

        // Assert
        assertThat(created.getId()).isEqualTo(id);
        assertThat(created.getSlug().asString()).isEqualTo("xin-chao-spring");
        assertThat(created.getCreatedAt()).isEqualTo(NOW);
        assertThat(created.getUpdatedAt()).isEqualTo(NOW);
    }

    @Test
    void create_titleWithoutUsableCharacters_fallsBackToIdBasedSlug() {
        // Arrange
        ArticleId id = ArticleId.fromString("7f3c0b9e-1d2a-4c3b-9f8e-0a1b2c3d4e5f");
        when(articleStore.nextId()).thenReturn(id);
        when(articleStore.findAll()).thenReturn(List.of());
        when(articleStore.save(any(Article.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Article created = articleService.create(createParameters("???", null));

        // Assert
        assertThat(created.getSlug().asString()).isEqualTo("article-7f3c0b9e");
    }

    @Test
    void create_publishedAtWithSeconds_truncatesToMinutes() {
        // Arrange
        when(articleStore.nextId()).thenReturn(ArticleId.generate());
        when(articleStore.findAll()).thenReturn(List.of());
        when(articleStore.save(any(Article.class))).thenAnswer(invocation -> invocation.getArgument(0));
        ArticleParameters parameters = new ArticleParameters(new ArticleTitle("T"), null, null,
                new ArticleContent("c"), null, List.of(), LocalDateTime.of(2026, 9, 28, 9, 30, 45));

        // Act
        Article created = articleService.create(parameters);

        // Assert
        assertThat(created.getPublishedAt()).isEqualTo(LocalDateTime.of(2026, 9, 28, 9, 30));
    }

    @Test
    void create_duplicateSlug_throwsArticleAlreadyExistsException() {
        // Arrange
        when(articleStore.nextId()).thenReturn(ArticleId.generate());
        when(articleStore.findAll()).thenReturn(List.of(article("taken", NOW)));

        // Act + Assert
        assertThatThrownBy(() -> articleService.create(createParameters("Something new", "taken")))
                .isInstanceOf(ArticleAlreadyExistsException.class)
                .hasMessage("article.error.slugAlreadyExists");
        verify(articleStore, never()).save(any());
    }

    @Test
    void create_duplicateTitleIgnoringCase_throwsArticleAlreadyExistsException() {
        // Arrange
        when(articleStore.nextId()).thenReturn(ArticleId.generate());
        when(articleStore.findAll()).thenReturn(List.of(article("existing", NOW)));

        // Act + Assert
        assertThatThrownBy(() -> articleService.create(createParameters("TITLE OF EXISTING", "other-slug")))
                .isInstanceOf(ArticleAlreadyExistsException.class)
                .hasMessage("article.error.titleAlreadyExists");
    }

    @Test
    void update_publishedArticle_savesRevision() {
        // Arrange
        ArticleId id = ArticleId.generate();
        Article current = article(id, "current", NOW.minusDays(1));
        when(articleStore.findById(id)).thenReturn(Optional.of(current));
        when(articleStore.findAll()).thenReturn(List.of(current));
        when(articleStore.save(any(Article.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        articleService.update(id, updateParameters("Title of current", "current", NOW.minusDays(1)));

        // Assert
        ArgumentCaptor<Article> saved = ArgumentCaptor.forClass(Article.class);
        verify(articleStore).save(saved.capture());
        assertThat(saved.getValue().getContent().asString()).isEqualTo("Updated body");
        assertThat(saved.getValue().getSummary().asString()).isEqualTo("Updated summary");
        assertThat(saved.getValue().getCreatedAt()).isEqualTo(current.getCreatedAt());
        assertThat(saved.getValue().getUpdatedAt()).isEqualTo(NOW);
    }

    @Test
    void update_newCoverImage_replacesCoverImage() {
        // Arrange
        ArticleId id = ArticleId.generate();
        Article current = article(id, "current", NOW).withCoverImage(new ArticleCoverImage("/images/old.png"));
        when(articleStore.findById(id)).thenReturn(Optional.of(current));
        when(articleStore.findAll()).thenReturn(List.of(current));
        when(articleStore.save(any(Article.class))).thenAnswer(invocation -> invocation.getArgument(0));
        ArticleParameters parameters = new ArticleParameters(new ArticleTitle("Title of current"), new Slug("current"),
                null, new ArticleContent("c"), null, List.of(), NOW, new ArticleCoverImage("/images/new.png"));

        // Act
        Article updated = articleService.update(id, parameters);

        // Assert
        assertThat(updated.getCoverImage().asString()).isEqualTo("/images/new.png");
    }

    @Test
        void update_clearingPublishedAt_turnsArticleIntoDraft() {
        // Arrange
        ArticleId id = ArticleId.generate();
        Article current = article(id, "current", NOW.minusDays(1));
        when(articleStore.findById(id)).thenReturn(Optional.of(current));
        when(articleStore.findAll()).thenReturn(List.of(current));
        when(articleStore.save(any(Article.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Article updated = articleService.update(id, updateParameters("Title of current", "current", null));

        // Assert
        assertThat(updated.isDraft()).isTrue();
    }

    @Test
    void update_slugOfAnotherArticle_throwsArticleAlreadyExistsException() {
        // Arrange
        ArticleId id = ArticleId.generate();
        Article current = article(id, "current", NOW);
        when(articleStore.findById(id)).thenReturn(Optional.of(current));
        when(articleStore.findAll()).thenReturn(List.of(current, article("taken", NOW)));

        // Act + Assert
        assertThatThrownBy(() -> articleService.update(id, updateParameters("T", "taken", NOW)))
                .isInstanceOf(ArticleAlreadyExistsException.class);
    }

    @Test
    void update_unknownArticle_throwsArticleNotFoundException() {
        // Arrange
        ArticleId id = ArticleId.generate();
        when(articleStore.findById(id)).thenReturn(Optional.empty());

        // Act + Assert
        assertThatThrownBy(() -> articleService.update(id, updateParameters("T", "t", NOW)))
                .isInstanceOf(ArticleNotFoundException.class);
    }

    @Test
    void delete_existingArticle_delegatesToStore() {
        // Arrange
        ArticleId id = ArticleId.generate();
        when(articleStore.delete(id)).thenReturn(true);

        // Act
        articleService.delete(id);

        // Assert
        verify(articleStore).delete(id);
    }

    @Test
    void delete_unknownArticle_throwsArticleNotFoundException() {
        // Arrange
        ArticleId id = ArticleId.generate();
        when(articleStore.delete(id)).thenReturn(false);

        // Act + Assert
        assertThatThrownBy(() -> articleService.delete(id)).isInstanceOf(ArticleNotFoundException.class);
    }

}
