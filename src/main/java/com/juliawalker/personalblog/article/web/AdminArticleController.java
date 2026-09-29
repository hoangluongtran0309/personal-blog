package com.juliawalker.personalblog.article.web;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.juliawalker.personalblog.article.Article;
import com.juliawalker.personalblog.article.ArticleAlreadyExistsException;
import com.juliawalker.personalblog.article.ArticleId;
import com.juliawalker.personalblog.article.ArticleNotFoundException;
import com.juliawalker.personalblog.article.ArticleService;
import com.juliawalker.personalblog.article.ArticleStatus;
import com.juliawalker.personalblog.infrastructure.web.Messages;
import com.juliawalker.personalblog.infrastructure.web.ResolveMessages;

/**
 * Admin section: Dashboard, Add, Edit and Delete Article. Access is restricted to
 * {@code ROLE_ADMIN} by the security filter chain ({@code /admin/**}).
 */
@Controller
@RequestMapping("/admin")
public class AdminArticleController {

    static final int PAGE_SIZE = 10;

    private static final String FORM_VIEW = "admin/articles/form";

    private final ArticleService articleService;
    private final ResolveMessages resolveMessages;

    public AdminArticleController(ArticleService articleService, ResolveMessages resolveMessages) {
        this.articleService = articleService;
        this.resolveMessages = resolveMessages;
    }

    @GetMapping
    public String dashboard(@RequestParam(defaultValue = "0") int page, Model model) {
        Page<Article> articles = articleService.getAll(PageRequest.of(Math.max(page, 0), PAGE_SIZE));
        Map<ArticleId, ArticleStatus> statuses = new LinkedHashMap<>();
        articles.forEach(article -> statuses.put(article.getId(), articleService.getStatus(article)));
        model.addAttribute("articles", articles);
        model.addAttribute("statuses", statuses);
        model.addAttribute("activePage", "dashboard");
        return "admin/dashboard";
    }

    @GetMapping("/articles/new")
    public String createForm(Model model) {
        model.addAttribute("formData", new ArticleFormData());
        return formView(model, null);
    }

    @PostMapping("/articles/new")
    public String create(
            @Validated(ArticleValidationGroupSequence.class) @ModelAttribute("formData") ArticleFormData formData,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return formView(model, null);
        }
        try {
            Article article = articleService.create(formData.toParameters());
            Messages.success(redirectAttributes,
                    resolveMessages.resolve("article.create.success", article.getTitle().asString()));
            return "redirect:/admin";
        } catch (ArticleAlreadyExistsException e) {
            rejectDuplicate(bindingResult, e);
            return formView(model, null);
        }
    }

    @GetMapping("/articles/{id}/edit")
    public String editForm(@PathVariable ArticleId id, Model model) {
        Article article = findArticle(id);
        model.addAttribute("formData", ArticleFormData.fromArticle(article));
        return formView(model, article);
    }

    @PostMapping("/articles/{id}/edit")
    public String edit(
            @PathVariable ArticleId id,
            @Validated(ArticleValidationGroupSequence.class) @ModelAttribute("formData") ArticleFormData formData,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        Article article = findArticle(id);
        if (bindingResult.hasErrors()) {
            return formView(model, article);
        }
        try {
            Article updated = articleService.update(id, formData.toParameters());
            Messages.success(redirectAttributes,
                    resolveMessages.resolve("article.edit.success", updated.getTitle().asString()));
            return "redirect:/admin";
        } catch (ArticleAlreadyExistsException e) {
            rejectDuplicate(bindingResult, e);
            return formView(model, article);
        }
    }

    @GetMapping("/articles/{id}/delete")
    public String deleteConfirmation(@PathVariable ArticleId id, Model model) {
        Article article = findArticle(id);
        model.addAttribute("article", article);
        model.addAttribute("status", articleService.getStatus(article));
        model.addAttribute("activePage", "dashboard");
        return "admin/articles/delete";
    }

    @PostMapping("/articles/{id}/delete")
    public String delete(@PathVariable ArticleId id, RedirectAttributes redirectAttributes) {
        Article article = findArticle(id);
        articleService.delete(id);
        Messages.success(redirectAttributes,
                resolveMessages.resolve("article.delete.success", article.getTitle().asString()));
        return "redirect:/admin";
    }

    private Article findArticle(ArticleId id) {
        return articleService.getById(id).orElseThrow(() -> new ArticleNotFoundException(id));
    }

    private String formView(Model model, Article article) {
        model.addAttribute("article", article);
        model.addAttribute("status", article == null ? null : articleService.getStatus(article));
        model.addAttribute("activePage", article == null ? "new" : "dashboard");
        return FORM_VIEW;
    }

    // a duplicate slug is reported on the slug field, a duplicate title on the title field
    private static void rejectDuplicate(BindingResult bindingResult, ArticleAlreadyExistsException e) {
        String field = e.getMessageKey().equals("article.error.slugAlreadyExists") ? "slug" : "title";
        bindingResult.rejectValue(field, e.getMessageKey(), e.getArgs(), null);
    }

}
