package com.hoangluongtran0309.personal_blog.user.web.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.hoangluongtran0309.personal_blog.infrastructure.web.EditMode;
import com.hoangluongtran0309.personal_blog.user.application.dto.UserFilter;
import com.hoangluongtran0309.personal_blog.user.application.dto.UserView;
import com.hoangluongtran0309.personal_blog.user.application.usecase.ChangePasswordUseCase;
import com.hoangluongtran0309.personal_blog.user.application.usecase.CreateUserUseCase;
import com.hoangluongtran0309.personal_blog.user.application.usecase.DeleteUserUseCase;
import com.hoangluongtran0309.personal_blog.user.application.usecase.GetUserQueryUseCase;
import com.hoangluongtran0309.personal_blog.user.application.usecase.UpdateUserUseCase;
import com.hoangluongtran0309.personal_blog.user.web.PageMetadata;
import com.hoangluongtran0309.personal_blog.user.web.form.ChangePasswordFormData;
import com.hoangluongtran0309.personal_blog.user.web.form.CreateUserFormData;
import com.hoangluongtran0309.personal_blog.user.web.form.UpdateUserFormData;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class UserController {

    private final GetUserQueryUseCase getUserQueryUseCase;
    private final CreateUserUseCase createUserUseCase;
    private final UpdateUserUseCase updateUserUseCase;
    private final DeleteUserUseCase deleteUserUseCase;
    private final ChangePasswordUseCase changePasswordUseCase;

    @GetMapping
    public String index(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "firstname") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model,
            @RequestHeader(value = "HX-Request", required = false) String hxRequest) {

        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Page<UserView> users = getUserQueryUseCase
                .getAllUsers(new UserFilter(search), PageRequest.of(page, size, sort));

        model.addAttribute("users", users);
        model.addAttribute("pageMeta", PageMetadata.of(users));

        model.addAttribute("searchValue", search);
        model.addAttribute("currentSort", sortBy);
        model.addAttribute("currentDir", sortDir);
        model.addAttribute("actionUrl", "/admin/users");
        model.addAttribute("resetUrl", "/admin/users");
        model.addAttribute("placeholder", "Search by name, email or username…");
        model.addAttribute("sortOptions", List.of(
                Map.of("value", "firstname", "label", "First Name"),
                Map.of("value", "lastname", "label", "Last Name"),
                Map.of("value", "email", "label", "Email"),
                Map.of("value", "username", "label", "Username")));

        model.addAttribute("extraParams", buildExtraParams(search, sortBy, sortDir));
        model.addAttribute("baseUrl", "/admin/users");

        return hxRequest != null
                ? "admin/user/index :: page-content"
                : "admin/user/index";
    }

    @GetMapping("/{id}")
    public String detail(
            @PathVariable UUID id,
            Model model,
            @RequestHeader(value = "HX-Request", required = false) String hxRequest) {

        UserView user = getUserQueryUseCase.getUserById(id);
        model.addAttribute("user", user);

        return hxRequest != null
                ? "admin/user/detail :: page-content"
                : "admin/user/detail";
    }

    @GetMapping("/new")
    public String createUserForm(
            Model model,
            @RequestHeader(value = "HX-Request", required = false) String hxRequest) {

        model.addAttribute("editMode", EditMode.CREATE);
        model.addAttribute("form", new CreateUserFormData());

        return hxRequest != null
                ? "admin/user/form :: page-content"
                : "admin/user/form";
    }

    @PostMapping("/new")
    public String doCreateUser(
            @Valid @ModelAttribute("form") CreateUserFormData form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes,
            @RequestHeader(value = "HX-Request", required = false) String hxRequest) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("editMode", EditMode.CREATE);
            return hxRequest != null ? "admin/user/form :: page-content" : "admin/user/form";
        }

        try {
            createUserUseCase.createUser(form.toRequest());

            if (hxRequest != null) {
                return loadUsersAndReturnIndex(model, null, "firstname", "asc", 0, 10);
            }
            redirectAttributes.addFlashAttribute("successMessage", "User created successfully!");
            return "redirect:/admin/users";
        } catch (Exception e) {
            model.addAttribute("editMode", EditMode.CREATE);
            model.addAttribute("errorMessage", e.getMessage());
            return hxRequest != null ? "admin/user/form :: page-content" : "admin/user/form";
        }
    }

    @GetMapping("/{id}/update")
    public String updateUserForm(
            @PathVariable UUID id,
            Model model,
            @RequestHeader(value = "HX-Request", required = false) String hxRequest) {

        UserView user = getUserQueryUseCase.getUserById(id);
        model.addAttribute("editMode", EditMode.UPDATE);
        model.addAttribute("form", UpdateUserFormData.fromData(user));

        return hxRequest != null
                ? "admin/user/form :: page-content"
                : "admin/user/form";
    }

    @PostMapping("/{id}/update")
    public String doUpdateUser(
            @PathVariable UUID id,
            @Valid @ModelAttribute("form") UpdateUserFormData form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes,
            @RequestHeader(value = "HX-Request", required = false) String hxRequest) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("editMode", EditMode.UPDATE);
            return hxRequest != null ? "admin/user/form :: page-content" : "admin/user/form";
        }

        try {
            updateUserUseCase.update(id, form.toRequest());

            if (hxRequest != null) {
                return loadUsersAndReturnIndex(model, null, "firstname", "asc", 0, 10);
            }
            redirectAttributes.addFlashAttribute("successMessage", "User updated successfully!");
            return "redirect:/admin/users/" + id;
        } catch (Exception e) {
            model.addAttribute("editMode", EditMode.UPDATE);
            model.addAttribute("errorMessage", e.getMessage());
            return hxRequest != null ? "admin/user/form :: page-content" : "admin/user/form";
        }
    }

    @PostMapping("/{id}/delete")
    public String doDeleteUser(
            @PathVariable UUID id,
            Model model,
            RedirectAttributes redirectAttributes,
            @RequestHeader(value = "HX-Request", required = false) String hxRequest) {

        try {
            deleteUserUseCase.delete(id);

            if (hxRequest != null) {
                return loadUsersAndReturnIndex(model, null, "firstname", "asc", 0, 10);
            }
            redirectAttributes.addFlashAttribute("successMessage", "User deleted successfully!");
        } catch (Exception e) {
            if (hxRequest != null) {
                model.addAttribute("errorMessage", e.getMessage());
                return loadUsersAndReturnIndex(model, null, "firstname", "asc", 0, 10);
            }
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/admin/users";
    }

    @GetMapping("/{id}/change-password")
    public String changePasswordForm(
            @PathVariable UUID id,
            Model model,
            @RequestHeader(value = "HX-Request", required = false) String hxRequest) {

        model.addAttribute("userId", id);
        model.addAttribute("form", new ChangePasswordFormData());

        return hxRequest != null
                ? "admin/user/change-password :: page-content"
                : "admin/user/change-password";
    }

    @PostMapping("/{id}/change-password")
    public String doChangePassword(
            @PathVariable UUID id,
            @Valid @ModelAttribute("form") ChangePasswordFormData form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("userId", id);
            return "admin/user/change-password";
        }

        try {
            changePasswordUseCase.change(id, form.toRequest());
            redirectAttributes.addFlashAttribute("successMessage", "Password changed successfully!");
            return "redirect:/admin/users/" + id;
        } catch (Exception e) {
            model.addAttribute("userId", id);
            model.addAttribute("errorMessage", e.getMessage());
            return "admin/user/change-password";
        }
    }

    private String buildExtraParams(String search, String sortBy, String sortDir) {
        StringBuilder sb = new StringBuilder();
        if (search != null && !search.isBlank())
            sb.append("&search=").append(search);
        if (sortBy != null && !sortBy.isBlank())
            sb.append("&sortBy=").append(sortBy);
        if (sortDir != null && !sortDir.isBlank())
            sb.append("&sortDir=").append(sortDir);
        return sb.toString();
    }

    private String loadUsersAndReturnIndex(Model model, String search, String sortBy, String sortDir, int page,
            int size) {
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Page<UserView> users = getUserQueryUseCase
                .getAllUsers(new UserFilter(search), PageRequest.of(page, size, sort));

        model.addAttribute("users", users);
        model.addAttribute("pageMeta", PageMetadata.of(users));
        model.addAttribute("searchValue", search);
        model.addAttribute("currentSort", sortBy);
        model.addAttribute("currentDir", sortDir);
        model.addAttribute("actionUrl", "/admin/users");
        model.addAttribute("resetUrl", "/admin/users");
        model.addAttribute("placeholder", "Search by name, email or username…");
        model.addAttribute("sortOptions", List.of(
                Map.of("value", "firstname", "label", "First Name"),
                Map.of("value", "lastname", "label", "Last Name"),
                Map.of("value", "email", "label", "Email"),
                Map.of("value", "username", "label", "Username")));
        model.addAttribute("extraParams", buildExtraParams(search, sortBy, sortDir));
        model.addAttribute("baseUrl", "/admin/users");
        model.addAttribute("successMessage", "Operation completed successfully!");

        return "admin/user/index :: page-content";

    }

}