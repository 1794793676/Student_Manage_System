package com.example.studentcourse.controller;

import com.example.studentcourse.config.SessionKeys;
import com.example.studentcourse.entity.User;
import com.example.studentcourse.service.UserService;
import jakarta.servlet.http.HttpSession;
import java.util.Optional;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    private static final int MIN_LOGIN_LENGTH = 6;
    private static final int MAX_LOGIN_LENGTH = 10;

    private final UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String index(HttpSession session) {
        User currentUser = (User) session.getAttribute(SessionKeys.CURRENT_USER);
        return currentUser == null ? "redirect:/login" : redirectHome(currentUser);
    }

    @GetMapping("/login")
    public String loginPage(
            @RequestParam(required = false) Boolean forbidden,
            HttpSession session,
            Model model
    ) {
        User currentUser = (User) session.getAttribute(SessionKeys.CURRENT_USER);
        if (currentUser != null && !Boolean.TRUE.equals(forbidden)) {
            return redirectHome(currentUser);
        }
        if (Boolean.TRUE.equals(forbidden)) {
            model.addAttribute("error", "无权访问目标页面，请使用对应角色账号登录");
        }
        return "login";
    }

    @PostMapping("/login")
    public String login(
            @RequestParam(defaultValue = "") String username,
            @RequestParam(defaultValue = "") String password,
            HttpSession session,
            Model model
    ) {
        String normalizedUsername = username.trim();
        String normalizedPassword = password.trim();
        String validationError = validateLoginInput(normalizedUsername, normalizedPassword);
        if (validationError != null) {
            return loginWithError(model, normalizedUsername, validationError);
        }

        Optional<User> user = userService.login(normalizedUsername, normalizedPassword);
        if (user.isEmpty()) {
            return loginWithError(model, normalizedUsername, "用户名或密码错误");
        }

        session.setAttribute(SessionKeys.CURRENT_USER, user.get());
        return redirectHome(user.get());
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    private String validateLoginInput(String username, String password) {
        if (username.isEmpty()) {
            return "用户名不能为空";
        }
        if (password.isEmpty()) {
            return "密码不能为空";
        }
        if (!isValidLoginLength(username)) {
            return "用户名长度必须为6-10位";
        }
        if (!isValidLoginLength(password)) {
            return "密码长度必须为6-10位";
        }
        return null;
    }

    private boolean isValidLoginLength(String value) {
        int length = value.length();
        return length >= MIN_LOGIN_LENGTH && length <= MAX_LOGIN_LENGTH;
    }

    private String loginWithError(Model model, String username, String error) {
        model.addAttribute("error", error);
        model.addAttribute("username", username);
        return "login";
    }

    private String redirectHome(User user) {
        if (user.isAdmin()) {
            return "redirect:/admin/home";
        }
        return "redirect:/student/home";
    }
}
