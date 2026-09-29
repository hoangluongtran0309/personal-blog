package com.juliawalker.personalblog.infrastructure.web;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/login")
public class LoginController {

    private final ResolveMessages resolveMessages;

    public LoginController(ResolveMessages resolveMessages) {
        this.resolveMessages = resolveMessages;
    }

    @GetMapping
    public String login(
            @RequestParam(required = false) String error,
            @RequestParam(required = false) String logout,
            Authentication authentication,
            Model model) {
        if (authentication != null && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)) {
            return "redirect:/admin";
        }
        if (error != null) {
            Messages.error(model, resolveMessages.resolve("login.error"));
        }
        if (logout != null) {
            Messages.success(model, resolveMessages.resolve("logout.success"));
        }
        return "admin/login";
    }

}
