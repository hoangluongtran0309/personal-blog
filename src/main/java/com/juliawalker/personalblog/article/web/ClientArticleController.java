package com.juliawalker.personalblog.article.web;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.juliawalker.personalblog.article.Article;
import com.juliawalker.personalblog.article.ArticleService;
import com.juliawalker.personalblog.article.ArticleTerm;
import com.juliawalker.personalblog.article.Slug;
import com.juliawalker.personalblog.infrastructure.exception.NotFoundException;
import com.juliawalker.personalblog.infrastructure.markdown.MarkdownService;

/**
 * Guest section: Home (with category / tag filters) and Article pages.
 */
@Controller
public class ClientArticleController {

    static final int PAGE_SIZE = 6;

    private final ArticleService articleService;
    private final MarkdownService markdownService;

    public ClientArticleController(ArticleService articleService, MarkdownService markdownService) {
        this.articleService = articleService;
        this.markdownService = markdownService;
    }

    @GetMapping("/")
    public String home(@RequestParam(defaultValue = "0") int page, Model model) {
        Page<Article> articles = articleService.getPublished(pageRequest(page));
        return home(model, articles, "/", null, null);
    }

    @GetMapping("/category/{slug}")
    public String category(@PathVariable String slug, @RequestParam(defaultValue = "0") int page, Model model) {
        List<ArticleTerm> categories = articleService.getPublishedCategories();
        ArticleTerm category = findTerm(categories, slug, "category.error.notFound");
        Page<Article> articles = articleService.getPublishedByCategory(category.getSlug(), pageRequest(page));
        return home(model, articles, "/category/" + category.getSlug().asString(), category, null);
    }

    @GetMapping("/tag/{slug}")
    public String tag(@PathVariable String slug, @RequestParam(defaultValue = "0") int page, Model model) {
        List<ArticleTerm> tags = articleService.getPublishedTags();
        ArticleTerm tag = findTerm(tags, slug, "tag.error.notFound");
        Page<Article> articles = articleService.getPublishedByTag(tag.getSlug(), pageRequest(page));
        return home(model, articles, "/tag/" + tag.getSlug().asString(), null, tag);
    }

    @GetMapping("/articles/{slug}")
    public String article(@PathVariable String slug, Model model) {
        if (!Slug.isValid(slug)) {
            throw new NotFoundException("article.error.notFound.bySlug", slug);
        }
        Article article = articleService.getPublishedBySlug(new Slug(slug));
        model.addAttribute("article", article);
        model.addAttribute("contentHtml", markdownService.toHtml(article.getContent().asString()));
        model.addAttribute("categories", articleService.getPublishedCategories());
        model.addAttribute("activePage", "home");
        return "client/articles/detail";
    }

    private String home(Model model, Page<Article> articles, String baseUrl, ArticleTerm currentCategory,
            ArticleTerm currentTag) {
        model.addAttribute("articles", articles);
        model.addAttribute("baseUrl", baseUrl);
        model.addAttribute("currentCategory", currentCategory);
        model.addAttribute("currentTag", currentTag);
        model.addAttribute("categories", articleService.getPublishedCategories());
        model.addAttribute("tags", articleService.getPublishedTags());
        model.addAttribute("activePage", "home");
        return "client/home";
    }

    // a category/tag only exists when at least one published article uses it
    private static ArticleTerm findTerm(List<ArticleTerm> terms, String slug, String messageKey) {
        return terms.stream()
                .filter(term -> term.getSlug().asString().equals(slug))
                .findFirst()
                .orElseThrow(() -> new NotFoundException(messageKey, slug));
    }

    private static Pageable pageRequest(int page) {
        return PageRequest.of(Math.max(page, 0), PAGE_SIZE);
    }

}
