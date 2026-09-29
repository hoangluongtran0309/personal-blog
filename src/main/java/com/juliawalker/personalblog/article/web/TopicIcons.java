package com.juliawalker.personalblog.article.web;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.juliawalker.personalblog.article.ArticleTerm;

/**
 * Picks a stable ionicon for a category, so each topic keeps the same icon across pages.
 * Used from templates: {@code ${T(...TopicIcons).of(category)}}.
 */
public final class TopicIcons {

    private static final List<String> ICONS = List.of(
            "server-outline",
            "rocket-outline",
            "accessibility-outline",
            "code-slash-outline",
            "cafe-outline",
            "layers-outline",
            "shield-checkmark-outline",
            "bulb-outline");

    private TopicIcons() {
    }

    // common topics get a meaningful icon; the rest are picked by hash so they stay stable across pages
    private static final Map<String, String> KEYWORDS = new LinkedHashMap<>();

    static {
        KEYWORDS.put("data", "server-outline");
        KEYWORDS.put("sql", "server-outline");
        KEYWORDS.put("perf", "rocket-outline");
        KEYWORDS.put("hieu-nang", "rocket-outline");
        KEYWORDS.put("access", "accessibility-outline");
        KEYWORDS.put("a11y", "accessibility-outline");
        KEYWORDS.put("java", "cafe-outline");
        KEYWORDS.put("spring", "leaf-outline");
        KEYWORDS.put("security", "shield-checkmark-outline");
        KEYWORDS.put("bao-mat", "shield-checkmark-outline");
        KEYWORDS.put("web", "globe-outline");
        KEYWORDS.put("devops", "construct-outline");
        KEYWORDS.put("docker", "cube-outline");
    }

    public static String of(ArticleTerm term) {
        if (term == null) {
            return "folder-outline";
        }
        String slug = term.getSlug().asString();
        return KEYWORDS.entrySet().stream()
                .filter(entry -> slug.contains(entry.getKey()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElseGet(() -> ICONS.get(Math.floorMod(slug.hashCode(), ICONS.size())));
    }

}
