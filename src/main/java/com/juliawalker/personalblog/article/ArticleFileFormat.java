package com.juliawalker.personalblog.article;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.representer.Representer;

/**
 * Reads and writes the article file format (see ROADMAP.md section 2): YAML front matter
 * between two {@code ---} lines, followed by the Markdown body.
 */
final class ArticleFileFormat {

    static final String FILE_EXTENSION = ".md";

    private static final String DELIMITER = "---";
    private static final String OPENING = DELIMITER + "\n";
    private static final String CLOSING = "\n" + DELIMITER + "\n";

    private final Yaml yaml;

    ArticleFileFormat() {
        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        dumperOptions.setIndent(2);
        dumperOptions.setAllowUnicode(true);
        dumperOptions.setSplitLines(false);
        // SafeConstructor: an article file must not be able to instantiate arbitrary Java objects
        this.yaml = new Yaml(new SafeConstructor(new LoaderOptions()), new Representer(dumperOptions),
                dumperOptions);
    }

    String write(Article article) {
        Map<String, Object> frontMatter = new LinkedHashMap<>();
        frontMatter.put("id", article.getId().asString());
        frontMatter.put("title", article.getTitle().asString());
        frontMatter.put("slug", article.getSlug().asString());
        if (article.getSummary() != null) {
            frontMatter.put("summary", article.getSummary().asString());
        }
        if (article.getCoverImage() != null) {
            frontMatter.put("coverImage", article.getCoverImage().asString());
        }
        if (article.getCategory() != null) {
            frontMatter.put("category", article.getCategory().getName());
        }
        frontMatter.put("tags", article.getTags().stream().map(ArticleTerm::getName).toList());
        frontMatter.put("publishedAt", article.getPublishedAt() == null ? null : article.getPublishedAt().toString());
        frontMatter.put("createdAt", article.getCreatedAt().toString());
        frontMatter.put("updatedAt", article.getUpdatedAt().toString());
        return OPENING + yaml.dump(frontMatter) + DELIMITER + "\n" + article.getContent().asString();
    }

    /**
     * @throws IllegalArgumentException when the text is not a valid article file
     */
    Article read(String text) {
        String normalized = text.replace("\r\n", "\n");
        if (!normalized.startsWith(OPENING)) {
            throw new IllegalArgumentException("missing opening front matter delimiter");
        }
        int closing = normalized.indexOf(CLOSING, OPENING.length() - 1);
        String body;
        if (closing >= 0) {
            body = normalized.substring(closing + CLOSING.length());
        } else if (normalized.endsWith("\n" + DELIMITER)) {
            closing = normalized.length() - DELIMITER.length() - 1;
            body = "";
        } else {
            throw new IllegalArgumentException("missing closing front matter delimiter");
        }
        String frontMatterText = normalized.substring(OPENING.length(), Math.max(OPENING.length(), closing + 1));
        Object loaded = yaml.load(frontMatterText);
        if (!(loaded instanceof Map<?, ?> frontMatter)) {
            throw new IllegalArgumentException("front matter is not a mapping");
        }
        return new Article(
                ArticleId.fromString(requiredString(frontMatter, "id")),
                new ArticleTitle(requiredString(frontMatter, "title")),
                new Slug(requiredString(frontMatter, "slug")),
                optionalString(frontMatter, "summary") == null ? null
                        : new ArticleSummary(optionalString(frontMatter, "summary")),
                new ArticleContent(body),
                optionalString(frontMatter, "category") == null ? null
                        : ArticleTerm.of(optionalString(frontMatter, "category")),
                tags(frontMatter.get("tags")),
                optionalDateTime(frontMatter, "publishedAt"),
                requiredDateTime(frontMatter, "createdAt"),
                requiredDateTime(frontMatter, "updatedAt"),
                optionalString(frontMatter, "coverImage") == null ? null
                        : new ArticleCoverImage(optionalString(frontMatter, "coverImage")));
    }

    private static String requiredString(Map<?, ?> map, String key) {
        String value = optionalString(map, key);
        if (value == null) {
            throw new IllegalArgumentException("missing required field: " + key);
        }
        return value;
    }

    private static String optionalString(Map<?, ?> map, String key) {
        Object value = map.get(key);
        if (value == null) {
            return null;
        }
        String text = value.toString();
        return text.isBlank() ? null : text;
    }

    private static LocalDateTime requiredDateTime(Map<?, ?> map, String key) {
        LocalDateTime value = optionalDateTime(map, key);
        if (value == null) {
            throw new IllegalArgumentException("missing required field: " + key);
        }
        return value;
    }

    // SnakeYAML turns "2026-09-28T10:00:00" (with seconds, unquoted) into a Date in UTC by itself
    private static LocalDateTime optionalDateTime(Map<?, ?> map, String key) {
        Object value = map.get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof Date date) {
            return LocalDateTime.ofInstant(date.toInstant(), ZoneOffset.UTC);
        }
        String text = value.toString();
        return text.isBlank() ? null : LocalDateTime.parse(text.strip());
    }

    private static List<ArticleTerm> tags(Object value) {
        if (value == null) {
            return List.of();
        }
        if (!(value instanceof List<?> list)) {
            throw new IllegalArgumentException("tags must be a list");
        }
        List<ArticleTerm> tags = new ArrayList<>();
        for (Object item : list) {
            if (item != null && !item.toString().isBlank()) {
                tags.add(ArticleTerm.of(item.toString()));
            }
        }
        return tags;
    }

}
