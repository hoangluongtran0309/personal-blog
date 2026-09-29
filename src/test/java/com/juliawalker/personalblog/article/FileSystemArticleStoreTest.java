package com.juliawalker.personalblog.article;

import static com.juliawalker.personalblog.article.ArticleFixtures.NOW;
import static com.juliawalker.personalblog.article.ArticleFixtures.article;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class FileSystemArticleStoreTest {

    @TempDir
    Path directory;

    @Test
    void constructor_missingDirectory_createsIt() {
        // Arrange
        Path nested = directory.resolve("a/b/articles");

        // Act
        FileSystemArticleStore store = new FileSystemArticleStore(nested);

        // Assert
        assertThat(nested).isDirectory();
        assertThat(store.findAll()).isEmpty();
    }

    @Test
    void save_newArticle_writesFileNamedById() {
        // Arrange
        FileSystemArticleStore store = new FileSystemArticleStore(directory);
        Article article = article(ArticleId.generate(), "hello", NOW);

        // Act
        Article saved = store.save(article);

        // Assert
        assertThat(directory.resolve(article.getId().asString() + ".md")).isRegularFile();
        assertThat(store.findById(article.getId())).contains(saved);
        assertThat(store.findBySlug(new Slug("hello"))).contains(saved);
    }

    @Test
    void save_thenNewStoreOnSameDirectory_reloadsArticles() {
        // Arrange
        FileSystemArticleStore store = new FileSystemArticleStore(directory);
        Article saved = store.save(article(ArticleId.generate(), "persisted", NOW));

        // Act
        FileSystemArticleStore reloaded = new FileSystemArticleStore(directory);

        // Assert
        assertThat(reloaded.findAll()).containsExactly(saved);
    }

    @Test
    void save_existingArticle_updatesFileAndSlugIndex() {
        // Arrange
        FileSystemArticleStore store = new FileSystemArticleStore(directory);
        Article saved = store.save(article(ArticleId.generate(), "old-slug", NOW));
        Article revised = saved.revise(new ArticleTitle("New"), new Slug("new-slug"), null,
                new ArticleContent("New body"), null, List.of(), null, NOW.plusHours(1));

        // Act
        Article updated = store.save(revised);

        // Assert
        assertThat(store.findBySlug(new Slug("old-slug"))).isEmpty();
        assertThat(store.findBySlug(new Slug("new-slug"))).contains(updated);
        assertThat(new FileSystemArticleStore(directory).findById(saved.getId())).contains(updated);
    }

    @Test
    void save_slugUsedByAnotherArticle_throwsArticleAlreadyExistsException() {
        // Arrange
        FileSystemArticleStore store = new FileSystemArticleStore(directory);
        store.save(article(ArticleId.generate(), "taken", NOW));

        // Act + Assert
        assertThatThrownBy(() -> store.save(article(ArticleId.generate(), "taken", NOW)))
                .isInstanceOf(ArticleAlreadyExistsException.class);
        assertThat(store.findAll()).hasSize(1);
    }

    @Test
    void save_success_leavesNoTemporaryFiles() throws IOException {
        // Arrange
        FileSystemArticleStore store = new FileSystemArticleStore(directory);

        // Act
        store.save(article(ArticleId.generate(), "clean", NOW));

        // Assert
        try (Stream<Path> files = Files.list(directory)) {
            assertThat(files).allMatch(file -> file.getFileName().toString().endsWith(".md")
                    && !file.getFileName().toString().startsWith("."));
        }
    }

    @Test
    void delete_existingArticle_removesFileAndIndex() {
        // Arrange
        FileSystemArticleStore store = new FileSystemArticleStore(directory);
        Article saved = store.save(article(ArticleId.generate(), "doomed", NOW));

        // Act
        boolean deleted = store.delete(saved.getId());

        // Assert
        assertThat(deleted).isTrue();
        assertThat(directory.resolve(saved.getId().asString() + ".md")).doesNotExist();
        assertThat(store.findById(saved.getId())).isEmpty();
        assertThat(store.findBySlug(new Slug("doomed"))).isEmpty();
    }

    @Test
    void delete_unknownArticle_returnsFalse() {
        // Arrange
        FileSystemArticleStore store = new FileSystemArticleStore(directory);

        // Act + Assert
        assertThat(store.delete(ArticleId.generate())).isFalse();
    }

    @Test
    void constructor_malformedFile_skipsItAndLoadsOthers() throws IOException {
        // Arrange
        Article valid = new FileSystemArticleStore(directory).save(article(ArticleId.generate(), "valid", NOW));
        Files.writeString(directory.resolve(ArticleId.generate().asString() + ".md"), "not an article",
                StandardCharsets.UTF_8);

        // Act
        FileSystemArticleStore store = new FileSystemArticleStore(directory);

        // Assert
        assertThat(store.findAll()).containsExactly(valid);
    }

    @Test
    void constructor_fileNameNotMatchingId_skipsIt() throws IOException {
        // Arrange
        Article saved = new FileSystemArticleStore(directory).save(article(ArticleId.generate(), "moved", NOW));
        Files.move(directory.resolve(saved.getId().asString() + ".md"), directory.resolve("renamed.md"));

        // Act
        FileSystemArticleStore store = new FileSystemArticleStore(directory);

        // Assert
        assertThat(store.findAll()).isEmpty();
    }

    @Test
    void constructor_twoFilesWithSameSlug_keepsOnlyOne() throws IOException {
        // Arrange
        ArticleFileFormat fileFormat = new ArticleFileFormat();
        Article first = article(ArticleId.generate(), "same", NOW);
        Article second = article(ArticleId.generate(), "same", NOW);
        Files.writeString(directory.resolve(first.getId().asString() + ".md"), fileFormat.write(first));
        Files.writeString(directory.resolve(second.getId().asString() + ".md"), fileFormat.write(second));

        // Act
        FileSystemArticleStore store = new FileSystemArticleStore(directory);

        // Assert
        assertThat(store.findAll()).hasSize(1);
    }

    @Test
    void constructor_ignoresHiddenAndNonMarkdownFiles() throws IOException {
        // Arrange
        Files.writeString(directory.resolve(".leftover.md.tmp"), "partial");
        Files.writeString(directory.resolve("notes.txt"), "text");

        // Act
        FileSystemArticleStore store = new FileSystemArticleStore(directory);

        // Assert
        assertThat(store.findAll()).isEmpty();
    }

    @Test
    void save_concurrentCreatesWithDistinctSlugs_storesAll() throws Exception {
        // Arrange
        FileSystemArticleStore store = new FileSystemArticleStore(directory);
        ExecutorService executor = Executors.newFixedThreadPool(8);
        List<Future<Article>> futures = new ArrayList<>();

        // Act
        try {
            for (int i = 0; i < 40; i++) {
                String slug = "concurrent-" + i;
                futures.add(executor.submit(() -> store.save(article(ArticleId.generate(), slug, NOW))));
            }
            for (Future<Article> future : futures) {
                future.get();
            }
        } finally {
            executor.shutdownNow();
        }

        // Assert
        assertThat(store.findAll()).hasSize(40);
        assertThat(new FileSystemArticleStore(directory).findAll()).hasSize(40);
    }

}
