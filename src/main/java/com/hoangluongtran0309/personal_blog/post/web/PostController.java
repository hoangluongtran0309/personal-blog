package com.hoangluongtran0309.personal_blog.post.web;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hoangluongtran0309.personal_blog.post.Post;
import com.hoangluongtran0309.personal_blog.post.PostId;
import com.hoangluongtran0309.personal_blog.post.PostService;

import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest;

@Controller
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/")
    public String index(Model model,
            @SortDefault.SortDefaults({
        @SortDefault(sort = "publishDate", direction = Sort.Direction.DESC)
    }) Pageable pageable) {
        model.addAttribute("activePage", "blog");
        model.addAttribute("posts", postService.getAllPublishedPosts(pageable));
        return "posts/index";
    }

    @GetMapping("/posts/{id}")
    public String detail(@PathVariable PostId id, Model model) {
        model.addAttribute("activePage", "blog");
        model.addAttribute("post", postService.getPostById(id));
        return "posts/detail";
    }

    @GetMapping("/admin")
    public String admin(Model model,
            @SortDefault.SortDefaults({
        @SortDefault(sort = "publishDate", direction = Sort.Direction.DESC)
    }) Pageable pageable) {
        model.addAttribute("activePage", "posts");
        model.addAttribute("posts", postService.getAllPosts(pageable));
        return "admin/posts/index";
    }

    @GetMapping("/new")
    @HxRequest
    public String createPostForm(Model model) {
        model.addAttribute("editMode", EditMode.CREATE);
        model.addAttribute("post", new CreatePostFormData());
        return "admin/posts/form :: form";
    }

    @PostMapping("/posts")
    @HxRequest
    public String createPost(@Validated @ModelAttribute("post") CreatePostFormData data,
            BindingResult bindingResult,
            Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("editMode", EditMode.CREATE);
            return "admin/posts/form :: form";
        }

        Post savedPost = postService.createPost(data.toParameters());
        model.addAttribute("post", savedPost);
        return "admin/posts/item :: item";
    }

    @GetMapping("/posts/{id}/edit")
    @HxRequest
    public String editPostForm(@PathVariable PostId id, Model model) {
        Post post = postService.getPostById(id);
        model.addAttribute("editMode", EditMode.UPDATE);
        model.addAttribute("post", EditPostFormData.fromPost(post));
        return "admin/posts/form :: form";
    }

    @PostMapping("/posts/{id}")
    @HxRequest
    public String editPost(@PathVariable PostId id,
            @Validated @ModelAttribute("post") EditPostFormData data,
            BindingResult bindingResult,
            Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("editMode", EditMode.UPDATE);
            return "admin/posts/form :: form";
        }

        postService.updatePost(id, data.toParameters());
        Post updatedPost = postService.getPostById(id);
        model.addAttribute("post", updatedPost);
        return "admin/posts/item :: item";
    }

    @DeleteMapping("/posts/{id}")
    @HxRequest
    @ResponseBody
    public String deletePost(@PathVariable PostId id, Model model) {
        postService.deletePost(id);
        return "";
    }

    @GetMapping("/posts/{id}")
    @HxRequest
    public String item(@PathVariable PostId id, Model model) {
        Post post = postService.getPostById(id);
        model.addAttribute("post", post);
        return "admin/posts/item :: item";
    }

}
