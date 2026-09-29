package com.juliawalker.personalblog.article;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Article business rules. Sorting is fixed per use case: the sort of the given
 * {@link Pageable} is ignored, only its page number and size are used.
 */
@Service
public class ArticleServiceImpl implements ArticleService {

    private static final Logger logger = LoggerFactory.getLogger(ArticleServiceImpl.class);

    private static final Comparator<Article> NEWEST_PUBLISHED_FIRST = Comparator
            .comparing(Article::getPublishedAt, Comparator.reverseOrder())
            .thenComparing(article -> article.getTitle().asString(), String.CASE_INSENSITIVE_ORDER);

    private static final Comparator<Article> RECENTLY_UPDATED_FIRST = Comparator
            .comparing(Article::getUpdatedAt, Comparator.reverseOrder())
            .thenComparing(article -> article.getTitle().asString(), String.CASE_INSENSITIVE_ORDER);

    private static final Comparator<ArticleTerm> BY_NAME = Comparator
            .comparing(term -> term.getName().toLowerCase(Locale.ROOT));

    private final ArticleStore articleStore;
    private final Clock clock;

    public ArticleServiceImpl(ArticleStore articleStore, Clock clock) {
        this.articleStore = articleStore;
        this.clock = clock;
    }

    @Override
    public Page<Article> getPublished(Pageable pageable) {
        return toPage(published().sorted(NEWEST_PUBLISHED_FIRST).toList(), pageable);
    }

    @Override
    public Page<Article> getPublishedByCategory(Slug categorySlug, Pageable pageable) {
        return toPage(published()
                .filter(article -> article.hasCategory(categorySlug))
                .sorted(NEWEST_PUBLISHED_FIRST)
                .toList(), pageable);
    }

    @Override
    public Page<Article> getPublishedByTag(Slug tagSlug, Pageable pageable) {
        return toPage(published()
                .filter(article -> article.hasTag(tagSlug))
                .sorted(NEWEST_PUBLISHED_FIRST)
                .toList(), pageable);
    }

    @Override
    public Article getPublishedBySlug(Slug slug) {
        LocalDateTime now = now();
        // drafts/scheduled articles return 404 instead of 403 so their existence is not revealed
        return articleStore.findBySlug(slug)
                .filter(article -> article.isPublishedAt(now))
                .orElseThrow(() -> new ArticleNotFoundException(slug));
    }

    @Override
    public List<ArticleTerm> getPublishedCategories() {
        return distinctSorted(published()
                .map(Article::getCategory)
                .filter(category -> category != null)
                .toList());
    }

    @Override
    public List<ArticleTerm> getPublishedTags() {
        return distinctSorted(published()
                .flatMap(article -> article.getTags().stream())
                .toList());
    }

    @Override
    public Page<Article> getAll(Pageable pageable) {
        return toPage(articleStore.findAll().stream().sorted(RECENTLY_UPDATED_FIRST).toList(), pageable);
    }

    @Override
    public Optional<Article> getById(ArticleId articleId) {
        return articleStore.findById(articleId);
    }

    @Override
    public ArticleStatus getStatus(Article article) {
        return article.statusAt(now());
    }

    @Override
    public Article create(ArticleParameters parameters) {
        ArticleId articleId = articleStore.nextId();
        Slug slug = resolveSlug(parameters, articleId);
        logger.info("Creating article with slug: {}", slug.asString());
        ensureUnique(articleId, slug, parameters.getTitle());
        LocalDateTime now = now();
        Article article = new Article(
                articleId,
                parameters.getTitle(),
                slug,
                parameters.getSummary(),
                parameters.getContent(),
                parameters.getCategory(),
                parameters.getTags(),
                truncate(parameters.getPublishedAt()),
                now,
                now,
                parameters.getCoverImage());
        return articleStore.save(article);
    }

    @Override
    public Article update(ArticleId articleId, ArticleParameters parameters) {
        logger.info("Updating article with ID: {}", articleId.asString());
        Article current = articleStore.findById(articleId)
                .orElseThrow(() -> new ArticleNotFoundException(articleId));
        Slug slug = resolveSlug(parameters, articleId);
        ensureUnique(articleId, slug, parameters.getTitle());
        Article revised = current.revise(
                parameters.getTitle(),
                slug,
                parameters.getSummary(),
                parameters.getContent(),
                parameters.getCategory(),
                parameters.getTags(),
                truncate(parameters.getPublishedAt()),
                now()).withCoverImage(parameters.getCoverImage());
        return articleStore.save(revised);
    }

    @Override
    public void delete(ArticleId articleId) {
        logger.info("Deleting article with ID: {}", articleId.asString());
        if (!articleStore.delete(articleId)) {
            throw new ArticleNotFoundException(articleId);
        }
    }

    private Stream<Article> published() {
        LocalDateTime now = now();
        return articleStore.findAll().stream().filter(article -> article.isPublishedAt(now));
    }

    // empty slug -> generate from title; title has no usable characters -> use the first 8 characters of the id
    private static Slug resolveSlug(ArticleParameters parameters, ArticleId articleId) {
        if (parameters.getSlug() != null) {
            return parameters.getSlug();
        }
        return Slug.fromText(parameters.getTitle().asString())
                .orElseGet(() -> new Slug("article-" + articleId.asString().substring(0, 8)));
    }

    private void ensureUnique(ArticleId articleId, Slug slug, ArticleTitle title) {
        Predicate<Article> other = article -> !article.getId().equals(articleId);
        List<Article> others = articleStore.findAll().stream().filter(other).toList();
        if (others.stream().anyMatch(article -> article.getSlug().equals(slug))) {
            throw new ArticleAlreadyExistsException(slug);
        }
        if (others.stream().anyMatch(article -> article.getTitle().sameAs(title))) {
            throw new ArticleAlreadyExistsException(title);
        }
    }

    private static List<ArticleTerm> distinctSorted(Collection<ArticleTerm> terms) {
        // keep the name of the first occurrence when two terms share a slug
        Map<Slug, ArticleTerm> bySlug = new LinkedHashMap<>();
        terms.forEach(term -> bySlug.putIfAbsent(term.getSlug(), term));
        return bySlug.values().stream().sorted(BY_NAME).toList();
    }

    private static <T> Page<T> toPage(List<T> items, Pageable pageable) {
        if (pageable.isUnpaged()) {
            return new PageImpl<>(items, pageable, items.size());
        }
        int from = (int) Math.min(pageable.getOffset(), items.size());
        int to = Math.min(from + pageable.getPageSize(), items.size());
        return new PageImpl<>(items.subList(from, to), pageable, items.size());
    }

    // datetime-local form input only goes down to minutes; drop seconds/nanos for tidy files and stable comparisons
    private static LocalDateTime truncate(LocalDateTime dateTime) {
        return dateTime == null ? null : dateTime.truncatedTo(ChronoUnit.MINUTES);
    }

    private LocalDateTime now() {
        return LocalDateTime.now(clock).truncatedTo(ChronoUnit.SECONDS);
    }

}
