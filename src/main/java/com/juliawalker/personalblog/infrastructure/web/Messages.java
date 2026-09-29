package com.juliawalker.personalblog.infrastructure.web;

import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

public final class Messages {

    public static final String SUCCESS = "successMessage";
    public static final String ERROR = "errorMessage";
    public static final String WARNING = "warningMessage";
    public static final String INFO = "infoMessage";

    private Messages() {

    }

    public static void success(RedirectAttributes redirectAttributes, String message) {
        redirectAttributes.addFlashAttribute(SUCCESS, message);
    }

    public static void error(RedirectAttributes redirectAttributes, String message) {
        redirectAttributes.addFlashAttribute(ERROR, message);
    }

    public static void warning(RedirectAttributes redirectAttributes, String message) {
        redirectAttributes.addFlashAttribute(WARNING, message);
    }

    public static void info(RedirectAttributes redirectAttributes, String message) {
        redirectAttributes.addFlashAttribute(INFO, message);
    }

    public static void success(Model model, String message) {
        model.addAttribute(SUCCESS, message);
    }

    public static void error(Model model, String message) {
        model.addAttribute(ERROR, message);
    }

    public static void warning(Model model, String message) {
        model.addAttribute(WARNING, message);
    }

    public static void info(Model model, String message) {
        model.addAttribute(INFO, message);
    }

}